package com.company.tasks

import com.company.googledrive.Parser
import com.company.googledrive.entity.User
import com.company.lolapi.ApiFabric
import com.company.lolapi.Summoner
import com.company.tasks.NamesUpdater.UpdateResult.Result
import one.util.streamex.StreamEx
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import java.io.IOException

class NamesUpdater : Task {

    override fun run(): List<TaskResult> {
        LOG.info("NamesUpdater started")
        ApiFabric.getInstance().use { fabric ->
            return try {
                val users = Parser.parse(User::class)
                LOG.info("{} users parsed", users.size)
                save(users.filterNot { StringUtils.isAllBlank(it.accountId, it.name) }
                        .mapNotNull { process(it, fabric) }
                        .toList())
            } catch (e: IOException) {
                LOG.error("Failed to update names", e)
                emptyList()
            } finally {
                LOG.info("NamesUpdater completed")
            }
        }
    }

    private fun process(user: User, apiFabric: ApiFabric): UpdateResult? {
        val summoner = getSummoner(user, apiFabric) ?: return UpdateResult(user, null, Result.NOT_MATCH)

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

    private fun getSummoner(user: User, apiFabric: ApiFabric): Summoner? {
        return if (StringUtils.isNotBlank(user.accountId))
            apiFabric.summonerApi.getSummonerByAccountId(user.accountId).execute().body()
        else
            apiFabric.summonerApi.getSummonerByName(user.name).execute().body()
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
