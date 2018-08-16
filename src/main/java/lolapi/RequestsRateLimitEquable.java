package lolapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

public class RequestsRateLimitEquable implements RequestsRateLimit {
    private static final Logger log = LoggerFactory.getLogger(RequestsRateLimitEquable.class);

    private final long cooldown;
    private final ExecutorService executorService;
    private Future future;
    private final Runnable waitTask;

    protected RequestsRateLimitEquable(int requestsCount, TimeUnit timeUnit, int unitsCount) {
        cooldown = timeUnit.toMillis(unitsCount) / requestsCount;
        executorService = Executors.newSingleThreadExecutor();
        future = null;
        waitTask = () -> {
            try {
                Thread.sleep(cooldown);
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
            } else {
                try {
                    future.get();
                    future = null;
                } catch (InterruptedException | ExecutionException e) {
                    log.error(e.getMessage(), e);
                }
            }
        } else {
            log.error("ExecutorService is shutted down");
        }
    }

    @Override
    public long getCooldown() {
        return cooldown;
    }
}
