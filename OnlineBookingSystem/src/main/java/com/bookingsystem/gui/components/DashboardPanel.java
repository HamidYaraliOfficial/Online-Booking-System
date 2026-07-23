package com.bookingsystem.gui.components;

import com.bookingsystem.i18n.Language;
import com.bookingsystem.i18n.LanguageManager;
import com.bookingsystem.service.ReservationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Landing tab summarising the current state of the schedule: how many
 * reservations are booked today and upcoming, how many services and
 * customers are registered.
 */
public class DashboardPanel extends JPanel implements LanguageManager.LanguageChangeListener {

    private final ReservationService reservationService;

    private final JLabel todayValue = new JLabel();
    private final JLabel upcomingValue = new JLabel();
    private final JLabel servicesValue = new JLabel();
    private final JLabel customersValue = new JLabel();

    private final TitledBorder todayBorder;
    private final TitledBorder upcomingBorder;
    private final TitledBorder servicesBorder;
    private final TitledBorder customersBorder;

    public DashboardPanel(ReservationService reservationService) {
        this.reservationService = reservationService;

        setLayout(new GridLayout(2, 2, 16, 16));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        LanguageManager lm = LanguageManager.getInstance();
        todayBorder = BorderFactory.createTitledBorder(lm.t("label.totalToday"));
        upcomingBorder = BorderFactory.createTitledBorder(lm.t("label.totalUpcoming"));
        servicesBorder = BorderFactory.createTitledBorder(lm.t("label.totalServices"));
        customersBorder = BorderFactory.createTitledBorder(lm.t("label.totalCustomers"));

        add(statCard(todayBorder, todayValue));
        add(statCard(upcomingBorder, upcomingValue));
        add(statCard(servicesBorder, servicesValue));
        add(statCard(customersBorder, customersValue));

        refresh();
        lm.addListener(this);
    }

    private JPanel statCard(TitledBorder border, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(border);
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.BOLD, 42f));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(valueLabel, BorderLayout.CENTER);
        return panel;
    }

    public void refresh() {
        todayValue.setText(String.valueOf(reservationService.countTodayReservations()));
        upcomingValue.setText(String.valueOf(reservationService.countUpcomingReservations()));
        servicesValue.setText(String.valueOf(reservationService.getActiveServices().size()));
        customersValue.setText(String.valueOf(reservationService.getCustomers().size()));
    }

    private void applyLabels() {
        LanguageManager lm = LanguageManager.getInstance();
        todayBorder.setTitle(lm.t("label.totalToday"));
        upcomingBorder.setTitle(lm.t("label.totalUpcoming"));
        servicesBorder.setTitle(lm.t("label.totalServices"));
        customersBorder.setTitle(lm.t("label.totalCustomers"));
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
