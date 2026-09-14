package com.rohini.queue.controller;

import com.rohini.queue.dto.JobRequest;
import com.rohini.queue.model.Job;
import com.rohini.queue.service.JobService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService){
        this.jobService = jobService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Job submitJob(@RequestBody JobRequest request) {
        return jobService.submitJob(request.getPayload(), request.getPriority(), request.getMaxAttempts());
    }
    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs(){
        return ResponseEntity.ok(jobService.getAllJobs());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable Long id){
        return jobService.getJobById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/dead-letter")
    public ResponseEntity<List<Job>> getDeadLetterJobs(){
        return ResponseEntity.ok(jobService.getDeadLetterJobs());
    }
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Long>> getMetrics(){
        return ResponseEntity.ok(jobService.getMetrics());
    }
}
