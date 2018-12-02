package com.company.lolapi

import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

open class RequestsRateLimit(protected val requestsCount: Int, timeUnit: TimeUnit, unitsCount: Int)
    : Comparable<RequestsRateLimit> {

    val cooldownTime: Long
    protected var cooldown: Job? = null

    init {
        val timeLimit = timeUnit.toMillis(unitsCount.toLong())
        cooldownTime = Math.ceil(timeLimit.toDouble() / requestsCount.toDouble()).toLong()
    }

    fun acquire() = runBlocking  {
        if (shouldWait()) {
            cooldown!!.join()
        }
        startCooldown()
    }

    fun shutdown() {
        cooldown?.cancel()
    }

    protected open fun shouldWait(): Boolean {
        return cooldown?.isActive == true
    }

    protected open fun startCooldown() {
        cooldown = GlobalScope.launch {
            delay(cooldownTime)
        }
    }

    override fun compareTo(other: RequestsRateLimit): Int {
        return cooldownTime.compareTo(other.cooldownTime)
    }
}