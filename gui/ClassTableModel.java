package gui;

import model.Class;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ClassTableModel extends AbstractTableModel {
    private final String[] cols = {"ID", "Time", "Instructor ID", "Capacity"};
    private List<Class> classes = new ArrayList<>();

    public void setClasses(List<Class> classes) {
        this.classes = classes;
        fireTableDataChanged();
    }

    @Override public int getRowCount() { return classes.size(); }
    @Override public int getColumnCount() { return cols.length; }
    @Override public String getColumnName(int col) { return cols[col]; }
    @Override public Object getValueAt(int row, int col) {
        Class cls = classes.get(row);
        return switch (col) {
            case 0 -> cls.getClassId();
            case 1 -> cls.getTime();
            case 2 -> cls.getInstructorId();
            case 3 -> cls.getCapacity();
            default -> null;
        };
    }
}
