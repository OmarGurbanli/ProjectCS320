package gui;

import app.App;
import utils.AppContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainFrame extends JFrame {

    private boolean darkMode = false;

    public MainFrame() {
        super("Gym Management");
        initMenuBar();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        String role = AppContext.getCurrentUser().getRole();
        JTabbedPane tabs = new JTabbedPane();

        switch (role) {
            case "ADMIN":
                tabs.addTab("Classes", new ClassPanel());
                tabs.addTab("Instructors", new InstructorPanel());
                tabs.addTab("Users", new UserManagementPanel());
                tabs.addTab("Payments", new PaymentPanel());
                tabs.addTab("Gym Status", new GymStatusPanel());
                break;

            case "TRAINER":
                tabs.addTab("Classes", new ClassPanel());
                tabs.addTab("Instructors", new InstructorPanel());
                break;

            case "MEMBER":
                tabs.addTab("Profile", new MemberPanel());
                tabs.addTab("Classes", new ClassPanel());
                tabs.addTab("Instructors", new InstructorPanel());
                break;

            default:
                tabs.addTab("Classes", new ClassPanel());
                break;
        }

        add(tabs, BorderLayout.CENTER);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("File");
        JMenuItem miSwitch = new JMenuItem(new AbstractAction("Switch User") {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                AppContext.setCurrentUser(null);

                LoginDialog login = new LoginDialog();
                login.setVisible(true);

                if (AppContext.getCurrentUser() != null) {
                    new MainFrame().setVisible(true);
                } else {
                    System.exit(0);
                }
            }
        });
        JMenuItem miExit = new JMenuItem(new AbstractAction("Exit") {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        file.add(miSwitch);
        file.add(miExit);

        JMenu settings = new JMenu("Settings");
        JMenuItem miTheme = new JMenuItem(new AbstractAction("Toggle Dark Mode") {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleTheme();
            }
        });
        settings.add(miTheme);

        menuBar.add(file);
        menuBar.add(settings);
        setJMenuBar(menuBar);
    }

    private void toggleTheme() {
        try {
            if (!darkMode) {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } else {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            }
            SwingUtilities.updateComponentTreeUI(this);
            darkMode = !darkMode;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
