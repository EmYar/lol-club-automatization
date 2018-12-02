import com.company.lolapi.ApiRequestLimiter
import org.junit.jupiter.api.Test
import org.opentest4j.AssertionFailedError
import java.util.concurrent.TimeUnit.SECONDS

class ApiRequestLimiter {

    @Test
    fun `Requests count in equable limit`() {
        val timeUnit = SECONDS
        val timeUnitCount = 1
        val requestsLimitCount = 3
        val limiter = ApiRequestLimiter.builder()
                .add(requestsLimitCount, timeUnit, timeUnitCount, true)
                .build()
        val limitTime = timeUnit.toMillis(timeUnitCount.toLong())

        val requestsTime = getRequestsTime(limiter, requestsLimitCount + 1)

        if (requestsTime <= limitTime)
            throw AssertionFailedError("", limitTime, requestsTime)
    }

    @Test
    fun `Requests count less than the quantity limit run faster than the non equable time limit`() {
        val timeUnit = SECONDS
        val timeUnitCount = 1
        val requestsLimitCount = 3
        val limiter = ApiRequestLimiter.builder()
                .add(requestsLimitCount, timeUnit, timeUnitCount, false)
                .build()
        val limitTime = timeUnit.toMillis(timeUnitCount.toLong())

        val requestsTime = getRequestsTime(limiter, requestsLimitCount - 1)

        if (requestsTime >= limitTime)
            throw AssertionFailedError("Requests time above or equal limit time:", limitTime, requestsTime)
    }

    @Test
    fun `Requests count more than the quantity limit run not faster than the non equable time limit`() {
        val timeUnit = SECONDS
        val timeUnitCount = 1
        val requestsLimitCount = 3
        val limiter = ApiRequestLimiter.builder()
                .add(requestsLimitCount, timeUnit, timeUnitCount, false)
                .build()
        val limitTime = timeUnit.toMillis(timeUnitCount.toLong())

        val requestsTime = getRequestsTime(limiter, requestsLimitCount + 1)

        if (requestsTime <= limitTime)
            throw AssertionFailedError("Requests time below or equal limit time:", limitTime, requestsTime)
    }

    private fun getRequestsTime(limiter: ApiRequestLimiter, requestsCount: Int): Long {
        val startTime = System.currentTimeMillis()
        for (i in 1..requestsCount) {
            limiter.acquire()
        }
        return System.currentTimeMillis() - startTime
    }
}