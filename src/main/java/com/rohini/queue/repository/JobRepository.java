package com.rohini.queue.repository;

import com.rohini.queue.model.Job;
import com.rohini.queue.model.JobStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE jobs " +
            "SET status = 'PROCESSING', " +
            "worker_id = :workerId, " +
            "locked_at = NOW(), " +
            "updated_at = NOW() " +
            "WHERE id = (" +
            "SELECT id FROM jobs " +
            "WHERE status = 'PENDING' " +
            "AND run_at <= NOW() " +
            "ORDER BY priority DESC, id ASC " +
            "LIMIT 1 " +
            "FOR UPDATE SKIP LOCKED" +
            ") RETURNING *", nativeQuery = true)
    Optional<Job> claimNextJob(@Param("workerId") String workerId);
    List<Job> findByStatus(JobStatus status);
    Long countByStatus(JobStatus status);

}