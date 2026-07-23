package com.bookingsystem.gui.components;

import com.bookingsystem.i18n.Language;
import com.bookingsystem.i18n.LanguageManager;
import com.bookingsystem.model.Customer;
import com.bookingsystem.model.Service;
import com.bookingsystem.model.TimeSlot;
import com.bookingsystem.service.ReservationService;
import com.bookingsystem.service.SlotGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

/**
 * Tab where an operator creates a new reservation. Choosing a service and
 * a date automatically (re)computes every open time slot for that day
 * from the business-hours configuration, and separately shows the very
 * next available slot system-wide with a countdown of how long the wait
 * is - fully derived, nothing hardcoded.
 */
public class NewReservationPanel extends JPanel implements LanguageManager.LanguageChangeListener {

    private final ReservationService reservationService;
    private final Consumer<String> statusConsumer;
    private final Runnable onBooked;

    private final JComboBox<Service> serviceCombo = new JComboBox<>();
    private final JComboBox<Customer> customerCombo = new JComboBox<>();
    private final JTextField newCustomerName = new JTextField();
    private final JTextField newCustomerPhone = new JTextField();
    private final JTextField newCustomerEmail = new JTextField();
    private final JSpinner dateSpinner;
    private final DefaultListModel<TimeSlot> slotListModel = new DefaultListModel<>();
    private final JList<TimeSlot> slotList = new JList<>(slotListModel);
    private final JTextArea notesArea = new JTextArea(3, 20);
    private final JLabel nextAvailableLabel = new JLabel();
    private final JButton bookButton = new JButton();
    private final JButton clearButton = new JButton();

    private final JLabel serviceLabel = new JLabel();
    private final JLabel customerLabel = new JLabel();
    private final JLabel newCustomerLabel = new JLabel();
    private final JLabel nameLabel = new JLabel();
    private final JLabel phoneLabel = new JLabel();
    private final JLabel emailLabel = new JLabel();
    private final JLabel dateLabel = new JLabel();
    private final JLabel slotsLabel = new JLabel();
    private final JLabel notesLabel = new JLabel();
    private final JLabel nextAvailableTitle = new JLabel();

    private final TitledBorder formBorder;

    public NewReservationPanel(ReservationService reservationService, Consumer<String> statusConsumer, Runnable onBooked) {
        this.reservationService = reservationService;
        this.statusConsumer = statusConsumer;
        this.onBooked = onBooked;

        setLayout(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(14, 14, 14, 14));

        LanguageManager lm = LanguageManager.getInstance();
        formBorder = BorderFactory.createTitledBorder(lm.t("tab.newBooking"));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(formBorder);

        form.add(fieldRow(serviceLabel, serviceCombo));
        form.add(Box.createVerticalStrut(10));
        form.add(fieldRow(customerLabel, customerCombo));
        form.add(Box.createVerticalStrut(6));

        JPanel newCustomerPanel = new JPanel(new GridLayout(3, 2, 6, 6));
        newCustomerPanel.setBorder(BorderFactory.createTitledBorder(""));
        newCustomerPanel.add(nameLabel);
        newCustomerPanel.add(newCustomerName);
        newCustomerPanel.add(phoneLabel);
        newCustomerPanel.add(newCustomerPhone);
        newCustomerPanel.add(emailLabel);
        newCustomerPanel.add(newCustomerEmail);
        form.add(newCustomerPanel);
        form.add(Box.createVerticalStrut(10));

        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        form.add(fieldRow(dateLabel, dateSpinner));
        form.add(Box.createVerticalStrut(10));

        form.add(slotsLabel);
        slotList.setVisibleRowCount(6);
        slotList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane slotScroll = new JScrollPane(slotList);
        slotScroll.setPreferredSize(new Dimension(260, 130));
        form.add(slotScroll);
        form.add(Box.createVerticalStrut(10));

        form.add(notesLabel);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        form.add(new JScrollPane(notesArea));
        form.add(Box.createVerticalStrut(14));

        JPanel nextAvailablePanel = new JPanel(new BorderLayout());
        nextAvailablePanel.setBorder(BorderFactory.createEtchedBorder());
        nextAvailableTitle.setFont(nextAvailableTitle.getFont().deriveFont(Font.BOLD));
        nextAvailablePanel.add(nextAvailableTitle, BorderLayout.NORTH);
        nextAvailablePanel.add(nextAvailableLabel, BorderLayout.CENTER);
        form.add(nextAvailablePanel);
        form.add(Box.createVerticalStrut(14));

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        bookButton.setFont(bookButton.getFont().deriveFont(Font.BOLD, 14f));
        bookButton.addActionListener(e -> onBookClicked());
        clearButton.addActionListener(e -> clearForm());
        buttonRow.add(bookButton);
        buttonRow.add(clearButton);
        form.add(buttonRow);

        JScrollPane outerScroll = new JScrollPane(form);
        outerScroll.setBorder(null);
        add(outerScroll, BorderLayout.CENTER);

        serviceCombo.addActionListener(e -> refreshSlots());
        dateSpinner.addChangeListener(e -> refreshSlots());

        applyLabels();
        refreshServiceCombo();
        refreshCustomerCombo();
        refreshSlots();

        lm.addListener(this);
    }

