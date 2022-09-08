package be.hi10.realnutrition.scheduledtasks;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import be.hi10.realnutrition.retryprocedure.ScheduledRetryProcedure;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemoryChecker {
    private final static Logger log = LoggerFactory.getLogger(ScheduledRetryProcedure.class);
	
	@Scheduled(fixedDelay = 1800000)
    public void checkMemory() {
        Runtime runtime = Runtime.getRuntime();
        long memoryUsed = runtime.maxMemory() - runtime.freeMemory();
        log.info("MEMORY STATS:\nFREE: " + bytesToMegabytes(runtime.freeMemory())
                + " MB\nUSED: " + bytesToMegabytes(memoryUsed) + " MB\n"
                + "TOTAL: " + bytesToMegabytes(runtime.totalMemory()) + " MB\n"
                + "MAX: " + bytesToMegabytes(runtime.maxMemory()) + " MB\n");
    }

    private long bytesToMegabytes(long bytes) {
        return bytes / (1024L * 1024L);
    }
}
