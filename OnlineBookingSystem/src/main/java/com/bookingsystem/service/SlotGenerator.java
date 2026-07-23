package com.bookingsystem.service;

import com.bookingsystem.model.*;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Computes bookable time slots for a given date and service, entirely
 * from the user-configured {@link BusinessHoursConfig} (opening/closing
 * time per day, slot duration, buffer between slots) and the list of
 * reservations already on the books. This is the heart of the
 * "automatic schedule" requirement: the operator only has to fill in
 * business hours once, and every available slot - plus the time
 * remaining until the very next one - is derived automatically.
 */
public class SlotGenerator {

    /**
     * Returns every free slot on {@code date} that can fully accommodate a
     * service lasting {@code serviceDurationMinutes}, skipping slots that
     * would overlap an existing non-cancelled reservation.
     */
    public List<TimeSlot> generateAvailableSlots(BusinessHoursConfig config, LocalDate date,
                                                  int serviceDurationMinutes, List<Reservation> existingReservations) {
        List<TimeSlot> result = new ArrayList<>();
        DayOfWeek dow = date.getDayOfWeek();
        BusinessDay businessDay = config.getDay(dow);
        if (businessDay == null || businessDay.isClosed()) {
            return result;
        }

        LocalTime open = businessDay.getOpenTime();
        LocalTime close = businessDay.getCloseTime();
        if (open == null || close == null || !open.isBefore(close)) {
            return result;
        }

        int step = config.getSlotDurationMinutes();
        int buffer = config.getBufferMinutes();
        if (step <= 0) {
            step = 30;
        }

        List<Reservation> dayReservations = new ArrayList<>();
        for (Reservation r : existingReservations) {
            if (r.getDate().equals(date) && r.getStatus() != ReservationStatus.CANCELLED) {
                dayReservations.add(r);
            }
        }

        LocalTime cursor = open;
        while (true) {
            LocalTime slotEnd = cursor.plusMinutes(serviceDurationMinutes);
            if (slotEnd.isAfter(close)) {
                break;
            }
            if (!overlapsAny(cursor, slotEnd, dayReservations)) {
                result.add(new TimeSlot(cursor, slotEnd));
            }
            LocalTime next = cursor.plusMinutes(step + buffer);
            if (!next.isAfter(cursor)) {
                break; // safety guard against non-progressing loops
            }
            cursor = next;
        }
        return result;
    }

    private boolean overlapsAny(LocalTime start, LocalTime end, List<Reservation> reservations) {
        for (Reservation r : reservations) {
            if (start.isBefore(r.getEndTime()) && r.getStartTime().isBefore(end)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Scans forward from {@code fromDateTime} across up to
     * {@code maxDaysToScan} days to find the very next slot that can fit
     * the given service, and returns both the slot and how much time
     * remains until it starts.
     */
    public NextSlotResult findNextAvailableSlot(BusinessHoursConfig config, LocalDateTime fromDateTime,
                                                 int serviceDurationMinutes, List<Reservation> existingReservations,
                                                 int maxDaysToScan) {
        for (int i = 0; i < maxDaysToScan; i++) {
            LocalDate candidateDate = fromDateTime.toLocalDate().plusDays(i);
            List<TimeSlot> slots = generateAvailableSlots(config, candidateDate, serviceDurationMinutes, existingReservations);
            for (TimeSlot slot : slots) {
                LocalDateTime slotStart = LocalDateTime.of(candidateDate, slot.getStart());
                if (slotStart.isAfter(fromDateTime) || slotStart.isEqual(fromDateTime)) {
                    Duration untilNext = Duration.between(fromDateTime, slotStart);
                    return new NextSlotResult(candidateDate, slot, untilNext);
                }
            }
        }
        return null;
    }

    /** Simple holder pairing the next free slot with the wait time until it. */
    public static class NextSlotResult {
        public final LocalDate date;
        public final TimeSlot slot;
        public final Duration waitTime;

        public NextSlotResult(LocalDate date, TimeSlot slot, Duration waitTime) {
            this.date = date;
            this.slot = slot;
            this.waitTime = waitTime;
        }

        public String formatWaitTime() {
            long totalMinutes = waitTime.toMinutes();
            long days = totalMinutes / (60 * 24);
            long hours = (totalMinutes % (60 * 24)) / 60;
            long minutes = totalMinutes % 60;
            StringBuilder sb = new StringBuilder();
            if (days > 0) {
                sb.append(days).append("d ");
            }
            if (hours > 0 || days > 0) {
                sb.append(hours).append("h ");
            }
            sb.append(minutes).append("m");
            return sb.toString();
        }
    }
}
