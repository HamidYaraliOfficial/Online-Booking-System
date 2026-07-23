package com.bookingsystem.model;

/**
 * Lifecycle status of a {@link Reservation}.
 */
public enum ReservationStatus {
    PENDING("status.pending"),
    CONFIRMED("status.confirmed"),
    COMPLETED("status.completed"),
    CANCELLED("status.cancelled");

    private final String labelKey;

    ReservationStatus(String labelKey) {
        this.labelKey = labelKey;
    }

    public String getLabelKey() {
        return labelKey;
    }
}
