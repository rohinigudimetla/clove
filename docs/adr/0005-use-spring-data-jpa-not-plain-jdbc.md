---
status: accepted
date: 2026-11-17
decision-makers: Rohini Gudimetla
---

# ADR-0005: Use Spring Data JPA, not plain JDBC

## Context and Problem Statement
In ADR-0007, we chose Spring Boot, so Java will be used to
read and write the jobs table.
Each row that is read by a worker becomes a `Job` object.
Every column has to be copied into a matching field.
The jobs table will gain more columns as Clove is built.
We need to choose how the Java code
will turn rows of the jobs table into `Job` objects, and `Job` objects back into rows.

## Decision Drivers

* Adding a column to the `jobs` table should change one place in the code, for both reading and writing a job.


## Considered Options
* Spring Data JPA, with Hibernate
* Plain JDBC

## Decision Outcome

Chosen option: "Spring Data JPA, with Hibernate", because every time we add a
column to the `jobs` table, we only need to change one part of the code, for both reading and writing a job.

The `Job` class maps each column of the `jobs` table to a field,
and JPA fills in those fields for every query in `JobRepository`.
`save` builds the `INSERT` and `UPDATE` statements from the same fields.
A new column means a new field in `Job.java`, and no query method changes.

### Consequences

* Good, because `JobRepository` is an interface that Spring Data implements, so standard methods such as `save` and `findById` need no handwritten SQL.
* Bad, because Hibernate writes the SQL for those methods, so it never appears in the code. Seeing what runs against the database means turning on `spring.jpa.show-sql`.

### Confirmation

* Code review: `Job.java` is an `@Entity` with one field for each column of the `jobs` table.
* Code review: no class implements `JobRepository`. Spring Data builds the implementation.
* Not yet run: add a column to the `jobs` table in a new migration and a matching field to `Job.java`, then check that `save` writes the new value and `findById` reads it back with no other code changes.

## Pros and Cons of the Options

### Plain JDBC

* Good, because every SQL statement is written out in full, so the code shows exactly what runs against the database.
* Bad, because adding a column to the `jobs` table changes more than one place in the code: the row-mapping code that reads a job, and every `INSERT` and `UPDATE` statement that writes one.
## More Information

Decided in October 2026, recorded here on 2026-11-17.