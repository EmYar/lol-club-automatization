package com.company.tasks

import com.company.googledrive.Parser
import com.company.googledrive.entity.User
import com.company.lolapi.ApiFabric
import com.company.lolapi.Summoner
import com.company.lolapi.SummonerApi
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import java.io.IOException

class NamesUpdater : Task {
    private val summonerApi: SummonerApi = ApiFabric.getInstance().summonerApi

    override fun run(): List<TaskResult> {
        return try {
            val users = Parser.parse(User::class)
            LOG.info("{} users parsed", users.size)
            users.filter { StringUtils.isNoneBlank(it.accountId, it.name) }
                    .map { process(it) }
                    .filterNotNull()
                    .toList()
        } catch (e: IOException) {
            LOG.error("Failed to update names", e)
            emptyList()
        }
    }

    private fun process(user: User): UpdateResult? {
        val summoner = getSummoner(user) ?: return UpdateResult(user, null, "Not match")

        return when {
            user.name != summoner.name -> {
                user.name = summoner.name
                UpdateResult(user, summoner, "Name updated")
            }
            StringUtils.isBlank(user.accountId) -> {
                user.accountId = summoner.accountId
                UpdateResult(user, summoner, "Account id added")
            }
            else -> null
        }
    }

    private fun getSummoner(user: User): Summoner? {
        return if (StringUtils.isNotBlank(user.accountId))
            summonerApi.getSummonerByAccountId(user.accountId).execute().body()
        else
            summonerApi.getSummonerByName(user.name).execute().body()
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(NamesUpdater::class.java)
    }

    data class UpdateResult(val user: User?, val summoner: Summoner?, val status: String?): TaskResult {
        override fun toString(): String {
            return "NamesUpdater: " +  user?.accountId + " " + user?.name + " " + status
        }
    }
}
