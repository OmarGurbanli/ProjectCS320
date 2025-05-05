package gui;

import dao.GymStatusDAO;
import model.GymStatus;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class GymStatusPanel extends JPanel {
    private final JCheckBox cbOpen = new JCheckBox("Gym Open");

    public GymStatusPanel() {
        super(new BorderLayout(10,10));
        add(cbOpen, BorderLayout.CENTER);

        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> onSave());
        add(btnSave, BorderLayout.SOUTH);

        loadStatus();
    }

    private void loadStatus() {
        try {
            GymStatus gs = new GymStatusDAO().getStatus();
            cbOpen.setSelected(gs.isActive());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error in status download: " + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onSave() {
        try {
            new GymStatusDAO().setStatus(cbOpen.isSelected());
            JOptionPane.showMessageDialog(this,
                    "Saved status: " + cbOpen.isSelected(),
                    "OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error in save: " + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
