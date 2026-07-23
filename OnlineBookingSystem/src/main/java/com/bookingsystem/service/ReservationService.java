package com.bookingsystem.service;

import com.bookingsystem.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Central facade the whole UI talks to: owns the in-memory {@link AppData},
 * persists it through {@link DataStore}, and exposes intention-revealing
 * operations (book, cancel, add service, ...) instead of leaking raw
 * collections everywhere.
 */
public class ReservationService {

    private final DataStore dataStore = new DataStore();
    private final SlotGenerator slotGenerator = new SlotGenerator();
    private AppData data;

    public ReservationService() {
        this.data = dataStore.load();
    }

    public void save() {
        dataStore.save(data);
    }

    // ----- Business hours -----

    public BusinessHoursConfig getBusinessHours() {
        return data.getBusinessHoursConfig();
    }

    // ----- Services -----

    public List<Service> getServices() {
        return data.getServices();
    }

    public List<Service> getActiveServices() {
        List<Service> active = new ArrayList<>();
        for (Service s : data.getServices()) {
            if (s.isActive()) {
                active.add(s);
            }
        }
        return active;
    }

    public void addService(Service service) {
        data.getServices().add(service);
    }

    public void removeService(Service service) {
        data.getServices().remove(service);
    }

    // ----- Customers -----

    public List<Customer> getCustomers() {
        return data.getCustomers();
    }

    public void addCustomer(Customer customer) {
        data.getCustomers().add(customer);
    }

    public void removeCustomer(Customer customer) {
        data.getCustomers().remove(customer);
    }

    // ----- Reservations -----

    public List<Reservation> getReservations() {
        return data.getReservations();
    }

    public List<Reservation> getReservationsForDate(LocalDate date) {
        List<Reservation> list = new ArrayList<>();
        for (Reservation r : data.getReservations()) {
            if (r.getDate().equals(date)) {
                list.add(r);
            }
        }
        return list;
    }

    public List<TimeSlot> getAvailableSlots(LocalDate date, Service service) {
        return slotGenerator.generateAvailableSlots(data.getBusinessHoursConfig(), date,
                service.getDurationMinutes(), data.getReservations());
    }

    public SlotGenerator.NextSlotResult findNextAvailableSlot(Service service, LocalDateTime from) {
        return slotGenerator.findNextAvailableSlot(data.getBusinessHoursConfig(), from,
                service.getDurationMinutes(), data.getReservations(), 60);
    }

    public boolean isSlotStillFree(LocalDate date, TimeSlot slot) {
        for (Reservation r : data.getReservations()) {
            if (r.getDate().equals(date) && r.getStatus() != ReservationStatus.CANCELLED
                    && slot.getStart().isBefore(r.getEndTime()) && r.getStartTime().isBefore(slot.getEnd())) {
                return false;
            }
        }
        return true;
    }

    public Reservation book(Customer customer, Service service, LocalDate date, TimeSlot slot, String notes) {
        Reservation reservation = new Reservation(customer, service, date, slot.getStart(), slot.getEnd(), notes);
        data.getReservations().add(reservation);
        return reservation;
    }

    public void cancelReservation(Reservation reservation) {
        reservation.setStatus(ReservationStatus.CANCELLED);
    }

    public void completeReservation(Reservation reservation) {
        reservation.setStatus(ReservationStatus.COMPLETED);
    }

    public Optional<Customer> findCustomerById(String id) {
        for (Customer c : data.getCustomers()) {
            if (c.getId().equals(id)) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }

    public int countTodayReservations() {
        LocalDate today = LocalDate.now();
        int count = 0;
        for (Reservation r : data.getReservations()) {
            if (r.getDate().equals(today) && r.getStatus() != ReservationStatus.CANCELLED) {
                count++;
            }
        }
        return count;
    }

    public int countUpcomingReservations() {
        LocalDate today = LocalDate.now();
        int count = 0;
        for (Reservation r : data.getReservations()) {
            if (!r.getDate().isBefore(today) && r.getStatus() != ReservationStatus.CANCELLED) {
                count++;
            }
        }
        return count;
    }
}
