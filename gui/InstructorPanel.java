package gui;

import dao.InstructorDAO;
import model.Instructor;
import utils.AppContext;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// добавляем правильный импорт для RowFilter
import javax.swing.RowFilter;

public class InstructorPanel extends JPanel {

    //search and admin
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
        if ("admin".equals(role)) {
            JButton addBtn = new JButton("Add Instructor");
            // TODO: addBtn action listener
            btnPanel.add(addBtn);
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

        // Поиск по таблице
        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String text = tfSearch.getText();
                if (text.trim().isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        // Загрузка данных из БД
        try {
            List<Instructor> all = new InstructorDAO().findAll();
            model.setInstructors(new ArrayList<>(all));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "DB error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Вложенная модель таблицы
    private static class InstructorTableModel extends AbstractTableModel {
        private final String[] cols = {"ID", "Name"};
        private List<Instructor> list = new ArrayList<>();

        public void setInstructors(List<Instructor> instructors) {
            this.list = instructors;
            fireTableDataChanged();
        }

        public Instructor getInstructorAt(int row) {
            return list.get(row);
        }

        @Override public int getRowCount() {
            return list.size();
        }

        @Override public int getColumnCount() {
            return cols.length;
        }

        @Override public String getColumnName(int col) {
            return cols[col];
        }

        @Override public Class<?> getColumnClass(int col) { //her cell in data typeı, daha düzenli olsun diye
            return col == 0 ? Integer.class : String.class;
        }

        @Override public boolean isCellEditable(int r, int c) { //datalar değiştirilemez, daha güvenli
            return false;
        }

        @Override public Object getValueAt(int row, int col) {
            Instructor ins = list.get(row);
            return col == 0 ? ins.getId() : ins.getName();
        }
    }
}
