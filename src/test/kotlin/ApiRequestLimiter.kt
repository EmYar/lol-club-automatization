import com.company.ApiFabric
import com.company.lolapi.ApiRequestLimiter
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.opentest4j.AssertionFailedError
import java.util.concurrent.TimeUnit.SECONDS

class ApiRequestLimiter {

    //this is tmp test
    @Test
    fun `Clubs api test`() {
        ApiFabric.use { apiFabric ->
            val token = "PVPNET_TOKEN_RU=eyJkYXRlX3RpbWUiOjE1NDI5NzY4ODM2NTAsImdhc19hY2NvdW50X2lkIjoyMDAyNDMwNzksInB2cG5ldF9hY2NvdW50X2lkIjoyMDAyNDMwNzksInN1bW1vbmVyX25hbWUiOiJcdTA0MjJcdTA0MzBcdTA0MzdcdTA0MzhcdTA0M2FcdTA0NDEiLCJ2b3VjaGluZ19rZXlfaWQiOiI5MDM0NzUyYjJiNDU2MDQ0YWU4N2YyNTk4MmRhZDA3ZCIsInNpZ25hdHVyZSI6InFzb3ROWStKN3F0UVVIWFRtUjBva1FoSWtrbW0vTUNTZFhmYW0rZ08rZ1lxMGFXR1VIakdRVytxK0t5NUpidVAwbHNjZ1RBY3htTDNZclIveFNQNEhiSm41bURLbEh3eEF4QVN4K3g4VE1aVjhLQkdwRGhUZXJESWtwQ0tnZFZmUjB3ODVEdEYycTNVYm1jb1F0TFgwZmZvdUUveG9JNnBlNjhhN1FFY3BnVT0ifQ%3D%3D"
            val api = apiFabric.getLolClubApi()
            val result = api.getStageScores(14, 43, token).execute().body()
            assertTrue(result?.results?.isEmpty() == false)
        }
    }

    @Test
    fun `Requests count in equable limit`() {
        val timeUnit = SECONDS
        val timeUnitCount = 1
        val requestsLimitCount = 3
        ApiRequestLimiter.builder()
                .add(requestsLimitCount, timeUnit, timeUnitCount, true)
                .build()
                .use { limiter ->
                    val limitTime = timeUnit.toMillis(timeUnitCount.toLong())

                    val requestsTime = getRequestsTime(limiter, requestsLimitCount + 1)

                    if (requestsTime <= limitTime)
                        throw AssertionFailedError("", limitTime, requestsTime)
                }
    }

    @Test
    fun `Requests count less than the quantity limit run faster than the non equable time limit`() {
        val timeUnit = SECONDS
        val timeUnitCount = 1
        val requestsLimitCount = 3
        val limitTime = timeUnit.toMillis(timeUnitCount.toLong())
        ApiRequestLimiter.builder()
                .add(requestsLimitCount, timeUnit, timeUnitCount, false)
                .build()
                .use { limiter ->
                    val requestsTime = getRequestsTime(limiter, requestsLimitCount - 1)

                    if (requestsTime >= limitTime)
                        throw AssertionFailedError("Requests time above or equal limit time:", limitTime, requestsTime)
                }
    }

    @Test
    fun `Requests count more than the quantity limit run not faster than the non equable time limit`() {
        val timeUnit = SECONDS
        val timeUnitCount = 1
        val requestsLimitCount = 3
        val limitTime = timeUnit.toMillis(timeUnitCount.toLong())
        ApiRequestLimiter.builder()
                .add(requestsLimitCount, timeUnit, timeUnitCount, false)
                .build()
                .use { limiter ->
                    val requestsTime = getRequestsTime(limiter, requestsLimitCount + 1)

                    if (requestsTime <= limitTime)
                        throw AssertionFailedError("Requests time below or equal limit time:", limitTime, requestsTime)
                }
    }

    private fun getRequestsTime(limiter: ApiRequestLimiter, requestsCount: Int): Long {
        val startTime = System.currentTimeMillis()
        for (i in 1..requestsCount) {
            limiter.acquire()
        }
        return System.currentTimeMillis() - startTime
    }
}