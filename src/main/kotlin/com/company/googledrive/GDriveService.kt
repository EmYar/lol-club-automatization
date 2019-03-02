package com.company.googledrive

import com.company.googledrive.entity.*
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest
import java.io.IOException
import java.io.InputStreamReader
import kotlin.reflect.KClass

object GDriveService {
    private const val CLIENT_SECRET_FILE = "client_secret.json"
    private const val CREDENTIALS_FOLDER = "credentials"
    private val SCOPES = listOf(SheetsScopes.SPREADSHEETS)

    private val jsonFactory: JsonFactory
    private val service: Sheets

    private val entityParsersMap: Map<KClass<out GDriveEntity>, EntityParser<out GDriveEntity>>
    private val multiSheetsParsersMap: Map<KClass<out MultiSheetEntity>, MultiSheetsEntityParser<out MultiSheetEntity>>
    private val multiSpreadSheetEntity: Map<KClass<out MultiSpreadSheetEntity>, MultiSpreadSheetEntityParser<out MultiSpreadSheetEntity>>

    init {
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        jsonFactory = JacksonFactory.getDefaultInstance()
        service = Sheets.Builder(httpTransport, jsonFactory, getCredentials(httpTransport))
                .setApplicationName("The King's bot") //todo: move to resources
                .build()
        entityParsersMap = mapOf(User::class to UserParser())
        multiSheetsParsersMap = mapOf(SeasonInfo::class to SeasonInfoParser)
        multiSpreadSheetEntity = mapOf(FarmResult::class to FarmResultParser)
    }

    @Throws(IOException::class)
    fun <T : GDriveEntity> get(entityClass: KClass<T>): List<T> {
        val parseInfo = getEntityParser(entityClass)

        val response = service.spreadsheets().values()
                .get(parseInfo.spreadsheetId, "${parseInfo.sheetId}!${parseInfo.range}")
                .execute()

        return response.getValues()
                .mapIndexed { index, list -> parseInfo.parseEntity(list, index + parseInfo.firstRow) }
    }

    @Throws(IOException::class)
    fun <T : MultiSheetEntity> get(multiSheetsEntityClass: KClass<T>, sheetName: String): List<T> {
        val parseInfo = getEntityParser(multiSheetsEntityClass)

        val response = service.spreadsheets().values()
                .get(parseInfo.spreadsheetId, sheetName + "!" + parseInfo.range)
                .execute()

        return parseInfo.parseEntities(sheetName, response.getValues())
    }

    @Throws(IOException::class)
    fun <T : MultiSpreadSheetEntity> get(multiSpreadSheetEntityClass: KClass<T>,
                                         spreadsheetId: String,
                                         sheetName: String): List<T> {
        val parseInfo = getEntityParser(multiSpreadSheetEntityClass)

        val response = service.spreadsheets().values()
                .get(spreadsheetId, "$sheetName!${parseInfo.range}")
                .execute()

        return parseInfo.parseEntities(spreadsheetId, sheetName, response.getValues() ?: emptyList())
    }

    @Throws(IOException::class)
    fun <T : GDriveEntity> update(entityClass: KClass<T>, entities: Collection<T>) {
        val parseInfo = getEntityParser(entityClass)

        service.spreadsheets().values()
                .batchUpdate(parseInfo.spreadsheetId, BatchUpdateValuesRequest()
                        .setValueInputOption("USER_ENTERED")
                        .setData(entities.map { parseInfo.toValueRange(it) }))
                .execute()
    }

    @Throws(IOException::class)
    fun <T : MultiSheetEntity> updateMultiSheet(entityClass: KClass<T>, entities: Collection<T>) {
        val parseInfo = getEntityParser(entityClass)

        service.spreadsheets().values()
                .batchUpdate(parseInfo.spreadsheetId, BatchUpdateValuesRequest()
                        .setValueInputOption("USER_ENTERED")
                        .setData(entities.map { parseInfo.toValueRange(it) }))
                .execute()
    }

    @Throws(IOException::class)
    fun <T : MultiSpreadSheetEntity> updateMultiSpreadSheet(entityClass: KClass<T>, entities: Collection<T>) {
        val parseInfo = getEntityParser(entityClass)

        parseInfo.toValueRange(entities).forEach { spreadsheetId, valueRanges ->
            service.spreadsheets().values()
                    .batchUpdate(spreadsheetId, BatchUpdateValuesRequest()
                            .setValueInputOption("USER_ENTERED")
                            .setData(valueRanges))
                    .execute()
        }
    }

    fun getSheetsList(spreadsheetId: String): List<String> {
        val spreadsheet = service.spreadsheets().get(spreadsheetId).execute()
        return spreadsheet.sheets.map { it.properties.title }
    }

    @Throws(IOException::class)
    private fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential {
        val inputStream = Thread.currentThread().contextClassLoader.getResourceAsStream(CLIENT_SECRET_FILE)
        val clientSecrets = GoogleClientSecrets.load(jsonFactory, InputStreamReader(inputStream))
        val flow = GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, jsonFactory, clientSecrets, SCOPES)
                .setDataStoreFactory(FileDataStoreFactory(java.io.File(CREDENTIALS_FOLDER)))
                .setAccessType("offline")
                .build()

        return AuthorizationCodeInstalledApp(flow, LocalServerReceiver()).authorize("user")
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : GDriveEntity> getEntityParser(entityClass: KClass<T>): EntityParser<T> {
        return entityParsersMap[entityClass] as? EntityParser<T>
                ?: throw ParserNotFoundException("Parser for $entityClass not found")
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : MultiSheetEntity> getEntityParser(entityClass: KClass<T>): MultiSheetsEntityParser<T> {
        return multiSheetsParsersMap[entityClass] as? MultiSheetsEntityParser<T>
                ?: throw ParserNotFoundException("Parser for $entityClass not found")
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : MultiSpreadSheetEntity> getEntityParser(entityClass: KClass<T>): MultiSpreadSheetEntityParser<T> {
        return multiSpreadSheetEntity[entityClass] as? MultiSpreadSheetEntityParser<T>
                ?: throw ParserNotFoundException("Parser for $entityClass not found")
    }
}
