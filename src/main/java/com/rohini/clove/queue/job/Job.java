package com.rohini.clove.queue.job;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@DynamicInsert
@Table(name = "jobs")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String jobType;
    String lane;
    @JdbcTypeCode(SqlTypes.JSON)
    String payload;
    Integer priority;
    String workerId;
    Integer maxAttempts;
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    JobStatus status;
    Instant createdAt;
    Instant runAt;
    int attemptsMade;
    int progress;
    Instant claimedAt;
    Instant lastProgressAt;
    String lastError;
    String resultKey;
    Integer durationMs;
    Instant failedAt;
    public Long getId() {
        return id;
    }

    public String getJobType() {
        return jobType;
    }

    public String getPayload() {
        return payload;
    }

    public int getAttemptsMade() {
        return attemptsMade;
    }

}
