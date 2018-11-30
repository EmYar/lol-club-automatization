package com.company.lolapi

import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class RequestsRateLimitEquable(requestsCount: Int, timeUnit: TimeUnit, unitsCount: Int) : RequestsRateLimit {
    private val cooldownTime: Long
    private var cooldown: Job = GlobalScope.launch {  }

    init {
        val timeLimit = timeUnit.toMillis(unitsCount.toLong())
        cooldownTime = Math.ceil(timeLimit.toDouble() / requestsCount.toDouble()).toLong()
    }

    override fun acquire() = runBlocking  {
        if (cooldown.isActive) {
            System.out.println("Waiting")
            cooldown.join()
        } else {
            startCooldown()
            System.out.println("Skip")
        }
    }

    override fun getCooldownTime(): Long {
        return cooldownTime
    }

    override fun shutdown() {
        cooldown.cancel()
    }

    private fun startCooldown() {
        cooldown = GlobalScope.launch {
            delay(cooldownTime)
        }
    }
}