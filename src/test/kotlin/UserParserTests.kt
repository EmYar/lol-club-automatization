import com.company.googledrive.UserParser
import org.junit.jupiter.api.Test

class UserParserTests {

    @Test
    fun `Join date parsing`() {
        val userParser = UserParser()
        userParser.parseEntity(listOf("0", "name", "vk", "discord", "oldNames", "admin", "1.1.18"), 0)
        userParser.parseEntity(listOf("0", "name", "vk", "discord", "oldNames", "admin", "10.1.18"), 0)
        userParser.parseEntity(listOf("0", "name", "vk", "discord", "oldNames", "admin", "1.10.18"), 0)
        userParser.parseEntity(listOf("0", "name", "vk", "discord", "oldNames", "admin", "10.10.18"), 0)
        userParser.parseEntity(listOf("0", "name", "vk", "discord", "oldNames", "admin", "1.1.2018"), 0)
        userParser.parseEntity(listOf("0", "name", "vk", "discord", "oldNames", "admin", "10.1.2018"), 0)
        userParser.parseEntity(listOf("0", "name", "vk", "discord", "oldNames", "admin", "1.10.2018"), 0)
        userParser.parseEntity(listOf("0", "name", "vk", "discord", "oldNames", "admin", "10.10.2018"), 0)
    }
}