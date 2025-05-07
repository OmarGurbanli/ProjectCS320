package app;

import gui.MainFrame;
import javax.swing.*;
import gui.LoginDialog;
import gui.MainFrame;
import utils.AppContext;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        // LoginDialog seen just once
        LoginDialog login = new LoginDialog();
        login.setVisible(true);

        // if login unsuccessful ->exit
        if (AppContext.getCurrentUser() == null) {
            System.exit(0);
        }

        // otherwise open MainFrame
        new MainFrame().setVisible(true);
    }
}


