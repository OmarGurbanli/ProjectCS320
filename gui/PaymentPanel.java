package gui;

import dao.PaymentDAO;
import model.Payment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PaymentPanel extends JPanel {
    private final DefaultTableModel model;
    private final JTable table;

    public PaymentPanel() {
        super(new BorderLayout());
        model = new DefaultTableModel(
                new Object[]{"ID","Member","Amount","Paid At"}, 0
        ) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        refresh();

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void refresh() {
        model.setRowCount(0);
        try {
            List<Payment> pays = new PaymentDAO().findAll();
            for (Payment p : pays) {
                model.addRow(new Object[]{
                        p.getId(), p.getUsername(),
                        p.getAmount(), p.getPaidAt()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "DB error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
