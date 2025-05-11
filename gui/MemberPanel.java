package gui;

import dao.ClassDAO;
import dao.EnrollmentDAO;
import dao.GymStatusDAO;
import dao.InstructorDAO;
import dao.PaymentDAO;
import model.ClassSession;
import model.GymStatus;
import model.Instructor;
import utils.AppContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class MemberPanel extends JPanel {

    private final int memberId = AppContext.getCurrentUser().getId();
    private final String role = AppContext.getCurrentUser().getRole();

    private final DefaultTableModel availableModel = new DefaultTableModel(
            new Object[]{"ID", "Time", "Instructor", "Capacity"}, 0
    ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

    private final DefaultTableModel myModel = new DefaultTableModel(
            new Object[]{"ID", "Time", "Instructor", "Capacity"}, 0
    ) { @Override public boolean isCellEditable(int r, int c) { return false; } };

    public MemberPanel() {
        super(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Status", buildStatusPanel());
        tabs.addTab("All Classes", buildAllClassesPanel());
        tabs.addTab("Available Classes", buildAvailablePanel());
        tabs.addTab("My Classes", buildMyClassesPanel());
        tabs.addTab("Instructors", buildInstructorsPanel());
        if ("MEMBER".equals(role)) {
            tabs.addTab("Payment", buildPaymentPanel());
        }
        add(tabs, BorderLayout.CENTER);

        refreshAvailable();
        refreshMyClasses();
    }

    private JPanel buildStatusPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        try {
            GymStatus gs = new GymStatusDAO().getStatus();
            JLabel lbl = new JLabel(gs.isActive() ? "Gym is Open" : "Gym is Closed");
            lbl.setFont(lbl.getFont().deriveFont(16f));
            p.add(lbl);
        } catch (SQLException ex) {
            p.add(new JLabel("Status Error: " + ex.getMessage()));
        }
        return p;
    }

    private JPanel buildAllClassesPanel() {
        DefaultTableModel allModel = new DefaultTableModel(
                new Object[]{"ID", "Time", "Instructor", "Capacity"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = new JTable(allModel);
        try {
            List<ClassSession> all = new ClassDAO().findAll();
            for (ClassSession cs : all) {
                allModel.addRow(new Object[]{
                        cs.getId(), cs.getTime(), cs.getInstructorName(), cs.getCapacity()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading classes: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildAvailablePanel() {
        JTable table = new JTable(availableModel);
        JButton btnRegister = new JButton("Register");
        btnRegister.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a class to register.");
                return;
            }
            int classId = (int) availableModel.getValueAt(row, 0);
            try {
                new EnrollmentDAO().create(memberId, classId);
                JOptionPane.showMessageDialog(this, "Registered successfully.");
                refreshAvailable();
                refreshMyClasses();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(),
                        "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(btnRegister, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildMyClassesPanel() {
        JTable table = new JTable(myModel);
        JButton btnUnregister = new JButton("Unregister");
        btnUnregister.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Select a class to unregister.");
                return;
            }
            int classId = (int) myModel.getValueAt(row, 0);
            try {
                new EnrollmentDAO().delete(memberId, classId);
                JOptionPane.showMessageDialog(this, "Unregistered successfully.");
                refreshAvailable();
                refreshMyClasses();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(),
                        "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(btnUnregister, BorderLayout.SOUTH);
        return p;
    }

    private void refreshAvailable() {
        availableModel.setRowCount(0);
        try {
            List<ClassSession> all = new ClassDAO().findAll();
            List<Integer> enrolled = new EnrollmentDAO().findClassIdsByMember(memberId);
            for (ClassSession cs : all) {
                if (!enrolled.contains(cs.getId())) {
                    availableModel.addRow(new Object[]{
                            cs.getId(), cs.getTime(), cs.getInstructorName(), cs.getCapacity()
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading available classes: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshMyClasses() {
        myModel.setRowCount(0);
        try {
            List<ClassSession> all = new ClassDAO().findAll();
            List<Integer> enrolled = new EnrollmentDAO().findClassIdsByMember(memberId);
            for (ClassSession cs : all) {
                if (enrolled.contains(cs.getId())) {
                    myModel.addRow(new Object[]{
                            cs.getId(), cs.getTime(), cs.getInstructorName(), cs.getCapacity()
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading my classes: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel buildInstructorsPanel() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Name"}, 0
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        JTable table = new JTable(model);
        try {
            List<Instructor> list = new InstructorDAO().findAll();
            for (Instructor ins : list) {
                model.addRow(new Object[]{ins.getId(), ins.getName()});
            }
        } catch (SQLException ex) {
            model.addRow(new Object[]{-1, "Error: " + ex.getMessage()});
        }
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildPaymentPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Sum label
        gbc.gridx = 0; gbc.gridy = 0;
        p.add(new JLabel("Sum:"), gbc);

        // Text field
        gbc.gridx = 1; gbc.gridy = 0;
        JTextField tfAmount = new JTextField(10);
        p.add(tfAmount, gbc);

        // Pay button
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        JButton btnPay = new JButton("Pay");
        p.add(btnPay, gbc);

        btnPay.addActionListener(e -> {
            try {
                BigDecimal amount = new BigDecimal(tfAmount.getText().trim());
                new PaymentDAO().create(memberId, amount);
                JOptionPane.showMessageDialog(this, "Payment successful");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Incorrect amount", "Error", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "DB error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return p;
    }
}
