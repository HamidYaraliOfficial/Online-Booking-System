package com.bookingsystem.gui.components;

import com.bookingsystem.i18n.Language;
import com.bookingsystem.i18n.LanguageManager;
import com.bookingsystem.model.BusinessDay;
import com.bookingsystem.model.BusinessHoursConfig;
import com.bookingsystem.service.ReservationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Tab where the operator fully configures the weekly schedule: for every
 * day of the week, the opening time, the closing time and whether the
 * business is closed that day; plus the global slot duration and buffer
 * time between consecutive appointments. Every value the New Reservation
 * tab uses to compute free slots and the "next available slot" countdown
 * comes from here.
 */
public class BusinessHoursPanel extends JPanel implements LanguageManager.LanguageChangeListener {

    private final ReservationService reservationService;
    private final Consumer<String> statusConsumer;
    private final Runnable onSaved;

    private final Map<DayOfWeek, JLabel> dayLabels = new LinkedHashMap<>();
    private final Map<DayOfWeek, JSpinner> openSpinners = new LinkedHashMap<>();
    private final Map<DayOfWeek, JSpinner> closeSpinners = new LinkedHashMap<>();
    private final Map<DayOfWeek, JCheckBox> closedChecks = new LinkedHashMap<>();

    private final JSpinner slotDurationSpinner = new JSpinner(new SpinnerNumberModel(30, 5, 480, 5));
    private final JSpinner bufferSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 120, 5));

    private final JLabel slotDurationLabel = new JLabel();
    private final JLabel bufferLabel = new JLabel();
    private final JLabel openHeader = new JLabel();
    private final JLabel closeHeader = new JLabel();
    private final JLabel closedHeader = new JLabel();
    private final JButton saveButton = new JButton();

    private final TitledBorder daysBorder;
    private final TitledBorder globalBorder;

    public BusinessHoursPanel(ReservationService reservationService, Consumer<String> statusConsumer, Runnable onSaved) {
        this.reservationService = reservationService;
        this.statusConsumer = statusConsumer;
        this.onSaved = onSaved;

        setLayout(new BorderLayout(14, 14));
        setBorder(new EmptyBorder(14, 14, 14, 14));

        LanguageManager lm = LanguageManager.getInstance();
        daysBorder = BorderFactory.createTitledBorder(lm.t("tab.hours"));

        JPanel daysPanel = new JPanel(new GridLayout(8, 4, 10, 8));
        daysPanel.setBorder(daysBorder);

        daysPanel.add(new JLabel());
        daysPanel.add(openHeader);
        daysPanel.add(closeHeader);
        daysPanel.add(closedHeader);

        for (DayOfWeek dow : DayOfWeek.values()) {
            JLabel label = new JLabel();
            dayLabels.put(dow, label);
            daysPanel.add(label);

            JSpinner openSpinner = createTimeSpinner();
            openSpinners.put(dow, openSpinner);
            daysPanel.add(openSpinner);

            JSpinner closeSpinner = createTimeSpinner();
            closeSpinners.put(dow, closeSpinner);
            daysPanel.add(closeSpinner);

            JCheckBox closedCheck = new JCheckBox();
            closedChecks.put(dow, closedCheck);
            daysPanel.add(closedCheck);
        }

        add(daysPanel, BorderLayout.CENTER);

        globalBorder = BorderFactory.createTitledBorder("");
        JPanel globalPanel = new JPanel(new GridBagLayout());
        globalPanel.setBorder(globalBorder);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        globalPanel.add(slotDurationLabel, gbc);
        gbc.gridx = 1;
        globalPanel.add(slotDurationSpinner, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        globalPanel.add(bufferLabel, gbc);
        gbc.gridx = 3;
        globalPanel.add(bufferSpinner, gbc);

        gbc.gridx = 4; gbc.gridy = 0;
        saveButton.setFont(saveButton.getFont().deriveFont(Font.BOLD));
        saveButton.addActionListener(e -> onSave());
        globalPanel.add(saveButton, gbc);

        add(globalPanel, BorderLayout.SOUTH);

        applyLabels();
        loadFromConfig();
        lm.addListener(this);
    }

    private JSpinner createTimeSpinner() {
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(model);
        spinner.setEditor(new JSpinner.DateEditor(spinner, "HH:mm"));
        return spinner;
    }

    private void loadFromConfig() {
        BusinessHoursConfig config = reservationService.getBusinessHours();
        for (DayOfWeek dow : DayOfWeek.values()) {
            BusinessDay day = config.getDay(dow);
            openSpinners.get(dow).setValue(toDate(day.getOpenTime()));
            closeSpinners.get(dow).setValue(toDate(day.getCloseTime()));
            closedChecks.get(dow).setSelected(day.isClosed());
        }
        slotDurationSpinner.setValue(config.getSlotDurationMinutes());
        bufferSpinner.setValue(config.getBufferMinutes());
    }

    private void onSave() {
        LanguageManager lm = LanguageManager.getInstance();
        BusinessHoursConfig config = reservationService.getBusinessHours();

        for (DayOfWeek dow : DayOfWeek.values()) {
            LocalTime open = toLocalTime((Date) openSpinners.get(dow).getValue());
            LocalTime close = toLocalTime((Date) closeSpinners.get(dow).getValue());
            boolean closed = closedChecks.get(dow).isSelected();
            if (!closed && !open.isBefore(close)) {
                statusConsumer.accept(lm.t("status.invalidHours") + " (" + lm.dayNames()[indexOf(dow)] + ")");
                return;
            }
            BusinessDay day = config.getDay(dow);
            day.setOpenTime(open);
            day.setCloseTime(close);
            day.setClosed(closed);
        }
        config.setSlotDurationMinutes((Integer) slotDurationSpinner.getValue());
        config.setBufferMinutes((Integer) bufferSpinner.getValue());

        reservationService.save();
        statusConsumer.accept(lm.t("status.dataSaved"));
        onSaved.run();
    }

    private int indexOf(DayOfWeek dow) {
        // Aligns with LanguageManager.dayNames() ordering: Mon..Sun
        return dow.getValue() - 1;
    }

    private Date toDate(LocalTime time) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, time.getHour());
        cal.set(Calendar.MINUTE, time.getMinute());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private LocalTime toLocalTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime().withSecond(0).withNano(0);
    }

    private void applyLabels() {
        LanguageManager lm = LanguageManager.getInstance();
        daysBorder.setTitle(lm.t("tab.hours"));
        globalBorder.setTitle(lm.t("column.hours"));
        openHeader.setText(lm.t("label.open"));
        closeHeader.setText(lm.t("label.close"));
        closedHeader.setText(lm.t("label.closedDay"));
        slotDurationLabel.setText(lm.t("label.slotDuration"));
        bufferLabel.setText(lm.t("label.bufferTime"));
        saveButton.setText(lm.t("button.saveHours"));

        String[] names = lm.dayNames();
        for (DayOfWeek dow : DayOfWeek.values()) {
            dayLabels.get(dow).setText(names[indexOf(dow)]);
        }
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
