package com.bookingsystem.gui.components;

import com.bookingsystem.i18n.Language;
import com.bookingsystem.i18n.LanguageManager;
import com.bookingsystem.model.Service;
import com.bookingsystem.service.ReservationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Tab for creating, editing and removing bookable services, each with a
 * name, duration in minutes and price. The duration feeds directly into
 * the automatic slot generation on the New Reservation tab.
 */
public class ServicesPanel extends JPanel implements LanguageManager.LanguageChangeListener {

    private final ReservationService reservationService;
    private final Consumer<String> statusConsumer;
    private final Runnable onChanged;

    private final ServicesTableModel tableModel = new ServicesTableModel();
    private final JTable table = new JTable(tableModel);

    private final JTextField nameField = new JTextField();
    private final JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(30, 5, 480, 5));
    private final JSpinner priceSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1000000.0, 1.0));

    private final JLabel nameLabel = new JLabel();
    private final JLabel durationLabel = new JLabel();
    private final JLabel priceLabel = new JLabel();
    private final JButton addButton = new JButton();
    private final JButton deleteButton = new JButton();

    public ServicesPanel(ReservationService reservationService, Consumer<String> statusConsumer, Runnable onChanged) {
        this.reservationService = reservationService;
        this.statusConsumer = statusConsumer;
        this.onChanged = onChanged;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(14, 14, 14, 14));

        table.setRowHeight(26);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        form.add(nameLabel, gbc);
        gbc.gridx = 1;
        form.add(nameField, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        form.add(durationLabel, gbc);
        gbc.gridx = 3;
        form.add(durationSpinner, gbc);

        gbc.gridx = 4; gbc.gridy = 0;
        form.add(priceLabel, gbc);
        gbc.gridx = 5;
        form.add(priceSpinner, gbc);

        gbc.gridx = 6; gbc.gridy = 0;
        addButton.addActionListener(e -> onAdd());
        form.add(addButton, gbc);

        gbc.gridx = 7; gbc.gridy = 0;
        deleteButton.addActionListener(e -> onDelete());
        form.add(deleteButton, gbc);

        add(form, BorderLayout.SOUTH);

        applyLabels();
        refresh();
        LanguageManager.getInstance().addListener(this);
    }

    public void refresh() {
        tableModel.setData(reservationService.getServices());
    }

    private void onAdd() {
        LanguageManager lm = LanguageManager.getInstance();
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            statusConsumer.accept(lm.t("status.fillRequiredFields"));
            return;
        }
        int duration = (Integer) durationSpinner.getValue();
        double price = (Double) priceSpinner.getValue();
        reservationService.addService(new Service(name, duration, price));
        nameField.setText("");
        refresh();
        onChanged.run();
    }

    private void onDelete() {
        LanguageManager lm = LanguageManager.getInstance();
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        Service service = tableModel.getAt(table.convertRowIndexToModel(row));
        int confirm = JOptionPane.showConfirmDialog(this, lm.t("dialog.confirmDelete"),
                lm.t("dialog.confirmTitle"), JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            reservationService.removeService(service);
            refresh();
            onChanged.run();
        }
    }

    private void applyLabels() {
        LanguageManager lm = LanguageManager.getInstance();
        nameLabel.setText(lm.t("label.name"));
        durationLabel.setText(lm.t("label.duration"));
        priceLabel.setText(lm.t("label.price"));
        addButton.setText(lm.t("button.add"));
        deleteButton.setText(lm.t("button.delete"));
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

    private static class ServicesTableModel extends AbstractTableModel {
        private List<Service> data = new ArrayList<>();
        private String[] columns;

        ServicesTableModel() {
            refreshColumns();
        }

        void refreshColumns() {
            LanguageManager lm = LanguageManager.getInstance();
            columns = new String[]{lm.t("column.name"), lm.t("label.duration"), lm.t("column.price")};
            fireTableStructureChanged();
        }

        void setData(List<Service> data) {
            this.data = data;
            fireTableDataChanged();
        }

        Service getAt(int row) {
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
            Service s = data.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return s.getName();
                case 1:
                    return s.getDurationMinutes();
                case 2:
                    return String.format("%.2f", s.getPrice());
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
