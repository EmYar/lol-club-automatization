package com.company.lolapi

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class RequestsRateLimitNonEquable(requestsCount: Int, timeUnit: TimeUnit, unitsCount: Int):
        RequestsRateLimit(requestsCount, timeUnit, unitsCount) {

    @Volatile private var requestsInCurrentTimeLimitCount: Int = 0

    override fun shouldWait(): Boolean {
        return cooldown?.isActive == true && requestsInCurrentTimeLimitCount < requestsCount
    }

    override fun startCooldown() {
        cooldown = GlobalScope.launch {
            delay(cooldownTime)
            requestsInCurrentTimeLimitCount = 0
        }
    }
}