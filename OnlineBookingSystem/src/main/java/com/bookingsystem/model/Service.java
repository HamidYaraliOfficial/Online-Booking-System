package com.bookingsystem.model;

import java.io.Serializable;
import java.util.UUID;

/**
 * A bookable service offered by the business (e.g. "Haircut", "Consultation").
 * Its {@code durationMinutes} directly drives how many slots a reservation
 * for this service will occupy on the schedule.
 */
public class Service implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private String name;
    private int durationMinutes;
    private double price;
    private boolean active;

    public Service(String name, int durationMinutes, double price) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.durationMinutes = durationMinutes;
        this.price = price;
        this.active = true;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return name + " (" + durationMinutes + " min)";
    }
}
