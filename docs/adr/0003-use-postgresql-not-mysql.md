---
status: accepted
date: 2026-11-17
decision-makers: Rohini Gudimetla
---

# ADR-0003: Use PostgreSQL, not MySQL

## Context and Problem Statement
In ADR-0002, we decided to use a relational database for the jobs table,
but did not decide which one. Several workers will claim pending jobs from
that table at the same time. Each claim has to find a pending job,
mark it as "processing" and give the worker the job's details,
without two workers ever getting the same job.
In order to meet these requirements, we need to choose
the relational database that will store the jobs table.

## Decision Drivers
* A worker should pick up a job within a single atomic statement: finding the job,
  marking the job as "processing", and getting back the job details from the database.

## Considered Options
* PostgreSQL
* MySQL

## Decision Outcome

Chosen option: "PostgreSQL", because it allows workers to pick up a job in a single atomic statement.

The jobs table is stored in PostgreSQL.
A worker claims a job with one `UPDATE` statement
that finds a pending job, marks it as "processing"
and sends the job's details back to the worker through `RETURNING`.

### Consequences

* Good, because Flyway runs each migration in one transaction, and PostgreSQL can roll back table changes inside a transaction, so a migration that fails halfway leaves the `jobs` table as it was.
* Bad, because the job claim in `claimNext` uses PostgreSQL-specific SQL, so moving Clove to another database later would mean rewriting the job claim.

### Confirmation

* Code review: the `spring.datasource.url` setting starts with `jdbc:postgresql:`.
* Code review: `JobRepository.claimNext` is one native `UPDATE` statement that ends with `RETURNING *`.
* Not yet run: start Clove against PostgreSQL, call `claimNext`, and check that it gives back a `Job` whose status is "processing".

## Pros and Cons of the Options

### MySQL

* Good, because MySQL 8.0 supports `FOR UPDATE SKIP LOCKED`,
  so workers claiming jobs at the same time would still get
  different jobs without waiting for each other.
* Bad, because MySQL has no `RETURNING` clause, so a worker
  cannot pick up a job within a single atomic statement. Claiming a job takes
  a locking `SELECT` and a separate `UPDATE` inside one transaction.

## More Information

Decided in October 2026, recorded here on 2026-11-17. The single-statement driver was worked out on 2026-11-11, after PostgreSQL had already been picked.