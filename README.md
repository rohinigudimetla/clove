# Clove

Clove is a self-hosted background job processing server, built on Spring Boot and PostgreSQL. It runs slow tasks in the background, so your app can respond right away.

Clove is still in development. I explain where it stands in [A note to readers](#a-note-to-readers), further down this page.

## What is Clove?

Clove is built for slow work like data exports and reports. Your app sends it a task and goes back to answering requests. Clove does the work in the background, and a dashboard shows how each task is going.

## Features

- Your app sends Clove a slow task and moves on. Clove does the work in the background.
- A task is still there after a crash or a restart.
- Clove runs several tasks at the same time.
- Clove runs long tasks and quick tasks separately, so it can start a quick task even while a big export is running.
- Clove retries a failed task, up to the number of tries you set for it.
- A dashboard shows each task's status, how far along it is, and the last error it hit.
- You write the code for your own kinds of tasks, and Clove runs them.
- Every request needs your API key.

## A note to readers

> *Clove is a work in progress. I'm building it in the open, and the features above are where it's headed. Right now, only the foundation runs: Clove starts up, creates its database tables, and fills the demo shop's table with 400,000 orders. I'll keep this note up to date as more of it works.*
>
> *The first demo is in progress. It uses an invented coffee shop with 400,000 past orders. Someone at the shop starts an export, and you can watch the export move from queued, to running, to finished.*
>
> *If you plan to try Clove, here's what to expect:*
>
> - Clove suits tasks that take seconds or minutes, at a steady pace of tasks rather than thousands a second.
> - Clove checks for new work on a timer, so a new task can take a couple of seconds to start.
> - In rare cases a task can run twice, for example when a server freezes partway through and then recovers. Write your tasks so it's safe to run them twice.
> - You can cancel a task while it's still waiting to start. Once it starts, it runs to the end.
> - Clove keeps every finished task in your database, along with its status and its last error.
>
> *Thanks for reading,*\
> *Rohini*

## Architecture decisions

I wrote up each design choice behind Clove, along with the options I weighed against it.

| ADR | Decision |
|---|---|
| [ADR-0001](docs/adr/0001-store-jobs-in-a-database-not-a-message-broker.md) | [Store jobs in a database, not a message broker](docs/adr/0001-store-jobs-in-a-database-not-a-message-broker.md) |
| [ADR-0002](docs/adr/0002-use-a-relational-database-not-a-document-store.md) | [Use a relational database, not a document store](docs/adr/0002-use-a-relational-database-not-a-document-store.md) |
| [ADR-0003](docs/adr/0003-use-postgresql-not-mysql.md) | [Use PostgreSQL, not MySQL](docs/adr/0003-use-postgresql-not-mysql.md) |
| [ADR-0004](docs/adr/0004-claim-jobs-with-database-row-locks.md) | [Claim jobs with database row locks](docs/adr/0004-claim-jobs-with-database-row-locks.md) |
| [ADR-0005](docs/adr/0005-use-spring-data-jpa-not-plain-jdbc.md) | [Use Spring Data JPA, not plain JDBC](docs/adr/0005-use-spring-data-jpa-not-plain-jdbc.md) |
| [ADR-0006](docs/adr/0006-use-flyway-not-liquibase.md) | [Use Flyway, not Liquibase](docs/adr/0006-use-flyway-not-liquibase.md) |
| [ADR-0007](docs/adr/0007-use-spring-boot-not-go.md) | [Use Spring Boot, not Go](docs/adr/0007-use-spring-boot-not-go.md) |

## Built with

Clove is built with Java, Spring Boot, PostgreSQL, Spring Data JPA with Hibernate, and Flyway.