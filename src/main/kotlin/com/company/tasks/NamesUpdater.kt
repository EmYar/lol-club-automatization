package com.company.tasks

import com.company.ApiFabric
import com.company.googledrive.GDriveService
import com.company.googledrive.entity.User
import com.company.lolapi.LolApi
import com.company.lolapi.Summoner
import com.company.tasks.NamesUpdater.UpdateResult.Result
import com.google.common.base.Charsets
import com.google.common.io.Resources
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import java.io.IOException

class NamesUpdater : Task {

    @Suppress("UnstableApiUsage")
    private val apiKey = Resources.toString(Resources.getResource("lol_api_key"), Charsets.UTF_8)

    override fun run(): List<TaskResult> {
        LOG.info("NamesUpdater started")
        return try {
            val users = GDriveService.get(User::class)
            LOG.info("{} users parsed", users.size)
            val lolApi = ApiFabric.lolApi
            save(users.asSequence()
                    .filterNot { StringUtils.isAllBlank(it.accountId, it.name) }
                    .mapNotNull { process(it, lolApi) }
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

    private fun save(results: List<UpdateResult>): List<UpdateResult> {
        GDriveService.update(User::class,
                results.asSequence()
                        .filterNot { it -> it.status == Result.NOT_MATCH }
                        .mapNotNull(UpdateResult::user)
                        .toList())
        return results
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(NamesUpdater::class.java)
    }

    data class UpdateResult(val user: User?, val summoner: Summoner?, val status: Result?) : TaskResult {
        override fun toString(): String {
            return arrayOf("NamesUpdater:", user?.accountId, user?.name, status)
                    .filterNotNull()
                    .joinToString(" ")
        }

        enum class Result { NAME_UPDATED, ACCOUNT_ID_ADDED, NOT_MATCH }
    }
}
