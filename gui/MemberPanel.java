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

    public MemberPanel() {
        super(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();

        // 1) Глобальный статус зала (не привязан к пользователю)
        tabs.addTab("Status", buildStatusPanel());

        // 2) Классы — всем ролям
        tabs.addTab("Classes", buildClassesPanel());

        // 3) Инструкторы — всем ролям
        tabs.addTab("Instructors", buildInstructorsPanel());

        // 4) Оплата — только MEMBER
        if ("MEMBER".equals(role)) {
            tabs.addTab("Payment", buildPaymentPanel());
        }

        add(tabs, BorderLayout.CENTER);
    }

    /** 1) Глобальный статус зала */
    private JPanel buildStatusPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        try {
            GymStatus gs = new GymStatusDAO().getStatus();
            String text = gs.isActive() ? "Gym is Open" : "Gym is Closed";
            JLabel lbl = new JLabel(text);
            lbl.setFont(lbl.getFont().deriveFont(16f));
            p.add(lbl);
        } catch (SQLException ex) {
            p.add(new JLabel("Status Error: " + ex.getMessage()));
        }
        return p;
    }

    /** 2) Классы + кнопка записаться */
    private JPanel buildClassesPanel() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Time", "Instructor", "Capacity"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        refreshClasses(model);

        JButton btnEnroll = new JButton("Register");
        btnEnroll.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "get a class");
                return;
            }
            int classId = (int) model.getValueAt(row, 0);
            try {
                new EnrollmentDAO().create(memberId, classId);
                JOptionPane.showMessageDialog(this, "successfully enrolled");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                        "Ошибка: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        p.add(btnEnroll, BorderLayout.SOUTH);
        return p;
    }

    private void refreshClasses(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            List<ClassSession> classes = new ClassDAO().findAll();
            for (ClassSession cs : classes) {
                model.addRow(new Object[]{
                        cs.getId(), cs.getTime(), cs.getInstructorName(), cs.getCapacity()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading classes: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** 3) Список инструкторов */
    private JPanel buildInstructorsPanel() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Name"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
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

    /** 4) Оплата абонемента */
    private JPanel buildPaymentPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        p.add(new JLabel("Sum:"), gbc);
        JTextField tfAmount = new JTextField(10);
        gbc.gridx = 1;
        p.add(tfAmount, gbc);

        JButton btnPay = new JButton("Pay");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
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
