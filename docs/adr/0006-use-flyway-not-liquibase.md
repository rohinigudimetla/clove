---
status: accepted
date: 2026-11-17
decision-makers: Rohini Gudimetla
---

# ADR-0006: Use Flyway, not Liquibase

## Context and Problem Statement

The jobs table's columns will change as Clove is built,
so a migration tool has to apply each change to the database once
and in a specific order.
Spring Boot supports Flyway and Liquibase as migration tools.
We need to choose which migration tool to use for Clove.

## Decision Drivers

* The migration tool should handle
  Clove's known use cases with minimal overhead,
  and carry no features that go unused.

## Considered Options
* Flyway
* Liquibase

## Decision Outcome

Chosen option: "Flyway", because it handles Clove's known use cases, plain SQL files that create and change tables in one PostgreSQL database, with minimal overhead and no features that go unused.

Each change to the `jobs` table is a plain SQL file
in `src/main/resources/db/migration`, named `V<number>__<description>.sql`.
When the app starts,
Spring Boot runs any new files through Flyway in version order.

### Consequences

* Good, because Flyway records every migration it has run in the `flyway_schema_history` table, so the database itself shows which versions it is on.
* Bad, because Flyway's free edition has no undo command, so reversing a migration means writing a new migration that undoes it by hand.

### Confirmation

* Code review: every file in `src/main/resources/db/migration` is plain SQL named `V<number>__<description>.sql`.
* Not yet run: start the app, then check that `flyway_schema_history` lists V1, V2 and V3, each with `success` set to true.

## Pros and Cons of the Options

### Liquibase

* Good, because changelogs can be written in SQL, XML, YAML or JSON, and the XML, YAML and JSON formats generate database-specific SQL, so one changelog can run against several kinds of database.
* Bad, because Clove's known use cases are plain SQL files against one PostgreSQL database, so the database-independent formats go unused, and even a plain SQL file needs a `--liquibase formatted sql` first line and a `--changeset author:id` line for each change.
## More Information

Decided in October 2026, recorded here on 2026-11-17.