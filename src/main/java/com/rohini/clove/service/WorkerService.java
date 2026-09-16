package com.rohini.clove.service;

import com.rohini.clove.model.Job;
import com.rohini.clove.model.JobStatus;
import com.rohini.clove.repository.JobRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WorkerService {
    private final JobRepository jobRepository;
    private final ThreadPoolTaskScheduler scheduler;

    public WorkerService(JobRepository jobRepository, ThreadPoolTaskScheduler scheduler){
        this.jobRepository = jobRepository;
        this.scheduler = scheduler;
    }
    @Scheduled(fixedDelayString = "${clove.worker.poll-delay-ms}")
    @Transactional
    public void pollAndProcess(){
        String threadName = Thread.currentThread().getName();
        Optional<Job> found = jobRepository.findNextPendingJob();
        found.ifPresent(job -> {
            job.setStatus(JobStatus.PROCESSING);
            job.setWorkerId(threadName);
            job.setLockedAt(OffsetDateTime.now());
            jobRepository.save(job);
            processJob(job);
        });
    }


    private void processJob(Job job) {
        try {
            Thread.sleep(2000);
            job.setStatus(JobStatus.COMPLETED);
            job.setFinishedAt(OffsetDateTime.now());
            job.setUpdatedAt(OffsetDateTime.now());
            jobRepository.save(job);


        }
        catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            handleFailure(job, e);
        }

    }

    private void handleFailure(Job job, Exception e){
        job.setAttemptsMade(job.getAttemptsMade()+1);  //????
        job.setLastError(e.getMessage());
        if (job.getAttemptsMade()>=job.getMaxAttempts()) {
            deadLetter(job);

        }
        else {
            job.setRunAt(OffsetDateTime.now().plusSeconds((long)(Math.pow(2, job.getAttemptsMade()))));
            job.setStatus(JobStatus.PENDING);
            jobRepository.save(job);
        }
    }

    private void deadLetter(Job job){
        job.setStatus(JobStatus.FAILED);
        job.setFailedAt(OffsetDateTime.now());
        jobRepository.save(job);

    }
    @Scheduled(fixedDelayString = "${clove.worker.zombie-check-delay-ms}")
    public void releaseZombieJobs(){
        OffsetDateTime threshold = OffsetDateTime.now().minusMinutes(5);
        List<Job> zombies = jobRepository.findZombieJobs(threshold);
        for (Job zombie: zombies){
            zombie.setStatus(JobStatus.PENDING);
            zombie.setWorkerId(null);
            zombie.setLockedAt(null);
            jobRepository.save(zombie);
        }

    }

}
