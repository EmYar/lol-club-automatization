package com.company.tasks

import com.company.ApiFabric
import com.company.googledrive.FarmResultParser
import com.company.googledrive.GDriveService
import com.company.googledrive.entity.FarmResult
import com.company.googledrive.entity.Role
import com.company.googledrive.entity.User
import com.company.lolclubsapi.CurrentSeason
import com.company.lolclubsapi.SummonerResult
import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table

class ScoreSaver : Task {
    private val cookies = "__cfduid=d256ad35440283c94b7b8e2e9e00e349e1543776809;" +
            "PVPNET_TOKEN_RU=eyJkYXRlX3RpbWUiOjE1NDI5NzY4ODM2NTAsImdhc19hY2NvdW50X2lkIjoyMDAyNDMwNzksInB2cG5ldF9hY2NvdW50X2lkIjoyMDAyNDMwNzksInN1bW1vbmVyX25hbWUiOiJcdTA0MjJcdTA0MzBcdTA0MzdcdTA0MzhcdTA0M2FcdTA0NDEiLCJ2b3VjaGluZ19rZXlfaWQiOiI5MDM0NzUyYjJiNDU2MDQ0YWU4N2YyNTk4MmRhZDA3ZCIsInNpZ25hdHVyZSI6InFzb3ROWStKN3F0UVVIWFRtUjBva1FoSWtrbW0vTUNTZFhmYW0rZ08rZ1lxMGFXR1VIakdRVytxK0t5NUpidVAwbHNjZ1RBY3htTDNZclIveFNQNEhiSm41bURLbEh3eEF4QVN4K3g4VE1aVjhLQkdwRGhUZXJESWtwQ0tnZFZmUjB3ODVEdEYycTNVYm1jb1F0TFgwZmZvdUUveG9JNnBlNjhhN1FFY3BnVT0ifQ%3D%3D;" +
            "csrftoken=wP8m0Z7e85wgBDE6l8GEBBtf0nQfqQLyDmcyhnJIQ2Gsjv92zXMMQKFNJUbarUlq;" +
            "sessionid=a7in0yqvylbs6cdxzkz48nqvbszrv4qp;"

    private val scoresSpreadsheets = mapOf(
            2018 to "1sUBDpc10k47g-JBlgkXe_6dOUy8hT84QXfsuV05GhY8",
            2019 to "1yeYcxaJnc7_vghUmiISScqeOIg-NNd7lwzTaCcU769s"
    )

    //todo replace RuntimeException
    override fun run(): List<TaskResult> {
        val currentSeason = ApiFabric.lolClubsApi.getCurrentSeason(cookies)
                .execute()
                .body()
                ?: throw RuntimeException("Cannot get current season")

        val currentSeasonDate = currentSeason.getLocaleStartDate()
        val monthName = currentSeasonDate.month.toString()
        val scoresSpreadsheetId = scoresSpreadsheets[currentSeasonDate.year]
                ?: throw RuntimeException("Cannot find page for ${monthName} in ${currentSeasonDate.year} year")
        createSheetIfNotExists(scoresSpreadsheetId, monthName)

        val savedResults = GDriveService.get(FarmResult::class, scoresSpreadsheetId, monthName)
        val results = getCompletedStagesResults(currentSeason)
        val farmResultUtils = FarmResultUpdater(currentSeason, scoresSpreadsheetId, monthName, savedResults, results)

        GDriveService.updateMultiSpreadSheet(FarmResult::class, farmResultUtils.update())

        return emptyList()
    }

    private fun createSheetIfNotExists(scoresSpreadsheetId: String, sheetName: String) {

    }

    private fun getCompletedStagesResults(currentSeason: CurrentSeason): List<SummonerResult> {
        return currentSeason.stages.asSequence()
                .filter { stage -> stage.is_open && stage.is_closed }
                .mapNotNull { stage -> ApiFabric.lolClubsApi.getStageScores(currentSeason.id, stage.id, cookies).execute().body() }
                .flatMap { stageResult -> stageResult.results.asSequence() }
                .toList()
    }

    private class FarmResultUpdater(
            currentSeason: CurrentSeason,
            val spreadSheetId: String,
            val sheetName: String,
            savedResults: List<FarmResult>,
            val results: List<SummonerResult>) {

        private val stagesIds: List<Int> = currentSeason.stages.map { it.id }
        private val resultsTable: Table<String, String, FarmResult>
        init {
            resultsTable = HashBasedTable.create()
            for (savedResult in savedResults) {
                resultsTable.put(savedResult.userId, savedResult.userName, savedResult)
            }
        }

        fun update(): Collection<FarmResult> {
            for (result in results) {
                val savedResult = getSavedResult(result)
                val stageNumber = getResultStageNumber(result)
                savedResult.stages = savedResult.stages
                        .mapIndexed { index, i -> if (index == stageNumber) result.points else i }
            }
            for (user in GDriveService.get(User::class)) {
                //todo consider the case of incomplete user data
                if ((user.role == Role.ADMIN || user.role == Role.MEMBER) &&
                        !userInTable(user)) {
                    val newFarmResult = FarmResultParser.createEntity(
                            resultsTable.values(),
                            spreadSheetId,
                            sheetName,
                            user.accountId,
                            user.name,
                            listOf(0, 0, 0))
                    resultsTable.put(newFarmResult.userId, newFarmResult.userName, newFarmResult)
                }
            }
            return resultsTable.values()
        }

        private fun getSavedResult(result: SummonerResult): FarmResult {
            //todo consider the case of incomplete user data
            return resultsTable.get(result.summoner.lol_account_id, result.summoner.summoner_name)
                    ?: createNewSavedFarmResult(resultsTable.values(), result)
        }

        private fun createNewSavedFarmResult(savedResults: Collection<FarmResult>, result: SummonerResult): FarmResult {
            val newFarmResult = FarmResultParser.createEntity(
                    savedResults,
                    spreadSheetId,
                    sheetName,
                    result.summoner.lol_account_id,
                    result.summoner.summoner_name,
                    listOf(0, 0, 0))
            resultsTable.put(newFarmResult.userId, newFarmResult.userName, newFarmResult)
            return newFarmResult
        }

        private fun getResultStageNumber(result: SummonerResult): Int {
            return stagesIds.indexOf(result.stage)
        }

        private fun userInTable(user: User) : Boolean {
            return user.getNames().asSequence()
                    .any { resultsTable.contains(user.accountId, it) }
        }
    }
}