package com.rohini.clove.queue.handler;

public interface JobHandler {
    String handle(JobContext context);
    String jobType();
}
