---
status: accepted
date: 2026-11-16
decision-makers: Rohini Gudimetla
---

# ADR-0002: Use a relational database, not a document store

## Context and Problem Statement

In ADR-0001, we decided that Clove stores every job in a database, and that workers
find their next job by querying it. Databases differ in how they store a
record: some give every record the same fixed set of fields, and others allow variations of the record's fields. 
Clove needs every job to have all of its fields, and several workers ask the database for the next
waiting job at the same moment. In order to meet both needs, we need to choose the kind of database Clove needs.

## Decision Drivers

* Each worker gets a different waiting job on its first try, however many
  workers ask at the same time.
* The database checks every job for missing fields from the start, without
  extra rules to write.


## Considered Options
* A document store (like MongoDB)
* A relational database (like PostgreSQL or MySQL)


## Decision Outcome
Chosen option: "A relational database", because workers can claim jobs at
the same time without colliding, and every job is checked for missing
fields from the beginning.

### Consequences
* Bad, because adding a field to every job means changing the database's
  definition of a job first.

### Confirmation
* `V1__create_jobs_table.sql` declares the `jobs` table with `NOT NULL` on
  every required field, and `JobRepository` claims each job in one statement
  (ADR-0004).
* Manual check: insert a job with no `job_type` into `jobs`, and the database
  refuses it. Not yet run.

## Pros and Cons of the Options
### A document store (like MongoDB)

* Good, because a change made to a single record is atomic, so a job can
  be claimed by exactly one worker in a single call.
* Bad, because a job submitted with missing fields would still be 
stored without explicit schema validation. 
* Bad, because workers, asking for the next waiting job at the
  same time aim for the same job. One takes it and the others must search
  again, and these collisions grow as workers are added.

## More Information

Decided in October 2026, recorded here on 2026-11-16.