package com.bookingsystem.gui.components;

import com.bookingsystem.i18n.Language;
import com.bookingsystem.i18n.LanguageManager;
import com.bookingsystem.model.Customer;
import com.bookingsystem.service.ReservationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Tab for maintaining the customer directory used when creating new
 * reservations.
 */
public class CustomersPanel extends JPanel implements LanguageManager.LanguageChangeListener {

    private final ReservationService reservationService;
    private final Consumer<String> statusConsumer;
    private final Runnable onChanged;

    private final CustomersTableModel tableModel = new CustomersTableModel();
    private final JTable table = new JTable(tableModel);

    private final JTextField nameField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField emailField = new JTextField();

    private final JLabel nameLabel = new JLabel();
    private final JLabel phoneLabel = new JLabel();
    private final JLabel emailLabel = new JLabel();
    private final JButton addButton = new JButton();
    private final JButton deleteButton = new JButton();

    public CustomersPanel(ReservationService reservationService, Consumer<String> statusConsumer, Runnable onChanged) {
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
        form.add(phoneLabel, gbc);
        gbc.gridx = 3;
        form.add(phoneField, gbc);

        gbc.gridx = 4; gbc.gridy = 0;
        form.add(emailLabel, gbc);
        gbc.gridx = 5;
        form.add(emailField, gbc);

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
        tableModel.setData(reservationService.getCustomers());
    }

    private void onAdd() {
        LanguageManager lm = LanguageManager.getInstance();
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            statusConsumer.accept(lm.t("status.fillRequiredFields"));
            return;
        }
        reservationService.addCustomer(new Customer(name, phoneField.getText().trim(), emailField.getText().trim()));
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        refresh();
        onChanged.run();
    }

    private void onDelete() {
        LanguageManager lm = LanguageManager.getInstance();
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        Customer customer = tableModel.getAt(table.convertRowIndexToModel(row));
        int confirm = JOptionPane.showConfirmDialog(this, lm.t("dialog.confirmDelete"),
                lm.t("dialog.confirmTitle"), JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            reservationService.removeCustomer(customer);
            refresh();
            onChanged.run();
        }
    }

    private void applyLabels() {
        LanguageManager lm = LanguageManager.getInstance();
        nameLabel.setText(lm.t("label.name"));
        phoneLabel.setText(lm.t("label.phone"));
        emailLabel.setText(lm.t("label.email"));
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

    private static class CustomersTableModel extends AbstractTableModel {
        private List<Customer> data = new ArrayList<>();
        private String[] columns;

        CustomersTableModel() {
            refreshColumns();
        }

        void refreshColumns() {
            LanguageManager lm = LanguageManager.getInstance();
            columns = new String[]{lm.t("column.name"), lm.t("column.phone"), lm.t("column.email")};
            fireTableStructureChanged();
        }

        void setData(List<Customer> data) {
            this.data = data;
            fireTableDataChanged();
        }

        Customer getAt(int row) {
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
            Customer c = data.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return c.getName();
                case 1:
                    return c.getPhone();
                case 2:
                    return c.getEmail();
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
