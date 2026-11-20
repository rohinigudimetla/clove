package com.rohini.clove.queue.handler;

import com.rohini.clove.queue.job.JobRepository;

public class JobContext {
    private long id;
    private int attempt;
    private String payload;
    private JobRepository jobRepository;

    public JobContext(long jobId, int jobAttempt, String jobPayload, JobRepository repository){
        id= jobId;
        attempt = jobAttempt;
        payload = jobPayload;
        jobRepository = repository;
    }

    public void reportProgress(int progress){
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("progress must be 0 to 100");
        }
        jobRepository.updateProgress(id, progress);
    }

    public String payload(){
        return payload;
    }

    public int attempt(){
        return attempt;
    }

}