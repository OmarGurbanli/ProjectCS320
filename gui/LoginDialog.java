package gui;

import dao.UserDAO;
import model.User;
import utils.AppContext;
import utils.PasswordUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginDialog extends JDialog {
    private final JTextField tfLogin = new JTextField(20);
    private final JPasswordField pfPass = new JPasswordField(20);

    public LoginDialog() {
        setTitle("Login");
        setModal(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Логин
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        add(tfLogin, gbc);

        // Пароль
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        add(pfPass, gbc);

        // Кнопки
        JButton btnOk = new JButton("Войти");
        JButton btnCancel = new JButton("Отмена");

        btnOk.addActionListener(e -> onLogin());
        btnCancel.addActionListener(e -> {
            dispose();
            System.exit(0);
        });

        JPanel pnlButtons = new JPanel();
        pnlButtons.add(btnOk);
        pnlButtons.add(btnCancel);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(pnlButtons, gbc);

        pack();
        setLocationRelativeTo(null);
    }

    private void onLogin() {
        String login = tfLogin.getText().trim();
        String pass = new String(pfPass.getPassword());

        if (login.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Login And Password",
                    "Mistype error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            UserDAO dao = new UserDAO();
            User u = dao.findByLogin(login);
            if (u != null && PasswordUtil.verify(pass, u.getPasswordHash())) {
                AppContext.setCurrentUser(u);
                dispose();
                return;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "DB connection error:\n" + ex.getMessage(),
                    "DB Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Login or Password Incorrect",
                "Auth failed",
                JOptionPane.ERROR_MESSAGE);
    }
}
