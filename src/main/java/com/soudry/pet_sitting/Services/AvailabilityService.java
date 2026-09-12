package com.soudry.pet_sitting.Services;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.soudry.pet_sitting.objects.BookedTimeSlot;
import com.soudry.pet_sitting.objects.DailyAvailability;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AvailabilityService {

    private final GoogleCalendarService googleCalendarService;

    private static final ZoneId ZONE =
            ZoneId.of("America/New_York");

    private static final int MAX_BOOKED_TIMES_PER_DAY = 5;

    public AvailabilityService(
            GoogleCalendarService googleCalendarService
    ) {
        this.googleCalendarService = googleCalendarService;
    }

    public Map<LocalDate, DailyAvailability> getAvailabilityForMonth(
            int year,
            int month
    ) throws IOException {

        Map<LocalDate, DailyAvailability> availabilityMap =
                createAvailabilityMap(year, month);

        List<Event> events =
                googleCalendarService.getEventsForMonth(year, month);

        for (Event event : events) {

            if (isAllDayEvent(event)) {
                processAllDayEvent(event, availabilityMap);
            } else {
                processTimedEvent(event, availabilityMap);
            }
        }

        return availabilityMap;
    }

    private Map<LocalDate, DailyAvailability> createAvailabilityMap(
            int year,
            int month
    ) {

        Map<LocalDate, DailyAvailability> availabilityMap =
                new LinkedHashMap<>();

        YearMonth requestedMonth =
                YearMonth.of(year, month);

        for (
                int day = 1;
                day <= requestedMonth.lengthOfMonth();
                day++
        ) {

            LocalDate date =
                    requestedMonth.atDay(day);

            availabilityMap.put(
                    date,
                    new DailyAvailability(date)
            );
        }

        return availabilityMap;
    }

    private boolean isAllDayEvent(Event event) {

        return event.getStart() != null
                && event.getStart().getDate() != null;
    }

    private void processAllDayEvent(
            Event event,
            Map<LocalDate, DailyAvailability> availabilityMap
    ) {

        String serviceType =
                getServiceType(event);

        if (!"HOUSE_SITTING".equals(serviceType)) {
            return;
        }

        LocalDate startDate =
                LocalDate.parse(
                        event.getStart()
                                .getDate()
                                .toString()
                );

        LocalDate endDate =
                LocalDate.parse(
                        event.getEnd()
                                .getDate()
                                .toString()
                );

        for (
                LocalDate date = startDate;
                date.isBefore(endDate);
                date = date.plusDays(1)
        ) {

            DailyAvailability dailyAvailability =
                    availabilityMap.get(date);

            if (dailyAvailability != null) {
                dailyAvailability
                        .setHouseSittingAvailable(false);
            }
        }
    }

    private void processTimedEvent(
            Event event,
            Map<LocalDate, DailyAvailability> availabilityMap
    ) {

        String serviceType =
                getServiceType(event);

        if (
                !"DROP_IN".equals(serviceType)
                && !"PERSONAL".equals(serviceType)
        ) {
            return;
        }

        DateTime googleStart =
                event.getStart().getDateTime();

        DateTime googleEnd =
                event.getEnd().getDateTime();

        if (googleStart == null || googleEnd == null) {
            return;
        }

        Instant startInstant =
                Instant.ofEpochMilli(
                        googleStart.getValue()
                );

        Instant endInstant =
                Instant.ofEpochMilli(
                        googleEnd.getValue()
                );

        LocalDate date =
                startInstant
                        .atZone(ZONE)
                        .toLocalDate();

        LocalTime startTime =
                startInstant
                        .atZone(ZONE)
                        .toLocalTime();

        LocalTime endTime =
                endInstant
                        .atZone(ZONE)
                        .toLocalTime();

        DailyAvailability dailyAvailability =
                availabilityMap.get(date);

        if (dailyAvailability == null) {
            return;
        }

        BookedTimeSlot bookedTimeSlot =
                new BookedTimeSlot(
                        startTime,
                        endTime,
                        serviceType
                );

        dailyAvailability.addBookedTime(
                bookedTimeSlot
        );

        if (
                dailyAvailability
                        .getBookedTimes()
                        .size()
                        >= MAX_BOOKED_TIMES_PER_DAY
        ) {

            dailyAvailability
                    .setDropInsAvailable(false);
        }
    }

    private String getServiceType(Event event) {

        String summary =
                event.getSummary();

        if (summary == null) {
            return null;
        }

        String upperSummary =
                summary
                        .trim()
                        .toUpperCase();

        if (upperSummary.startsWith("HOUSE_SITTING")) {
            return "HOUSE_SITTING";
        }

        if (upperSummary.startsWith("DROP_IN")) {
            return "DROP_IN";
        }

        if (upperSummary.startsWith("PERSONAL")) {
            return "PERSONAL";
        }

        return null;
    }
}