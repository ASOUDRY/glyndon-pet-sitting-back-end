package com.soudry.pet_sitting.Services;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class GoogleCalendarService {

    private static final String APPLICATION_NAME =
            "Glyndon Pet Services Backend";

    private static final JsonFactory JSON_FACTORY =
            GsonFactory.getDefaultInstance();

    private static final List<String> SCOPES = List.of(
            "https://www.googleapis.com/auth/calendar.events.readonly"
    );

    private final Calendar calendar;
    private final String calendarId;

    public GoogleCalendarService(
            @Value("${app.google-calendar-id}") String calendarId
    ) throws GeneralSecurityException, IOException {

        this.calendarId = calendarId;

        NetHttpTransport httpTransport =
                GoogleNetHttpTransport.newTrustedTransport();

        GoogleCredentials credentials =
                GoogleCredentials
                        .getApplicationDefault()
                        .createScoped(SCOPES);

        HttpRequestInitializer requestInitializer =
                new HttpCredentialsAdapter(credentials);

        this.calendar = new Calendar.Builder(
                httpTransport,
                JSON_FACTORY,
                requestInitializer
        )
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public List<Event> getEventsForMonth(
            int year,
            int month
    ) throws IOException {

        YearMonth requestedMonth =
                YearMonth.of(year, month);

        ZoneId zone =
                ZoneId.of("America/New_York");

        ZonedDateTime start =
                requestedMonth
                        .atDay(1)
                        .atStartOfDay(zone);

        ZonedDateTime end =
                requestedMonth
                        .plusMonths(1)
                        .atDay(1)
                        .atStartOfDay(zone);

        DateTime timeMin =
                new DateTime(
                        start.toInstant().toEpochMilli()
                );

        DateTime timeMax =
                new DateTime(
                        end.toInstant().toEpochMilli()
                );

        Events events =
                calendar
                        .events()
                        .list(calendarId)
                        .setTimeMin(timeMin)
                        .setTimeMax(timeMax)
                        .setSingleEvents(true)
                        .setOrderBy("startTime")
                        .execute();

        return events.getItems();
    }
}