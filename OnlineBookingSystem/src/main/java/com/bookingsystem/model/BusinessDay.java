package com.bookingsystem.model;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * Represents the configured working hours for a single day of the week.
 * When {@code closed} is true the business does not accept any
 * reservation on that day regardless of the open/close values.
 */
public class BusinessDay implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int dayOfWeek; // 1 = Monday ... 7 = Sunday (java.time.DayOfWeek convention)
    private LocalTime openTime;
    private LocalTime closeTime;
    private boolean closed;

    public BusinessDay(int dayOfWeek, LocalTime openTime, LocalTime closeTime, boolean closed) {
        this.dayOfWeek = dayOfWeek;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.closed = closed;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getOpenTime() {
        return openTime;
    }

    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }

    public LocalTime getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(LocalTime closeTime) {
        this.closeTime = closeTime;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
