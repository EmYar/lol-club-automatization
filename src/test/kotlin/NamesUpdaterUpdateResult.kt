import com.company.googledrive.entity.User
import com.company.lolapi.Summoner
import com.company.tasks.NamesUpdater
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NamesUpdaterUpdateResult {

    @Test
    fun `To string`() {
        val user = User("", "", 0, "123", "User", "", "", null, null)
        val summoner = Summoner(0, "User", "", "", "", "")
        val updateResult = NamesUpdater.UpdateResult(user, summoner, NamesUpdater.UpdateResult.Result.ACCOUNT_ID_ADDED)

        assertEquals("NamesUpdater: 123 User ACCOUNT_ID_ADDED", updateResult.toString())
    }
}