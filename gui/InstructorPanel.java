package gui;

import dao.InstructorDAO;
import model.Instructor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class InstructorPanel extends JPanel {
    public InstructorPanel() {
        super(new BorderLayout());
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID","Name"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        try {
            List<Instructor> all = new InstructorDAO().findAll();
            for (Instructor ins : all) {
                model.addRow(new Object[]{ins.getId(), ins.getName()});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "DB error: "+ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
        add(new JScrollPane(table), BorderLayout.CENTER);
    }
}
