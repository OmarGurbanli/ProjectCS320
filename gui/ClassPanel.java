package gui;

import dao.ClassDAO;
import model.ClassSession;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClassPanel extends JPanel {
    // search field and buttons
    private final JTextField tfSearch;
    private final JButton btnAdd, btnEdit, btnDelete;

    // table and model for class sessions
    private final ClassTableModel model;
    private final JTable table;
    private final TableRowSorter<ClassTableModel> sorter;

    public ClassPanel() {
        super(new BorderLayout());

        // top panel: search + action buttons
        JPanel top = new JPanel(new BorderLayout(5,5));
        tfSearch = new JTextField();
        top.add(new JLabel("Search Classes:"), BorderLayout.WEST);
        top.add(tfSearch, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAdd    = new JButton("Add");
        btnEdit   = new JButton("Edit");
        btnDelete = new JButton("Delete");
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        top.add(btnPanel, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        // table setup
        model  = new ClassTableModel();
        table  = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // search filter
        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String txt = tfSearch.getText();
                if (txt.trim().isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + txt));
                }
            }
        });

        // load data
        try {
            List<ClassSession> list = new ClassDAO().findAll();
            model.setSessions(new ArrayList<>(list));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading classes: " + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }

        // button listeners
        btnEdit.addActionListener(e -> onEdit());
        // TODO: add btnAdd and btnDelete listeners
    }

    private void onEdit() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a row to edit.");
            return;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        ClassSession cs = model.getClassSessionAt(modelRow);
        // TODO: implement your edit dialog here, e.g.:
        // EditClassDialog dlg = new EditClassDialog(SwingUtilities.getWindowAncestor(this), cs);
        // dlg.setVisible(true);
        // if (dlg.isSaved()) model.fireTableRowsUpdated(modelRow, modelRow);
    }

    // nested table model
    private static class ClassTableModel extends AbstractTableModel {
        private final String[] cols     = { "ID", "Name", "Instructor", "Time" };
        private List<ClassSession> sessions = new ArrayList<>();

        public void setSessions(List<ClassSession> list) {
            this.sessions = list;
            fireTableDataChanged();
        }

        public ClassSession getClassSessionAt(int row) {
            return sessions.get(row);
        }

        @Override public int getRowCount() {
            return sessions.size();
        }

        @Override public int getColumnCount() {
            return cols.length;
        }

        @Override public String getColumnName(int col) {
            return cols[col];
        }

        @Override public Class<?> getColumnClass(int col) {
            switch (col) {
                case 0: return Integer.class;
                case 3: return String.class; // or LocalDateTime.class
                default: return String.class;
            }
        }

        @Override public boolean isCellEditable(int r, int c) {
            return false;
        }

        @Override public Object getValueAt(int row, int col) {
            ClassSession cs = sessions.get(row);
            switch (col) {
                case 0: return cs.getId();
                case 1:
                    // Placeholder: replace toString() with the actual getter, e.g. cs.getTitle()
                    return cs.toString();
                case 2: return cs.getInstructorName();
                case 3: return cs.getTime().toString();
                default: return null;
            }
        }
    }
}
