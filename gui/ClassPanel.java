package gui;

import dao.ClassDAO;
import dao.EnrollmentDAO;
import model.ClassSession;
import utils.AppContext;

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
    private final JTextField tfSearch;
    private final JButton btnAdd, btnEdit, btnDelete;
    private final ClassTableModel model;
    private final JTable table;
    private final TableRowSorter<ClassTableModel> sorter;

    public ClassPanel() {
        super(new BorderLayout());

        // Получаем роль и ID текущего пользователя
        String role = AppContext.getCurrentUser().getRole();
        int userId = AppContext.getCurrentUser().getId();
        boolean isMember  = "MEMBER".equals(role);
        boolean isTrainer = "TRAINER".equals(role);

        // Верхняя панель с поиском и кнопками
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

        // Переименовываем / скрываем кнопки в зависимости от роли
        if (isMember) {
            btnAdd.setText("Register");
            btnEdit.setVisible(false);
            btnDelete.setText("Unregister");
        } else if (isTrainer) {
            btnAdd.setVisible(false);
        }

        // Настройка таблицы
        model  = new ClassTableModel();
        table  = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Фильтр поиска
        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) { filter(); }
            private void filter() {
                String txt = tfSearch.getText().trim();
                sorter.setRowFilter(txt.isEmpty()
                        ? null
                        : RowFilter.regexFilter("(?i)" + txt));
            }
        });

        // Загрузка данных
        loadData();

        // --- ADD / REGISTER ---
        btnAdd.addActionListener(e -> {
            if (isMember) {
                int vr = table.getSelectedRow();
                if (vr < 0) {
                    JOptionPane.showMessageDialog(this, "Select a class to register.");
                    return;
                }
                int mr = table.convertRowIndexToModel(vr);
                ClassSession cs = model.getClassSessionAt(mr);
                try {
                    new EnrollmentDAO().create(userId, cs.getId());
                    JOptionPane.showMessageDialog(this, "Registered successfully.");
                    SwingUtilities.invokeLater(() -> {
                        loadData();
                        table.clearSelection();
                    });
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error registering:\n" + ex.getMessage(),
                            "DB Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                AddClassDialog dlg = new AddClassDialog(
                        SwingUtilities.getWindowAncestor(this)
                );
                dlg.setVisible(true);
                if (dlg.isSaved()) {
                    SwingUtilities.invokeLater(() -> {
                        loadData();
                        table.clearSelection();
                    });
                }
            }
        });

        // --- EDIT ---
        btnEdit.addActionListener(e -> {
            int vr = table.getSelectedRow();
            if (vr < 0) {
                JOptionPane.showMessageDialog(this, "Please select a row to edit.");
                return;
            }
            int mr = table.convertRowIndexToModel(vr);
            ClassSession cs = model.getClassSessionAt(mr);

            if (isTrainer && cs.getInstructorId() != userId) {
                JOptionPane.showMessageDialog(this,
                        "You can only edit your own classes.",
                        "Permission Denied", JOptionPane.WARNING_MESSAGE);
                return;
            }

            EditClassDialog dlg = new EditClassDialog(
                    SwingUtilities.getWindowAncestor(this), cs
            );
            dlg.setVisible(true);
            if (dlg.isSaved()) {
                SwingUtilities.invokeLater(() -> {
                    loadData();
                    table.clearSelection();
                });
            }
        });

        // --- DELETE / UNREGISTER ---
        btnDelete.addActionListener(e -> {
            int vr = table.getSelectedRow();
            if (vr < 0) {
                JOptionPane.showMessageDialog(this,
                        isMember ? "Select a class to unregister." : "Select a class to delete.");
                return;
            }
            int mr = table.convertRowIndexToModel(vr);
            ClassSession cs = model.getClassSessionAt(mr);

            if (isMember) {
                try {
                    new EnrollmentDAO().delete(userId, cs.getId());
                    JOptionPane.showMessageDialog(this, "Unregistered successfully.");
                    SwingUtilities.invokeLater(() -> {
                        loadData();
                        table.clearSelection();
                    });
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error unregistering:\n" + ex.getMessage(),
                            "DB Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                if (isTrainer && cs.getInstructorId() != userId) {
                    JOptionPane.showMessageDialog(this,
                            "You can only delete your own classes.",
                            "Permission Denied", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete class ID " + cs.getId() + "?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );
                if (confirm != JOptionPane.YES_OPTION) return;
                try {
                    new ClassDAO().delete(cs.getId());
                    SwingUtilities.invokeLater(() -> {
                        loadData();
                        table.clearSelection();
                    });
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error deleting class: " + ex.getMessage(),
                            "DB Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void loadData() {
        try {
            List<ClassSession> all = new ClassDAO().findAll();
            if ("MEMBER".equals(AppContext.getCurrentUser().getRole())) {
                EnrollmentDAO edao = new EnrollmentDAO();
                List<Integer> enrolled = edao.findClassIdsByMember(AppContext.getCurrentUser().getId());
                List<ClassSession> filtered = new ArrayList<>();

                if (btnAdd.getText().equals("Register")) {
                    for (ClassSession cs : all) {
                        if (!enrolled.contains(cs.getId())) filtered.add(cs);
                    }
                } else {
                    for (ClassSession cs : all) {
                        if (enrolled.contains(cs.getId())) filtered.add(cs);
                    }
                }
                model.setSessions(filtered);
            } else {
                model.setSessions(new ArrayList<>(all));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading classes: " + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // nested table model
    private static class ClassTableModel extends AbstractTableModel {
        private final String[] cols = { "ID", "Instructor", "Time", "Capacity" };
        private List<ClassSession> sessions = new ArrayList<>();

        public void setSessions(List<ClassSession> list) {
            this.sessions = list;
            fireTableDataChanged();
        }

        public ClassSession getClassSessionAt(int row) {
            return sessions.get(row);
        }

        @Override public int getRowCount() { return sessions.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int col) { return cols[col]; }
        @Override public Class<?> getColumnClass(int col) {
            return col == 0 ? Integer.class : String.class;
        }
        @Override public boolean isCellEditable(int r, int c) { return false; }
        @Override public Object getValueAt(int row, int col) {
            ClassSession cs = sessions.get(row);
            return switch (col) {
                case 0 -> cs.getId();
                case 1 -> cs.getInstructorName();
                case 2 -> cs.getTime().toString();
                case 3 -> cs.getCapacity();
                default -> null;
            };
        }
    }
}
