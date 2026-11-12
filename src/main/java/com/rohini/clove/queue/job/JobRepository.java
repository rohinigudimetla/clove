package com.rohini.clove.queue.job;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface JobRepository extends JpaRepository<Job, Long> {
    @Modifying
    @Transactional
    @Query(value = "UPDATE jobs SET progress = :progress WHERE id= :id AND status = 'PROCESSING'", nativeQuery = true)
    int updateProgress(Long id,int progress);

    @Modifying
    @Transactional
    @Query(value = "UPDATE jobs SET status = 'COMPLETED', result_key = :resultKey, duration_ms = :durationMs WHERE id = :id AND status <> 'COMPLETED'", nativeQuery = true)
    int complete(Long id, String resultKey, int durationMs);

    @Modifying
    @Transactional
    @Query(value = "UPDATE jobs SET status = 'CANCELLED' WHERE id = :id AND status = 'PENDING'", nativeQuery = true)
    int cancel(Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE jobs SET status = 'FAILED', attempts_made = :attemptsMade, failed_at = now(), last_error = :lastError WHERE id = :id", nativeQuery = true)
    int markFailed(Long id, int attemptsMade, String lastError);

    @Modifying
    @Transactional
    @Query(value = "UPDATE jobs SET status = 'PENDING', attempts_made = 0, run_at = now(), failed_at = NULL, last_error = NULL WHERE id = :id AND status = 'FAILED'", nativeQuery = true)
    int resetForRetry(Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE jobs SET status = 'PENDING', attempts_made = :attemptsMade, run_at = now() + interval '1 second' * :waitSeconds, failed_at = NULL, last_error = :lastError, progress = 0 WHERE id = :id", nativeQuery = true)
    int scheduleRetry(Long id, int attemptsMade, String lastError, long waitSeconds);

    @Transactional
    @Query(value = "UPDATE jobs SET status = 'PROCESSING', claimed_at = now(), last_progress_at = now(), worker_id = :workerId WHERE id = (SELECT id FROM jobs WHERE status = 'PENDING' AND run_at <= now() AND lane = :lane ORDER BY priority ASC, created_at ASC LIMIT 1 FOR UPDATE SKIP LOCKED) RETURNING *", nativeQuery = true)
    Job claimNext(String lane, String workerId);
}
