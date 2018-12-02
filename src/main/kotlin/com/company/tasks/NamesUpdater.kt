package com.company.tasks

import com.company.ApiFabric
import com.company.googledrive.Parser
import com.company.googledrive.entity.User
import com.company.lolapi.LolApi
import com.company.lolapi.Summoner
import com.company.tasks.NamesUpdater.UpdateResult.Result
import com.google.common.base.Charsets
import com.google.common.io.Resources
import one.util.streamex.StreamEx
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import java.io.IOException

class NamesUpdater : Task {

    private val apiKey = Resources.toString(Resources.getResource("lol_api_key"), Charsets.UTF_8)

    override fun run(): List<TaskResult> {
        LOG.info("NamesUpdater started")
        return try {
            val users = Parser.get(User::class)
            LOG.info("{} users parsed", users.size)
            save(users.filterNot { StringUtils.isAllBlank(it.accountId, it.name) }
                    .mapNotNull { process(it, ApiFabric.getLolApi()) }
                    .toList())
        } catch (e: IOException) {
            LOG.error("Failed to update names", e)
            emptyList()
        } finally {
            LOG.info("NamesUpdater completed")
        }
    }

    private fun process(user: User, lolApi: LolApi): UpdateResult? {
        val summoner = getSummoner(user, lolApi) ?: return UpdateResult(user, null, Result.NOT_MATCH)

        return when {
            user.name != summoner.name -> {
                user.name = summoner.name
                UpdateResult(user, summoner, Result.NAME_UPDATED)
            }
            StringUtils.isBlank(user.accountId) -> {
                user.accountId = summoner.accountId
                UpdateResult(user, summoner, Result.ACCOUNT_ID_ADDED)
            }
            else -> null
        }
    }

    private fun getSummoner(user: User, lolApi: LolApi): Summoner? {
        return if (StringUtils.isNotBlank(user.accountId))
            lolApi.getSummonerByAccountId(user.accountId, apiKey).execute().body()
        else
            lolApi.getSummonerByName(user.name, apiKey).execute().body()
    }

    private fun save(result: List<UpdateResult>): List<UpdateResult> {
        Parser.update(User::class,
                result.filterNot { it -> it.status == Result.NOT_MATCH }
                        .mapNotNull(UpdateResult::user)
                        .toList())
        return result
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(NamesUpdater::class.java)
    }

    data class UpdateResult(val user: User?, val summoner: Summoner?, val status: Result?) : TaskResult {
        override fun toString(): String {
            return StreamEx.of("NamesUpdater:", user?.accountId, user?.name, status)
                    .nonNull()
                    .joining(" ")
        }

        enum class Result { NAME_UPDATED, ACCOUNT_ID_ADDED, NOT_MATCH }
    }
}
