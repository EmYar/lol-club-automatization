package lolapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class ApiRequestLimiter {
    private final List<RequestsRateLimit> limits;

    protected ApiRequestLimiter(List<RequestsRateLimit> limits) {
        this.limits = Collections.unmodifiableList(limits);
    }

    public static ApiRequestLimiterBuilder build() {
        return new ApiRequestLimiterBuilder();
    }

    public void execute(Runnable runnable) {
        acquire();
        runnable.run();
    }

    public <T> T execute(Supplier<T> supplier) {
        acquire();
        return supplier.get();
    }

    public void acquire() {
        for (RequestsRateLimit limit : limits) {
            limit.acquire();
        }
    }

    public static class ApiRequestLimiterBuilder {
        private List<RequestsRateLimit> limits;

        protected ApiRequestLimiterBuilder() {
            limits = new ArrayList<>();
        }

        public ApiRequestLimiterBuilder add(int requestsCount, TimeUnit timeUnit, int unitsCount) {
            return add(requestsCount, timeUnit, unitsCount, true);
        }

        public ApiRequestLimiterBuilder add(int requestsCount, TimeUnit timeUnit, int unitsCount, boolean equable) {
            RequestsRateLimit limit = equable
                    ? new RequestsRateLimitEquable(requestsCount, timeUnit, unitsCount)
                    : new RequestsRateLimitNonEquable(requestsCount, timeUnit, unitsCount);
            limits.add(limit);
            return this;
        }

        public ApiRequestLimiter build() {
            Collections.sort(limits);
            return new ApiRequestLimiter(limits);
        }
    }
}
