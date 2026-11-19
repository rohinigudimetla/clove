---
status: accepted
date: 2026-11-17
decision-makers: Rohini Gudimetla
---

# ADR-0007: Use Spring Boot, not Go

## Context and Problem Statement

Clove is a job queue in which a number of workers claim jobs
from a database table and run them.
A language and framework have to be chosen before any code can be written.
Spring Boot and Go can both build a program like this.
We need to choose which one to build Clove with.


## Decision Drivers

* The language and framework should be ones the developer has already built with.
* Time spent learning a new language should pay off in features
  Clove would use. Go's main strengths are running thousands of
  tasks at once cheaply, fast startup and low memory use,
  and one long-running queue program makes little use of those strengths.


## Considered Options
* Spring Boot, in Java
* Go
## Decision Outcome

Chosen option: "Spring Boot, in Java", because we have already built with Java and Spring Boot, and learning Go would cost time without paying off in features Clove would use.

Clove is a Spring Boot application written in Java. ADR-0005 and ADR-0006 build on this choice.

### Consequences

* Good, because Spring Boot sets up Flyway and Spring Data JPA from configuration properties, so the tools chosen in ADR-0005 and ADR-0006 need no setup code.
* Bad, because the JVM makes Clove take several seconds to start and use more baseline memory than an equivalent Go program would.

### Confirmation

* Code review: `CloveApplication.java` is annotated with `@SpringBootApplication`.
* Code review: all application code under `src/main/java` is Java.

## Pros and Cons of the Options

### Go

* Good, because a goroutine costs a few KB of memory against about 1 MB for a Java thread, a Go program starts in under a second, and it deploys as a single file with no JVM to install.
* Bad, because we have not built with Go, and the time spent learning it would not pay off. Clove is one long-running queue program, so cheap goroutines at large scale and fast startup go mostly unused, and Java 21 virtual threads close the gap on cheap concurrency with one Spring Boot property.
## More Information

Decided in October 2026, recorded here on 2026-11-17.