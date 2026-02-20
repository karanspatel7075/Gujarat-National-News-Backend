package com.gnn.newsnetwork.GnnNewsNetworkApplication.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MemoryLogger {

    @EventListener(ApplicationReadyEvent.class)
    public void logMemoryOnStartup() {
        Runtime runtime = Runtime.getRuntime();

        long totalMemory = runtime.totalMemory() / (1024 * 1024);  // MB
        long freeMemory  = runtime.freeMemory()  / (1024 * 1024);  // MB
        long usedMemory  = totalMemory - freeMemory;
        long maxMemory   = runtime.maxMemory()   / (1024 * 1024);  // MB

        log.info("======= MEMORY STATUS ON STARTUP =======");
        log.info("Used Memory  : {} MB", usedMemory);
        log.info("Free Memory  : {} MB", freeMemory);
        log.info("Total Memory : {} MB", totalMemory);
        log.info("Max Memory   : {} MB", maxMemory);
        log.info("=========================================");
    }
}