package com.company.googledrive;

import com.company.googledrive.entity.GDriveEntity;
import com.company.googledrive.entity.User;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.ValueRange;
import one.util.streamex.StreamEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Parser {
    private static Logger LOG = LoggerFactory.getLogger(Parser.class);

    private static final String CLIENT_SECRET_FILE = "client_secret.json";
    private static final String CREDENTIALS_FOLDER = "credentials";
    private static final Collection<String> SCOPES = List.of(SheetsScopes.SPREADSHEETS);

    private static Parser instance = null;

    private final String applicationName;
    private final JsonFactory jsonFactory;
    private final NetHttpTransport httpTransport;
    private final Sheets service;

    private final Map<Class<? extends GDriveEntity>, ? extends EntityInfo> entityInfoMap;

    private Parser() throws GeneralSecurityException, IOException {
        applicationName = "The King's bot";
        jsonFactory = JacksonFactory.getDefaultInstance();
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        service = new Sheets.Builder(httpTransport, jsonFactory, getCredentials(httpTransport))
                .setApplicationName(applicationName)
                .build();
        entityInfoMap = Map.of(User.class, new UserInfo());
    }

    public static Parser getInstance() {
        if (instance == null) {
            try {
                instance = new Parser();
            } catch (GeneralSecurityException | IOException e) {
                LOG.error("Failed to create {} instance", Parser.class.getSimpleName(), e);
            }
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    public <T extends GDriveEntity> List<T> parse(Class<T> entityClass) throws IOException {
        EntityInfo<T> parseInfo = (EntityInfo<T>) entityInfoMap.get(entityClass);
        ValueRange response = service.spreadsheets().values()
                .get(parseInfo.spreadsheetId, parseInfo.sheetId + "!" + parseInfo.range)
                .execute();
        int i = parseInfo.firstRow;
        List<T> result = new ArrayList<>(response.getValues().size());
        for (List<Object> row : response.getValues()) {
            result.add(parseInfo.parseEntity(row, i++));
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public <T extends GDriveEntity> void update(Class<T> entityClass, List<T> entities) throws IOException {
        EntityInfo<T> parseInfo = (EntityInfo<T>) entityInfoMap.get(entityClass);

        service.spreadsheets().values()
                .batchUpdate(parseInfo.spreadsheetId, new BatchUpdateValuesRequest()
                        .setValueInputOption("USER_ENTERED")
                        .setData(StreamEx.of(entities)
                                .map(parseInfo::toValueRange)
                                .toList()))
                .execute();
    }

    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream in = classloader.getResourceAsStream(CLIENT_SECRET_FILE);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, jsonFactory,
                clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(CREDENTIALS_FOLDER)))
                .setAccessType("offline")
                .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }
}
