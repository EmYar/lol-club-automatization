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
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Parser {
    private static final String APPLICATION_NAME = "The King's bot";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static Map<Class<? extends GDriveEntity>, ? extends EntityParseInfo<? extends GDriveEntity>> map =
            Map.of(User.class, new UserParseInfo());

    public static <T extends GDriveEntity> List<T> parse(Class<T> entityClass) throws GeneralSecurityException, IOException {
        EntityParseInfo<T> parseInfo = (EntityParseInfo<T>) map.get(entityClass);
        NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, parseInfo))
                .setApplicationName(APPLICATION_NAME)
                .build();
        ValueRange response = service.spreadsheets().values()
                .get(parseInfo.spreadsheetId, parseInfo.sheetId + "!" + parseInfo.range)
                .execute();
        int i = 0;
        List<T> result = new ArrayList<>(response.getValues().size());
        for (List<Object> row : response.getValues()) {
            result.add(parseInfo.parseEntity(row, i++));
        }

        return result;
    }

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, EntityParseInfo parseInfo) throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream in = classloader.getResourceAsStream(parseInfo.clientSecretFile);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                clientSecrets, parseInfo.scopes)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(parseInfo.credentialsFolder)))
                .setAccessType("offline")
                .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }
}
