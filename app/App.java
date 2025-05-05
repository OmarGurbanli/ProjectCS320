package app;

import gui.MainFrame;
import javax.swing.*;
import gui.LoginDialog;
import gui.MainFrame;
import utils.AppContext;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        // Показываем LoginDialog один раз
        LoginDialog login = new LoginDialog();
        login.setVisible(true);

        // Если логин не прошёл — выходим
        if (AppContext.getCurrentUser() == null) {
            System.exit(0);
        }

        // Иначе — открываем MainFrame
        new MainFrame().setVisible(true);
    }
}


