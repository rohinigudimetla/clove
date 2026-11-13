---
status: accepted
date: 2026-11-13
decision-makers: Rohini Gudimetla
---

# ADR-0001: Store jobs in a database, not a message broker

## Context and Problem Statement

A task or work can take an unknown amount of time. The requester can choose to
accept not knowing when it finishes as long as it is *guaranteed* to finish. To
handle this asynchronously, Clove accepts each task as a job, returns an
immediate receipt, and executes the work later when a worker (the part of Clove
that runs the jobs) becomes available. Between submission and execution, every
job record must be stored somewhere durable so no tasks are lost during crashes
or restarts, and queryable so the system can inspect job state and history
afterward.

## Decision Drivers

* Job records must remain intact across application restarts, deployments, and
  unexpected crashes without losing execution history.
* A job's history is queryable and persistent.

## Considered Options

* A list in Java memory
* Redis
* A message broker (e.g. Kafka, RabbitMQ, Amazon SQS)
* A database

## Decision Outcome

Chosen option: "A database", because the jobs stored are durable and queryable.

We will store every job in a database instead of a message broker such as Kafka
or RabbitMQ. Workers find their next job by querying the database.

### Consequences

* Good, because the dashboard reads job status, errors and attempt counts from
  the same table the workers use.
* Good, because the database is the only service Clove needs to run.
* Bad, because workers have to poll. A new job can wait up to 2 seconds
  (picked, not measured) before a worker sees it, and workers keep querying
  when nothing is waiting.
* Bad, because Clove has to stop two workers from claiming the same job. A
  broker does this for you. Here the claim query does it (ADR-0004).
* Bad, because throughput is far below Kafka's. LinkedIn benchmarked Kafka at
  2 million writes per second; published Postgres queues reach hundreds to
  tens of thousands of jobs per second. Clove as configured claims at most
  about 2 jobs per second (4 workers, 2-second polls, both picked). That is
  acceptable at Clove's scale.
* Bad, because finished jobs are never deleted, so the table keeps growing. We
  have not decided on a retention policy yet.

### Confirmation

* `V1__create_jobs_table.sql` creates the `jobs` table, and `JobRepository`
  reads and writes it. `pom.xml` contains no message broker client.
* Manual check: stop Clove while a job is running, start it again, and the
  job's row is still in `jobs`. Not yet run.

## Pros and Cons of the Options

### A list in Java memory

* Good, because there is no operational overhead.
* Bad, because it is volatile: all job state is lost during every restart,
  deployment or crash.

### Redis

* Good, because established job queues such as BullMQ and Sidekiq use it to
  store their jobs.
* Bad, because Redis keeps jobs in memory and writes them to disk later on,
  so a crash in between loses the most recent jobs.

### A message broker

* Good, because its jobs survive a crash.
* Bad, because it deletes each message once a worker takes it, so a job's
  history cannot be queried later.
* Bad, because Clove would still need a database beside it to keep that history.

## More Information

Decided in October 2026, recorded here on 2026-11-13.