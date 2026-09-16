package com.rohini.clove.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "jobs")
@EntityListeners(AuditingEntityListener.class)
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "idempotency_key", unique = true, nullable = true)
    private String idempotencyKey;
    @Type(JsonType.class)
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private String payload;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private JobStatus status;
    @Column(name = "priority", nullable = false)
    private int priority;
    @Column(name = "worker_id", nullable = true)
    private String workerId;
    @Column(name = "attempts_made", nullable = false)
    private int attemptsMade;
    @Column(name = "max_attempts", nullable = false)
    private int maxAttempts;
    @Column(name = "last_error", nullable = true)
    private String lastError;
    @Column(name = "run_at", nullable = false)
    private OffsetDateTime runAt;
    @Column(name = "locked_at", nullable = true)
    private OffsetDateTime lockedAt;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
    @Column(name = "finished_at", nullable = true)
    private OffsetDateTime finishedAt;
    @Column(name = "failed_at", nullable = true)
    private OffsetDateTime failedAt;
}
