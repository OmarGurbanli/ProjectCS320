package gui;

import dao.UserDAO;
import dao.PaymentDAO;
import model.User;
import model.Payment;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserManagementPanel extends JPanel {

    // search and control panel for users
    private final UserTableModel userModel;
    private final JTable userTable;
    private final TableRowSorter<UserTableModel> userSorter;
    private final JTextField tfUserSearch;

    // search and control panel for payments
    private final PaymentTableModel paymentModel;
    private final JTable paymentTable;
    private final TableRowSorter<PaymentTableModel> paymentSorter;
    private final JTextField tfPaymentSearch;

    public UserManagementPanel() {
        super(new BorderLayout());

        // ======== Users Section ========
        JPanel userTop = new JPanel(new BorderLayout(5,5));
        tfUserSearch = new JTextField();
        userTop.add(new JLabel("Search Users:"), BorderLayout.WEST);
        userTop.add(tfUserSearch, BorderLayout.CENTER);
        add(userTop, BorderLayout.NORTH);

        userModel = new UserTableModel();
        userTable = new JTable(userModel);
        userSorter = new TableRowSorter<>(userModel);
        userTable.setRowSorter(userSorter);
        add(new JScrollPane(userTable), BorderLayout.CENTER);

        tfUserSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterUsers(); }
            @Override public void removeUpdate(DocumentEvent e) { filterUsers(); }
            @Override public void changedUpdate(DocumentEvent e) { filterUsers(); }
            private void filterUsers() {
                String text = tfUserSearch.getText();
                if (text.trim().isEmpty()) {
                    userSorter.setRowFilter(null);
                } else {
                    userSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        // load users
        try {
            List<User> allUsers = new UserDAO().findAll();
            userModel.setUsers(new ArrayList<>(allUsers));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "User load error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        // ======== Payments Section ========
        JPanel paymentTop = new JPanel(new BorderLayout(5,5));
        tfPaymentSearch = new JTextField();
        paymentTop.add(new JLabel("Search Payments:"), BorderLayout.WEST);
        paymentTop.add(tfPaymentSearch, BorderLayout.CENTER);
        add(paymentTop, BorderLayout.SOUTH);

        paymentModel = new PaymentTableModel();
        paymentTable = new JTable(paymentModel);
        paymentSorter = new TableRowSorter<>(paymentModel);
        paymentTable.setRowSorter(paymentSorter);
        add(new JScrollPane(paymentTable), BorderLayout.SOUTH);

        tfPaymentSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterPayments(); }
            @Override public void removeUpdate(DocumentEvent e) { filterPayments(); }
            @Override public void changedUpdate(DocumentEvent e) { filterPayments(); }
            private void filterPayments() {
                String text = tfPaymentSearch.getText();
                if (text.trim().isEmpty()) {
                    paymentSorter.setRowFilter(null);
                } else {
                    paymentSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        // load payments
        try {
            List<Payment> allPayments = new PaymentDAO().findAll();
            paymentModel.setPayments(new ArrayList<>(allPayments));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Payment load error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // nested model for users
    private static class UserTableModel extends AbstractTableModel {
        private final String[] cols = {"ID", "Username", "Role"};
        private List<User> list = new ArrayList<>();

        public void setUsers(List<User> users) {
            this.list = users;
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return list.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }
        @Override public Class<?> getColumnClass(int col) {
            return col == 0 ? Integer.class : String.class;
        }
        @Override public boolean isCellEditable(int r, int c) { return false; }
        @Override public Object getValueAt(int row, int col) {
            User u = list.get(row);
            switch (col) {
                case 0: return u.getId();
                case 1: return u.getUsername();
                case 2: return u.getRole();
                default: return null;
            }
        }
    }

    // nested model for payments
    private static class PaymentTableModel extends AbstractTableModel {
        private final String[] cols = {"ID", "Member", "Amount", "Paid At"};
        private List<Payment> list = new ArrayList<>();

        public void setPayments(List<Payment> payments) {
            this.list = payments;
            fireTableDataChanged();
        }

        @Override public int getRowCount() { return list.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }
        @Override public Class<?> getColumnClass(int col) {
            return col == 0 ? Integer.class
                    : col == 2 ? BigDecimal.class
                    : String.class;
        }
        @Override public boolean isCellEditable(int r, int c) { return false; }
        @Override public Object getValueAt(int row, int col) {
            Payment p = list.get(row);
            switch (col) {
                case 0: return p.getId();
                case 1: return p.getUsername();
                case 2: return p.getAmount();
                case 3: return p.getPaidAt();
                default: return null;
            }
        }
    }
}
