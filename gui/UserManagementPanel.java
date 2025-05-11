package gui;

import dao.UserDAO;
import model.User;
import utils.AppContext;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserManagementPanel extends JPanel {

    private final UserTableModel userModel;
    private final JTable userTable;
    private final TableRowSorter<UserTableModel> userSorter;
    private final JTextField tfUserSearch;
    private final JButton btnAddUser;
    private final JButton btnDeleteUser;

    public UserManagementPanel() {
        super(new BorderLayout(5,5));

        // Top: search + Add/Delete buttons
        JPanel top = new JPanel(new BorderLayout(5,5));
        tfUserSearch = new JTextField();
        top.add(new JLabel("Search Users:"), BorderLayout.WEST);
        top.add(tfUserSearch, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAddUser    = new JButton("Add User");
        btnDeleteUser = new JButton("Delete User");
        btnPanel.add(btnAddUser);
        btnPanel.add(btnDeleteUser);
        top.add(btnPanel, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);

        // Table
        userModel  = new UserTableModel();
        userTable  = new JTable(userModel);
        userSorter = new TableRowSorter<>(userModel);
        userTable.setRowSorter(userSorter);
        add(new JScrollPane(userTable), BorderLayout.CENTER);

        // Search filter
        tfUserSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String txt = tfUserSearch.getText().trim();
                userSorter.setRowFilter(txt.isEmpty()
                        ? null
                        : RowFilter.regexFilter("(?i)" + txt));
            }
        });

        // Load initial data
        loadUsers();

        // Add User action
        btnAddUser.addActionListener(e -> {
            JPanel input = new JPanel(new GridLayout(0,1,5,5));
            JTextField tfUsername = new JTextField();
            JPasswordField pfPassword = new JPasswordField();
            String[] roles = {"MEMBER","TRAINER","ADMIN"};
            JComboBox<String> cbRole = new JComboBox<>(roles);
            input.add(new JLabel("Username:"));   input.add(tfUsername);
            input.add(new JLabel("Password:"));   input.add(pfPassword);
            input.add(new JLabel("Role:"));       input.add(cbRole);

            int result = JOptionPane.showConfirmDialog(
                    this, input, "Add New User", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;

            String username = tfUsername.getText().trim();
            String password = new String(pfPassword.getPassword());
            String role = (String)cbRole.getSelectedItem();
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Username and password cannot be empty",
                        "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                String hash = utils.PasswordUtil.hash(password);
                new UserDAO().create(username, hash, role);
                JOptionPane.showMessageDialog(this,
                        "User added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error adding user:\n" + ex.getMessage(),
                        "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Delete User action
        btnDeleteUser.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this,
                        "Select a user to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = userTable.convertRowIndexToModel(row);
            User u = userModel.getUserAt(modelRow);

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Delete user “" + u.getUsername() + "” (role: " + u.getRole() + ")?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            try {
                new UserDAO().delete(u.getId(), u.getRole());
                JOptionPane.showMessageDialog(this,
                        "User deleted", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting user:\n" + ex.getMessage(),
                        "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void loadUsers() {
        try {
            List<User> list = new UserDAO().findAll();
            userModel.setUsers(new ArrayList<>(list));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading users:\n" + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Nested Table Model ---
    private static class UserTableModel extends AbstractTableModel {
        private final String[] cols = {"ID","Username","Role"};
        private List<User> users = List.of();

        public void setUsers(List<User> users) {
            this.users = users;
            fireTableDataChanged();
        }

        public User getUserAt(int row) {
            return users.get(row);
        }

        @Override public int getRowCount() { return users.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }
        @Override public Class<?> getColumnClass(int col) {
            return col==0? Integer.class : String.class;
        }
        @Override public boolean isCellEditable(int r,int c){ return false; }

        @Override
        public Object getValueAt(int row, int col) {
            User u = users.get(row);
            return switch(col) {
                case 0 -> u.getId();
                case 1 -> u.getUsername();
                case 2 -> u.getRole();
                default -> null;
            };
        }
    }
}
