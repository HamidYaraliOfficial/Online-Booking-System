package com.bookingsystem.gui.components;

import com.bookingsystem.i18n.Language;
import com.bookingsystem.i18n.LanguageManager;
import com.bookingsystem.model.Reservation;
import com.bookingsystem.model.ReservationStatus;
import com.bookingsystem.service.ReservationService;
import com.bookingsystem.util.CsvExporter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

/**
 * Tab listing every reservation, with an optional date filter, and
 * actions to cancel or mark a reservation as completed.
 */
public class ReservationsPanel extends JPanel implements LanguageManager.LanguageChangeListener {

    private final ReservationService reservationService;
    private final Consumer<String> statusConsumer;

    private final ReservationsTableModel tableModel = new ReservationsTableModel();
    private final JTable table = new JTable(tableModel);
    private final JCheckBox filterEnabled = new JCheckBox();
    private final JSpinner filterDateSpinner;
    private final JButton refreshButton = new JButton();
    private final JButton cancelButton = new JButton();
    private final JButton completeButton = new JButton();
    private final JButton exportButton = new JButton();

    public ReservationsPanel(ReservationService reservationService, Consumer<String> statusConsumer) {
        this.reservationService = reservationService;
        this.statusConsumer = statusConsumer;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(14, 14, 14, 14));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEADING, 10, 4));
        SpinnerDateModel dateModel = new SpinnerDateModel();
        filterDateSpinner = new JSpinner(dateModel);
        filterDateSpinner.setEditor(new JSpinner.DateEditor(filterDateSpinner, "yyyy-MM-dd"));
        filterEnabled.addActionListener(e -> refresh());
        filterDateSpinner.addChangeListener(e -> refresh());
        topBar.add(filterEnabled);
        topBar.add(filterDateSpinner);
        refreshButton.addActionListener(e -> refresh());
        topBar.add(refreshButton);
        add(topBar, BorderLayout.NORTH);

        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 4));
        cancelButton.addActionListener(e -> onCancel());
        completeButton.addActionListener(e -> onComplete());
        exportButton.addActionListener(e -> onExport());
        bottomBar.add(cancelButton);
        bottomBar.add(completeButton);
        bottomBar.add(exportButton);
        add(bottomBar, BorderLayout.SOUTH);

        applyLabels();
        refresh();
        LanguageManager.getInstance().addListener(this);
    }

    public void refresh() {
        List<Reservation> all = reservationService.getReservations();
        if (filterEnabled.isSelected()) {
            LocalDate date = ((Date) filterDateSpinner.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            List<Reservation> filtered = new ArrayList<>();
            for (Reservation r : all) {
                if (r.getDate().equals(date)) {
                    filtered.add(r);
                }
            }
            tableModel.setData(filtered);
        } else {
            tableModel.setData(all);
        }
    }

    private Reservation getSelected() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return null;
        }
        return tableModel.getAt(table.convertRowIndexToModel(row));
    }

    private void onCancel() {
        LanguageManager lm = LanguageManager.getInstance();
        Reservation r = getSelected();
        if (r == null) {
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, lm.t("dialog.confirmCancel"),
                lm.t("dialog.confirmTitle"), JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            reservationService.cancelReservation(r);
            refresh();
            statusConsumer.accept(lm.t("status.dataSaved"));
        }
    }

    private void onComplete() {
        Reservation r = getSelected();
        if (r == null) {
            return;
        }
        reservationService.completeReservation(r);
        refresh();
    }

    private void onExport() {
        LanguageManager lm = LanguageManager.getInstance();
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new java.io.File("reservations.csv"));
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                new CsvExporter().export(reservationService.getReservations(), chooser.getSelectedFile().getAbsolutePath());
                statusConsumer.accept(lm.t("status.exported"));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        }
    }

    private void applyLabels() {
        LanguageManager lm = LanguageManager.getInstance();
        filterEnabled.setText(lm.t("label.filterByDate"));
        refreshButton.setText(lm.t("button.refresh"));
        cancelButton.setText(lm.t("button.cancel"));
        completeButton.setText(lm.t("button.complete"));
        exportButton.setText(lm.t("menu.file.exportCsv"));
        tableModel.refreshColumns();
    }

    @Override
    public void onLanguageChanged(Language newLanguage) {
        applyLabels();
        applyComponentOrientation(newLanguage.isRtl()
                ? ComponentOrientation.RIGHT_TO_LEFT
                : ComponentOrientation.LEFT_TO_RIGHT);
        SwingUtilities.updateComponentTreeUI(this);
    }

    /** Table model translating {@link Reservation} objects into rows. */
    private static class ReservationsTableModel extends AbstractTableModel {
        private List<Reservation> data = new ArrayList<>();
        private String[] columns;

        ReservationsTableModel() {
            refreshColumns();
        }

        void refreshColumns() {
            LanguageManager lm = LanguageManager.getInstance();
            columns = new String[]{
                    lm.t("column.date"), lm.t("column.time"), lm.t("column.customer"),
                    lm.t("column.service"), lm.t("column.status")
            };
            fireTableStructureChanged();
        }

        void setData(List<Reservation> data) {
            this.data = data;
            fireTableDataChanged();
        }

        Reservation getAt(int row) {
            return data.get(row);
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Reservation r = data.get(rowIndex);
            LanguageManager lm = LanguageManager.getInstance();
            switch (columnIndex) {
                case 0:
                    return r.getDate().toString();
                case 1:
                    return r.getStartTime() + " - " + r.getEndTime();
                case 2:
                    return r.getCustomerNameSnapshot();
                case 3:
                    return r.getServiceNameSnapshot();
                case 4:
                    return lm.t(r.getStatus().getLabelKey());
                default:
                    return "";
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }
    }
}
