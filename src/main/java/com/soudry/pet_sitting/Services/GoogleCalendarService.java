package com.soudry.pet_sitting.Services;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

@Service
public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "Glyndon Pet Services Backend";

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static final List<String> SCOPES = List.of( "https://www.googleapis.com/auth/calendar.events.owned.readonly");

    private Calendar calendar;

    public GoogleCalendarService() throws GeneralSecurityException, IOException {

        NetHttpTransport httpTransport =
                GoogleNetHttpTransport.newTrustedTransport();

        Credential credential = authorize(httpTransport);

        this.calendar = new Calendar.Builder(
                httpTransport,
                JSON_FACTORY,
                credential
        )
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private Credential authorize(NetHttpTransport httpTransport) throws IOException {

        InputStream credentialsStream =
                getClass()
                        .getClassLoader()
                        .getResourceAsStream("credentials.json");

        if (credentialsStream == null) {
            throw new RuntimeException(
                    "credentials.json not found in resources folder"
            );
        }

        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(
                        JSON_FACTORY,
                        new InputStreamReader(credentialsStream)
                );

        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        httpTransport,
                        JSON_FACTORY,
                        clientSecrets,
                        SCOPES
                )
                        .setDataStoreFactory(
                                new FileDataStoreFactory(
                                        new java.io.File(TOKENS_DIRECTORY_PATH)
                                )
                        )
                        .setAccessType("offline")
                        .build();

        LocalServerReceiver receiver =
                new LocalServerReceiver.Builder()
                        .setPort(8888)
                        .build();

        return new AuthorizationCodeInstalledApp(
                flow,
                receiver
        ).authorize("user");
    }

    public List<Event> getEventsForMonth(int year, int month) throws IOException {

    YearMonth requestedMonth = YearMonth.of(year, month);

    ZoneId zone = ZoneId.of("America/New_York");

    ZonedDateTime start = requestedMonth
            .atDay(1)
            .atStartOfDay(zone);

    ZonedDateTime end = requestedMonth
            .plusMonths(1)
            .atDay(1)
            .atStartOfDay(zone);

    DateTime timeMin = new DateTime(
            start.toInstant().toEpochMilli()
    );

    DateTime timeMax = new DateTime(
            end.toInstant().toEpochMilli()
    );

    Events events = calendar
            .events()
            .list("primary")
            .setTimeMin(timeMin)
            .setTimeMax(timeMax)
            .setSingleEvents(true)
            .setOrderBy("startTime")
            .execute();

    return events.getItems();
}
}
