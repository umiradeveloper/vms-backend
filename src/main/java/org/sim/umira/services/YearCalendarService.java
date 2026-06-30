package org.sim.umira.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

public class YearCalendarService {
    public static class DayInfo {
        public LocalDate date;
        public String type; // GREEN / GRAY / RED
        public String status;
    }

    public static List<DayInfo> generateYear(
            int year,
            Set<LocalDate> holidays) {

        List<DayInfo> result = new ArrayList<>();

        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {

            DayInfo info = new DayInfo();
            info.date = date;

            boolean weekend = date.getDayOfWeek() == DayOfWeek.SATURDAY ||
                    date.getDayOfWeek() == DayOfWeek.SUNDAY;

            boolean holiday = holidays.contains(date);

            if (holiday) {
                info.type = "RED"; // Libur Nasional
                info.status = "Libur Nasional";
            } else if (weekend) {
                info.type = "GRAY"; // Weekend
                info.status = "Weekend";
            } else {
                info.type = "GREEN"; // Workday
                info.status = "Work";
            }

            result.add(info);
        }

        return result;
    }

    public static Set<LocalDate> getHolidays(Calendar service, String calendarId)
            throws Exception {

        Set<LocalDate> holidays = new HashSet<>();

        Events events = service.events()
                .list(calendarId)
                .setTimeMin(new DateTime("2026-01-01T00:00:00Z"))
                .setTimeMax(new DateTime("2026-12-31T23:59:59Z"))
                .execute();

        for (Event e : events.getItems()) {
            if (e.getStart().getDate() != null) {
                holidays.add(LocalDate.parse(
                        e.getStart().getDate().toString()));
            }
        }

        return holidays;
    }

     public static Set<LocalDate> getHolidaysByParams(Calendar service, String calendarId, String start_date, String end_date)
            throws Exception {

        Set<LocalDate> holidays = new HashSet<>();

        Events events = service.events()
                .list(calendarId)
                .setTimeMin(new DateTime(start_date+"T00:00:00Z"))
                .setTimeMax(new DateTime(end_date+"T23:59:59Z"))
                .execute();

        for (Event e : events.getItems()) {
            if (e.getStart().getDate() != null) {
                holidays.add(LocalDate.parse(
                        e.getStart().getDate().toString()));
            }
        }

        return holidays;
    }

    public static List<DayInfo> generatedDay(
            int year,
            Set<LocalDate> holidays, String startDate, String endDate) {

        List<DayInfo> result = new ArrayList<>();

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {

            DayInfo info = new DayInfo();
            info.date = date;

            boolean weekend = date.getDayOfWeek() == DayOfWeek.SATURDAY ||
                    date.getDayOfWeek() == DayOfWeek.SUNDAY;

            boolean holiday = holidays.contains(date);

            if (holiday) {
                info.type = "RED"; // Libur Nasional
                info.status = "Libur Nasional";
            } else if (weekend) {
                info.type = "GRAY"; // Weekend
                info.status = "Weekend";
            } else {
                info.type = "GREEN"; // Workday
                info.status = "Work";
            }

            result.add(info);
        }

        return result;
    }

    
}
