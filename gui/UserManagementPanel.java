package gui;

import dao.UserDAO;
import model.User;
import utils.PasswordUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private final UserTableModel model;
    private final JTable table;
    private final TableRowSorter<UserTableModel> sorter;
    private final PaymentTableModel model;
    private final JTable table;
    private final TableRowSorter<PaymentTableModel> sorter;
    private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public UserManagementPanel() {
        super(new BorderLayout());
        //search panel
        JPanel top = new JPanel(new BorderLayout(5, 5));
        JTextField tfSearch = new JTextField();
        top.add(new JLabel("Search:"), BorderLayout.WEST);
        top.add(tfSearch, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // Настраиваем модель таблицы
        model = new DefaultTableModel(new Object[]{"ID", "Username", "Role", "PasswordHash"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;  // правка «на месте» не нужна || No need to change
            }
        };
        table = new JTable(model);
        refreshTable();

        // Кнопки управления // Control Buttons
        JButton btnAdd = new JButton("Add User");
        JButton btnDel = new JButton("Delete Selected");
        JPanel btns = new JPanel();
        btns.add(btnAdd);
        btns.add(btnDel);

        btnAdd.addActionListener(e -> onAdd());
        btnDel.addActionListener(e -> onDelete());

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btns, BorderLayout.SOUTH);
    }

    private void refreshTable() {
        try {
            model.setRowCount(0);
            List<User> users = new UserDAO().findAll();
            for (User u : users) {
                model.addRow(new Object[]{
                        u.getId(), u.getUsername(), u.getRole(), u.getPasswordHash()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAdd() {
        JTextField tfLogin = new JTextField();
        JPasswordField pf = new JPasswordField();
        JComboBox<String> cbRole = new JComboBox<>(new String[]{"MEMBER", "TRAINER", "ADMIN"});
        Object[] fld = {
                "Username:", tfLogin,
                "Password:", pf,
                "Role:", cbRole
        };
        int ok = JOptionPane.showConfirmDialog(this, fld, "Add User", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        String login = tfLogin.getText().trim();
        String pass = new String(pf.getPassword());
        String role = (String) cbRole.getSelectedItem();
        if (login.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Login and password required.");
            return;
        }
        try {
            String hash = PasswordUtil.hash(pass);
            new UserDAO().create(login, hash, role);
            refreshTable();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row first.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        String role = (String) model.getValueAt(row, 2);
        int ask = JOptionPane.showConfirmDialog(this,
                "Delete user ID=" + id + " (" + role + ")?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (ask != JOptionPane.YES_OPTION) return;
        try {
            new UserDAO().delete(id, role);
            refreshTable();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class UserTableModel extends AbstractTableModel {
        private final String[] cols = {"ID", "Username", "Role", "PasswordHash"};
        private List<User> list = List.of();

        public void setUsers(List<User> users) {
            this.list = users;
            fireTableDataChanged();
        }

        public User getUserAt(int row) {
            return list.get(row);
        }

        @Override
        public int getRowCount() {
            return list.size();
        }

        @Override
        public int getColumnCount() {
            return cols.length;
        }

        @Override
        public String getColumnName(int col) {
            return cols[col];
        }

        @Override
        public Class<?> getColumnClass(int col) {
            return switch (col) {
                case 0 -> Integer.class;
                case 1, 2, 3 -> String.class;
                default -> Object.class;
            };
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }

        @Override
        public Object getValueAt(int row, int col) {
            User u = list.get(row);
            return switch (col) {
                case 0 -> u.getId();
                case 1 -> u.getUsername();
                case 2 -> u.getRole();
                case 3 -> u.getPasswordHash();
                default -> null;
            };
        }
    }
}

