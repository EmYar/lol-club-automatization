package com.company.lolapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestsRateLimitNonEquable implements RequestsRateLimit {
    private static final Logger log = LoggerFactory.getLogger(RequestsRateLimitNonEquable.class);

    private final long cooldown;
    private final int maxRequestsCount;
    private final AtomicInteger currentRequestsCount;
    private final ExecutorService executorService;
    private Future future;
    private final Runnable waitTask;

    protected RequestsRateLimitNonEquable(int requestsCount, TimeUnit timeUnit, int unitsCount) {
        cooldown = timeUnit.toMillis(unitsCount);
        maxRequestsCount = requestsCount;
        currentRequestsCount = new AtomicInteger(0);
        executorService = Executors.newSingleThreadExecutor();
        future = null;
        waitTask = () -> {
            try {
                Thread.sleep(cooldown);
                currentRequestsCount.set(0);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
                executorService.shutdownNow();
            }
        };
    }

    @Override
    public void acquire() {
        if (!executorService.isShutdown()) {
            if (future == null) {
                future = executorService.submit(waitTask);
            } else if (currentRequestsCount.intValue() >= maxRequestsCount) { //todo: fix >=
                try {
                    future.get();
                    future = null;
                } catch (InterruptedException | ExecutionException e) {
                    log.error(e.getMessage(), e);
                }
            }
            currentRequestsCount.incrementAndGet();
        } else {
            log.error("ExecutorService is shutted down");
        }
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }

    @Override
    public void shutdown() {
        executorService.shutdownNow();
    }
}
