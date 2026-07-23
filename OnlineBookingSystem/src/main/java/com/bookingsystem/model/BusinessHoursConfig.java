package com.bookingsystem.model;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumMap;
import java.util.Map;

/**
 * Holds the full weekly schedule (one {@link BusinessDay} per day of the
 * week) plus the two global scheduling parameters that the user can
 * configure entirely through the UI:
 * <ul>
 *     <li>{@code slotDurationMinutes} - how long each bookable appointment slot is</li>
 *     <li>{@code bufferMinutes} - idle time inserted between consecutive slots</li>
 * </ul>
 * From these values the {@link com.bookingsystem.service.SlotGenerator}
 * computes exactly which time slots are offered on any given date, and
 * how long a user has to wait until the next available slot.
 */
public class BusinessHoursConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<DayOfWeek, BusinessDay> days = new EnumMap<>(DayOfWeek.class);
    private int slotDurationMinutes = 30;
    private int bufferMinutes = 0;

    public BusinessHoursConfig() {
        for (DayOfWeek dow : DayOfWeek.values()) {
            boolean weekendClosed = dow == DayOfWeek.SUNDAY;
            days.put(dow, new BusinessDay(dow.getValue(), LocalTime.of(9, 0), LocalTime.of(18, 0), weekendClosed));
        }
    }

    public BusinessDay getDay(DayOfWeek dayOfWeek) {
        return days.get(dayOfWeek);
    }

    public Map<DayOfWeek, BusinessDay> getAllDays() {
        return days;
    }

    public int getSlotDurationMinutes() {
        return slotDurationMinutes;
    }

    public void setSlotDurationMinutes(int slotDurationMinutes) {
        this.slotDurationMinutes = slotDurationMinutes;
    }

    public int getBufferMinutes() {
        return bufferMinutes;
    }

    public void setBufferMinutes(int bufferMinutes) {
        this.bufferMinutes = bufferMinutes;
    }
}
