package com.company.googledrive

import com.company.googledrive.entity.GDriveEntity
import com.company.googledrive.entity.User
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

//todo emelyanov rename
object Parser {
    private const val CLIENT_SECRET_FILE = "client_secret.json"
    private const val CREDENTIALS_FOLDER = "credentials"
    private val SCOPES = listOf(SheetsScopes.SPREADSHEETS)

    private val jsonFactory: JsonFactory
    private val service: Sheets

    private val ENTITY_PARSERS_MAP: Map<KClass<out GDriveEntity>, EntityParser<*>>

    init {
        val applicationName = "The King's bot" //todo emelyanov: move to resources
        jsonFactory = JacksonFactory.getDefaultInstance()
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        service = Sheets.Builder(httpTransport, jsonFactory, getCredentials(httpTransport))
                .setApplicationName(applicationName)
                .build()
        ENTITY_PARSERS_MAP = mapOf(User::class to UserParser())

    }

    @Throws(IOException::class)
    fun <T : GDriveEntity> get(entityClass: KClass<T>): List<T> {
        val parseInfo = getEntityParser(entityClass)
        val response = service.spreadsheets().values()
                .get(parseInfo.spreadsheetId, parseInfo.sheetId + "!" + parseInfo.range)
                .execute()

        var i = parseInfo.firstRow

        return response.getValues()
                .map { parseInfo.parseEntity(it, i++) }
                .toList();
    }

    @Throws(IOException::class)
    fun <T : GDriveEntity> update(entityClass: KClass<T>, entities: List<T>) {
        val parseInfo = getEntityParser(entityClass)

        service.spreadsheets().values()
                .batchUpdate(parseInfo.spreadsheetId, BatchUpdateValuesRequest()
                        .setValueInputOption("USER_ENTERED")
                        .setData(entities.map { parseInfo.toValueRange(it) }.toList()))
                .execute()
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
    private fun <T : GDriveEntity> getEntityParser(config: KClass<T>): EntityParser<T> {
        return ENTITY_PARSERS_MAP[config] as EntityParser<T>
    }
}
