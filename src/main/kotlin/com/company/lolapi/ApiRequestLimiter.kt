package com.company.lolapi

import java.util.concurrent.TimeUnit
import java.util.function.Supplier

class ApiRequestLimiter private constructor(private val limits: List<RequestsRateLimit>) : AutoCloseable {

    fun execute(runnable: Runnable) {
        acquire()
        runnable.run()
    }

    fun <T> execute(supplier: Supplier<T>): T {
        acquire()
        return supplier.get()
    }

    fun acquire() {
        for (limit in limits) {
            limit.acquire()
        }
    }

    override fun close() {
        for (limit in limits) {
            limit.shutdown()
        }
    }

    class ApiRequestLimiterBuilder internal constructor() {
        private val limits = mutableListOf<RequestsRateLimit>()

        fun add(requestsCount: Int, timeUnit: TimeUnit, unitsCount: Int, equable: Boolean = true): ApiRequestLimiterBuilder {
            limits.add(
                    if (equable) RequestsRateLimit(requestsCount, timeUnit, unitsCount)
                    else RequestsRateLimitNonEquable(requestsCount, timeUnit, unitsCount))
            return this
        }

        fun build(): ApiRequestLimiter {
            limits.sort()
            return ApiRequestLimiter(limits)
        }
    }

    companion object {
        fun builder(): ApiRequestLimiterBuilder {
            return ApiRequestLimiterBuilder()
        }
    }
}
