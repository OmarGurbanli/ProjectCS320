package gui;

import dao.ClassDAO;
import model.ClassSession;
import utils.AppContext;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class ClassPanel extends JPanel {
    private final DefaultTableModel model;
    private final JTable table;
    private final TableRowSorter<DefaultTableModel> sorter;

    public ClassPanel() {
        super(new BorderLayout());

        // Верхняя панель с поиском
        JPanel top = new JPanel(new BorderLayout(5, 5));
        JLabel lblSearch = new JLabel("Search:");
        JTextField tfSearch = new JTextField();
        top.add(lblSearch, BorderLayout.WEST);
        top.add(tfSearch, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        // Таблица занятий
        model = new DefaultTableModel(new Object[]{"ID", "Time", "Instructor", "Capacity"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        refreshTable();

        // Фильтрация по вводу в поле поиска
        tfSearch.getDocument().addDocumentListener(new DocumentListener() {
            private void filter() {
                String text = tfSearch.getText().trim();
                if (text.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    // (?i) — case-insensitive
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) { filter(); }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Кнопки для тренера
        if ("TRAINER".equals(AppContext.getCurrentUser().getRole())) {
            JPanel btnPanel = new JPanel();
            JButton btnAdd = new JButton("Add Class");
            JButton btnDel = new JButton("Delete Class");

            btnAdd.addActionListener(e -> onAdd());
            btnDel.addActionListener(e -> onDelete());

            btnPanel.add(btnAdd);
            btnPanel.add(btnDel);
            add(btnPanel, BorderLayout.SOUTH);
        }
    }

    /** Загружает данные из БД в модель таблицы */
    private void refreshTable() {
        model.setRowCount(0);
        try {
            List<ClassSession> classes = new ClassDAO().findAll();
            for (ClassSession cs : classes) {
                model.addRow(new Object[]{
                        cs.getId(),
                        cs.getTime(),
                        cs.getInstructorName(),
                        cs.getCapacity()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error loading classes: " + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Обработчик добавления нового класса */
    private void onAdd() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField tfTime = new JTextField("yyyy-MM-dd HH:mm");
        JTextField tfCap  = new JTextField();
        panel.add(new JLabel("Time (yyyy-MM-dd HH:mm):"));
        panel.add(tfTime);
        panel.add(new JLabel("Capacity:"));
        panel.add(tfCap);

        int ok = JOptionPane.showConfirmDialog(this, panel,
                "Add New Class", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        try {
            java.util.Date parsed = new SimpleDateFormat("yyyy-MM-dd HH:mm")
                    .parse(tfTime.getText().trim());
            java.sql.Timestamp ts = new java.sql.Timestamp(parsed.getTime());
            int cap = Integer.parseInt(tfCap.getText().trim());
            int instrId = AppContext.getCurrentUser().getId();

            new ClassDAO().create(ts, instrId, cap);
            refreshTable();
        } catch (ParseException pe) {
            JOptionPane.showMessageDialog(this,
                    "Invalid date format", "Input Error", JOptionPane.WARNING_MESSAGE);
        } catch (NumberFormatException ne) {
            JOptionPane.showMessageDialog(this,
                    "Capacity must be a number", "Input Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "DB Error: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Обработчик удаления выбранного класса */
    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a class first");
            return;
        }
        int modelRow = table.convertRowIndexToModel(row);
        int classId = (int) model.getValueAt(modelRow, 0);

        int ok = JOptionPane.showConfirmDialog(this,
                "Delete class ID=" + classId + "?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        try {
            new ClassDAO().delete(classId);
            refreshTable();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "DB Error: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
