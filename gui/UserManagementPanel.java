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

    private final DefaultTableModel model;
    private final JTable table;

    public UserManagementPanel() {
        super(new BorderLayout());

        // Настраиваем модель таблицы
        model = new DefaultTableModel(new Object[]{"ID","Username","Role","PasswordHash"}, 0) {
            @Override public boolean isCellEditable(int row, int col) {
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
            JOptionPane.showMessageDialog(this, "DB error: "+ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onAdd() {
        JTextField tfLogin = new JTextField();
        JPasswordField pf = new JPasswordField();
        JComboBox<String> cbRole = new JComboBox<>(new String[]{"MEMBER","TRAINER","ADMIN"});
        Object[] fld = {
                "Username:", tfLogin,
                "Password:", pf,
                "Role:", cbRole
        };
        int ok = JOptionPane.showConfirmDialog(this, fld, "Add User", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        String login = tfLogin.getText().trim();
        String pass = new String(pf.getPassword());
        String role = (String)cbRole.getSelectedItem();
        if (login.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Login and password required.");
            return;
        }
        try {
            String hash = PasswordUtil.hash(pass);
            new UserDAO().create(login, hash, role);
            refreshTable();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: "+ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row first.");
            return;
        }
        int id = (int)model.getValueAt(row, 0);
        String role = (String)model.getValueAt(row, 2);
        int ask = JOptionPane.showConfirmDialog(this,
                "Delete user ID="+id+" ("+role+")?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (ask != JOptionPane.YES_OPTION) return;
        try {
            new UserDAO().delete(id, role);
            refreshTable();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB error: "+ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
