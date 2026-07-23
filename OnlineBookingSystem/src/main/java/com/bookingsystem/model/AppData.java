package com.bookingsystem.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Root object graph that gets persisted to disk as a single unit: the
 * business hours configuration plus every service, customer and
 * reservation currently known to the application.
 */
public class AppData implements Serializable {

    private static final long serialVersionUID = 1L;

    private BusinessHoursConfig businessHoursConfig = new BusinessHoursConfig();
    private List<Service> services = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private List<Reservation> reservations = new ArrayList<>();

    public BusinessHoursConfig getBusinessHoursConfig() {
        return businessHoursConfig;
    }

    public void setBusinessHoursConfig(BusinessHoursConfig businessHoursConfig) {
        this.businessHoursConfig = businessHoursConfig;
    }

    public List<Service> getServices() {
        return services;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }
}
