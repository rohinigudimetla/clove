package com.rohini.clove.queue.handler;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HandlerRegistry {
    private Map<String, JobHandler> handlers;

    public HandlerRegistry(List<JobHandler> jobHandlers){
        handlers = new HashMap<>();
        for (JobHandler handler : jobHandlers) {
            handlers.put(handler.jobType(), handler);
        }
    }

    public JobHandler get(String jobType){
        return handlers.get(jobType);
    }
}