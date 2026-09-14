package com.rohini.queue.service;

import com.rohini.queue.model.Job;
import com.rohini.queue.model.JobStatus;
import com.rohini.queue.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository){
        this.jobRepository = jobRepository;
    }

    public Job submitJob(String payload, int priority, int maxAttempts){
        Job job = new Job();
        job.setPayload(payload);
        job.setPriority(priority);
        job.setMaxAttempts(maxAttempts);
        job.setStatus(JobStatus.PENDING);
        job.setAttemptsMade(0);
        job.setRunAt(OffsetDateTime.now());
        return jobRepository.save(job);
    }

    public Optional<Job> getJobById(Long id){
        return jobRepository.findById(id);

    }

    public List<Job> getAllJobs(){
        return jobRepository.findAll();
    }

    public List<Job> getDeadLetterJobs(){
        return jobRepository.findByStatus(JobStatus.FAILED);
    }

    public Map<String, Long> getMetrics(){
        Map<String, Long> metrics= new HashMap<>();
        metrics.put("PENDING", jobRepository.countByStatus(JobStatus.PENDING));
        metrics.put("PROCESSING", jobRepository.countByStatus(JobStatus.PROCESSING));
        metrics.put("COMPLETED", jobRepository.countByStatus(JobStatus.COMPLETED));
        metrics.put("FAILED", jobRepository.countByStatus(JobStatus.FAILED));
        return metrics;
    }

}

