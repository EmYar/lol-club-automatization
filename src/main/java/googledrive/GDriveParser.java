package googledrive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import entity.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.List;

public abstract class GDriveParser<T> {

    private static final String CLIENT_SECRET_FILE = "client_secret.json";
    private static final String CREDENTIALS_FOLDER = "credentials";
    private static final Collection<String> SCOPES = List.of(SheetsScopes.SPREADSHEETS);
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    public T parse() throws ParsingException {
        try {
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            Sheets service = getSheetsService(httpTransport, getCredentials(httpTransport));
            return parse(service);
        } catch (GeneralSecurityException | IOException e) {
            throw new ParsingException(e);
        }
    }

    public T parse(Sheets service) throws ParsingException {
        Pair<String, String> docInfo = getDocInfo();
        String spreadsheetId = docInfo.getFirst();
        String list = docInfo.getSecond();

        try {
            ValueRange response = service.spreadsheets().values()
                    .get(spreadsheetId, list + "!" + getRange())
                    .execute();
            List<List<Object>> values = response.getValues();

            if (values == null || values.isEmpty()) {
                throw new ParsingException("No data parsed");
            } else {
                return parse(values, service);
            }
        } catch (IOException e) {
            throw new ParsingException(e);
        }
    }

    protected abstract Pair<String, String> getDocInfo();
    protected abstract String getRange();
    protected abstract T parse(List<List<Object>> rows, Sheets service) throws ParsingException;

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream in = classloader.getResourceAsStream(CLIENT_SECRET_FILE);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(GDriveParser.JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, GDriveParser.JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(GDriveParser.CREDENTIALS_FOLDER)))
                .setAccessType("offline")
                .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    private Sheets getSheetsService(HttpTransport httpTransport, Credential credential) {
        return new Sheets.Builder(httpTransport, JSON_FACTORY, credential).build();
    }
}
