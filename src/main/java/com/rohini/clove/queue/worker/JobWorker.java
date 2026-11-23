package com.rohini.clove.queue.worker;

import com.rohini.clove.queue.handler.HandlerRegistry;
import com.rohini.clove.queue.handler.JobContext;
import com.rohini.clove.queue.handler.JobHandler;
import com.rohini.clove.queue.job.Job;
import com.rohini.clove.queue.job.JobRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobWorker {
    private JobRepository jobRepository;
    private HandlerRegistry registry;

    public JobWorker(JobRepository repository, HandlerRegistry handlerRegistry) {
        jobRepository = repository;
        registry = handlerRegistry;
    }

    @Scheduled(fixedDelay = 2000)
    public void pollAndProcess(){
        Job job = jobRepository.claimNext("heavy", "heavy-1");
        if (job == null) {
            return;
        }
        try {
            JobHandler handler = registry.get(job.getJobType());
            JobContext context = new JobContext(job.getId(), job.getAttemptsMade() + 1, job.getPayload(), jobRepository);
            long start = System.currentTimeMillis();
            String key = handler.handle(context);
            int durationMs = (int) (System.currentTimeMillis() - start);
            jobRepository.complete(job.getId(), key, durationMs);
        } catch (Exception e) {
            jobRepository.markFailed(job.getId(), job.getAttemptsMade() + 1, e.getMessage());
        }

    }

}
