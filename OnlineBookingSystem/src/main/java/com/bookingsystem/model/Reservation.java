package com.bookingsystem.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

/**
 * A confirmed or pending appointment binding a {@link Customer} to a
 * {@link Service} at a specific date and time window.
 */
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private String customerId;
    private String customerNameSnapshot;
    private String serviceId;
    private String serviceNameSnapshot;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String notes;
    private ReservationStatus status;

    public Reservation(Customer customer, Service service, LocalDate date, LocalTime startTime, LocalTime endTime, String notes) {
        this.id = UUID.randomUUID().toString();
        this.customerId = customer.getId();
        this.customerNameSnapshot = customer.getName();
        this.serviceId = service.getId();
        this.serviceNameSnapshot = service.getName();
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.notes = notes;
        this.status = ReservationStatus.CONFIRMED;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerNameSnapshot() {
        return customerNameSnapshot;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getServiceNameSnapshot() {
        return serviceNameSnapshot;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getNotes() {
        return notes;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
}
