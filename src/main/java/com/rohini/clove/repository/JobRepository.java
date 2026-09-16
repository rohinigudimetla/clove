package com.rohini.clove.repository;

import com.rohini.clove.model.Job;
import com.rohini.clove.model.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    @Query(value = "SELECT * FROM jobs WHERE status = 'PENDING' AND run_at <= NOW() ORDER BY priority DESC, id ASC LIMIT 1 FOR UPDATE SKIP LOCKED", nativeQuery = true)
    Optional<Job> findNextPendingJob();

    List<Job> findByStatus(JobStatus status);

    Long countByStatus(JobStatus status);

    @Query("SELECT j FROM Job j WHERE j.status = 'PROCESSING' AND j.lockedAt < :threshold")
    List<Job> findZombieJobs(@Param("threshold") OffsetDateTime threshold);

}