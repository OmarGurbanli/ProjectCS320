package gui;

import dao.InstructorDAO;
import model.Instructor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class InstructorPanel extends JPanel {


        private final InstructorTableModel model;
        private final JTable table;
        private final TableRowSorter<InstructorTableModel> sorter;

    public InstructorPanel() {
        super(new BorderLayout());

        //search and admin button
        JPanel top = new JPanel(new BorderLayout(5,5));
        JTextField tfSearch = new JTextField();
        top.add(new JLabel("Search:"), BorderLayout.WEST);
        top.add(tfSearch, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        String role = AppContext.getCurrentUser().getRole();
        if ("ADMIN".equals(role)) {
            JButton btnAdd = new JButton("Add Instructor"); // *** NEW ***
            JButton btnEdit = new JButton("Edit Instructor"); // *** NEW ***
            JButton btnDel = new JButton("Delete Instructor"); // *** NEW ***
            btnAdd.addActionListener(e -> onAdd()); // *** NEW ***
            btnEdit.addActionListener(e -> onEdit()); // *** NEW ***
            btnDel.addActionListener(e -> onDelete()); // *** NEW ***
            btnPanel.add(btnAdd);
            btnPanel.add(btnEdit);
            btnPanel.add(btnDel);
        }
        top.add(btnPanel, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        // *** NEW *** Table setup with custom model and sorter
        model = new InstructorTableModel();
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        add(new JScrollPane(table), BorderLayout.CENTER);
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
private static class InstructorTableModel extends AbstractTableModel {
    private final String[] cols = {"ID", "Name"};
    private List<Instructor> list = List.of();

    public void setInstructors(List<Instructor> instructors) {
        this.list = instructors;
        fireTableDataChanged();
    }

    //daha düzenli görünmesi için instructor table model ekledim
    public Instructor getInstructorAt(int row) {
        return list.get(row);
    }

    @Override public int getRowCount() {
        return list.size(); }
    @Override public int getColumnCount() {
        return cols.length; }
    @Override public String getColumnName(int col) {
        return cols[col]; }
    @Override public Class<?> getColumnClass(int col) { //her cell in data typeı, daha düzenli olsun diye
        return col == 0 ? Integer.class : String.class; }
    @Override public boolean isCellEditable(int r, int c) { //datalar değiştirilemez, daha güvenli
        return false; }
    @Override public Object getValueAt(int row, int col) {
        Instructor ins = list.get(row);
        return col == 0 ? ins.getId() : ins.getName();
    }
}
}

