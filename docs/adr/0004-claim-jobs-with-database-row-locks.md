---
status: accepted
date: 2026-11-17
decision-makers: Rohini Gudimetla
---

# ADR-0004: Claim jobs with database row locks

## Context and Problem Statement

In ADR-0003, we chose PostgreSQL to store the jobs table.
Several workers will claim pending jobs from that table
at the same time. If two workers read the same pending
job before either one marks it as "processing",
then both workers will run it. We need a way for workers to claim jobs
so that each pending job goes to only one worker at a time.

## Decision Drivers

* The concurrency control should cover any entity
  making changes to the `jobs` table.
* The workers claiming jobs at the same time should get
  different jobs without having to wait for another worker or retry a job claim.
* There should not be a single point of failure like a single thread,
  a process or service that every worker's job claim depends on.
* Clove should show several workers claiming jobs from the same table
  at the same time.

## Considered Options

* A lock in Java
* Database row locks
* Compare-and-swap
* A single claimer thread

## Decision Outcome

Chosen option: "Database row locks", because PostgreSQL
enforces them on every connection that can change the `jobs` table.
Workers claiming jobs at the same time each get a different job
without waiting for another worker or retrying a job claim.
No single thread or service
has to handle every job claim. It also makes it easy to show
several workers claiming jobs from the same table at once.

When a worker claims a job, it locks that job's row with `FOR UPDATE SKIP LOCKED`.
Other workers skip the locked row instead of waiting for it.

### Consequences

* Good, because the claim is one statement, so the row lock lasts only as long as that statement. A slow job does not hold a database lock while it runs.
* Bad, because the row lock ends when the claim statement ends, so a worker that crashes after claiming a job leaves that job marked "processing". A separate sweep has to find such jobs and reset them.

### Confirmation

* Code review: the `SELECT` inside `JobRepository.claimNext` ends with `FOR UPDATE SKIP LOCKED`.
* Code review: no worker calls `claimNext` inside a transaction that also runs the job.
* Not yet run: start several workers that call `claimNext` at the same moment against a table of pending jobs, and check that no job ID goes to two workers.

## Pros and Cons of the Options

### A lock in Java

* Good, because `synchronized` and `ReentrantLock` are built into Java,
  so threads in one Clove process can take turns claiming jobs with
  no database feature needed.
* Bad, because a Java lock only covers threads inside one JVM. A second Clove instance, or any other connection changing the `jobs` table, is not covered, and a worker waits for the lock while another worker holds it.

### Compare-and-swap

* Good, because no lock is held. The check happens inside the `UPDATE` statement, so it covers any connection changing the `jobs` table.
* Bad, because when two workers pick the same job, the second worker's `UPDATE` changes zero rows, and that worker has to retry its job claim on another pending job. The more workers there are, the more often job claims have to be retried.

### A single claimer thread

* Good, because only one thread reads pending jobs and hands them out, so two workers can never get the same job, and no locks are needed.
* Bad, because every worker's job claim depends on that one thread. If the thread stops, no worker gets a job, which makes it a single point of failure. It also means several workers can never be shown claiming jobs at the same moment.

## More Information

Decided in October 2026, recorded here on 2026-11-17.