    private JPanel fieldRow(JLabel label, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(6, 4));
        row.add(label, BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);
        return row;
    }

    public void refreshServiceCombo() {
        Service selected = (Service) serviceCombo.getSelectedItem();
        serviceCombo.removeAllItems();
        for (Service s : reservationService.getActiveServices()) {
            serviceCombo.addItem(s);
        }
        if (selected != null) {
            serviceCombo.setSelectedItem(selected);
        }
        refreshSlots();
    }

    public void refreshCustomerCombo() {
        Customer selected = (Customer) customerCombo.getSelectedItem();
        customerCombo.removeAllItems();
        for (Customer c : reservationService.getCustomers()) {
            customerCombo.addItem(c);
        }
        if (selected != null) {
            customerCombo.setSelectedItem(selected);
        }
    }

    private LocalDate getSelectedDate() {
        Date date = (Date) dateSpinner.getValue();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void refreshSlots() {
        LanguageManager lm = LanguageManager.getInstance();
        slotListModel.clear();
        Service service = (Service) serviceCombo.getSelectedItem();
        if (service == null) {
            nextAvailableLabel.setText("-");
            return;
        }
        LocalDate date = getSelectedDate();
        List<TimeSlot> slots = reservationService.getAvailableSlots(date, service);
        for (TimeSlot slot : slots) {
            slotListModel.addElement(slot);
        }
        if (slots.isEmpty()) {
            boolean closed = reservationService.getBusinessHours().getDay(date.getDayOfWeek()).isClosed();
            statusConsumer.accept(closed ? lm.t("status.closedOnDate") : lm.t("status.noSlotsAvailable"));
        }

        SlotGenerator.NextSlotResult next = reservationService.findNextAvailableSlot(service, LocalDateTime.now());
        if (next != null) {
            nextAvailableLabel.setText(next.date + "  " + next.slot + "   (" + next.formatWaitTime() + ")");
        } else {
            nextAvailableLabel.setText("-");
        }
    }

    private void onBookClicked() {
        LanguageManager lm = LanguageManager.getInstance();
        Service service = (Service) serviceCombo.getSelectedItem();
        if (service == null) {
            statusConsumer.accept(lm.t("status.noServiceSelected"));
            return;
        }

        Customer customer = resolveCustomer();
        if (customer == null) {
            statusConsumer.accept(lm.t("status.noCustomer"));
            return;
        }

        TimeSlot slot = slotList.getSelectedValue();
        if (slot == null) {
            statusConsumer.accept(lm.t("status.noSlotSelected"));
            return;
        }

        LocalDate date = getSelectedDate();
        if (!reservationService.isSlotStillFree(date, slot)) {
            statusConsumer.accept(lm.t("status.slotUnavailable"));
            refreshSlots();
            return;
        }

        reservationService.book(customer, service, date, slot, notesArea.getText());
        statusConsumer.accept(lm.t("status.bookingCreated"));
        clearForm();
        refreshSlots();
        onBooked.run();
    }

    private Customer resolveCustomer() {
        Customer selected = (Customer) customerCombo.getSelectedItem();
        if (selected != null) {
            return selected;
        }
        String name = newCustomerName.getText().trim();
        if (name.isEmpty()) {
            return null;
        }
        Customer customer = new Customer(name, newCustomerPhone.getText().trim(), newCustomerEmail.getText().trim());
        reservationService.addCustomer(customer);
        refreshCustomerCombo();
        customerCombo.setSelectedItem(customer);
        return customer;
    }

    private void clearForm() {
        newCustomerName.setText("");
        newCustomerPhone.setText("");
        newCustomerEmail.setText("");
        notesArea.setText("");
        customerCombo.setSelectedItem(null);
    }

    private void applyLabels() {
        LanguageManager lm = LanguageManager.getInstance();
        formBorder.setTitle(lm.t("tab.newBooking"));
        serviceLabel.setText(lm.t("label.service"));
        customerLabel.setText(lm.t("label.customer") + " (" + lm.t("label.newCustomer") + " ↓)");
        nameLabel.setText(lm.t("label.name"));
        phoneLabel.setText(lm.t("label.phone"));
        emailLabel.setText(lm.t("label.email"));
        dateLabel.setText(lm.t("label.date"));
        slotsLabel.setText(lm.t("label.availableSlots"));
        notesLabel.setText(lm.t("label.notes"));
        nextAvailableTitle.setText(lm.t("label.nextAvailable"));
        bookButton.setText(lm.t("button.book"));
        clearButton.setText(lm.t("button.clear"));
        repaint();
    }

    @Override
    public void onLanguageChanged(Language newLanguage) {
        applyLabels();
        applyComponentOrientation(newLanguage.isRtl()
                ? ComponentOrientation.RIGHT_TO_LEFT
                : ComponentOrientation.LEFT_TO_RIGHT);
        SwingUtilities.updateComponentTreeUI(this);
    }
}
