package gui;

import model.Member;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class MemberTableModel extends AbstractTableModel {
    private final String[] cols = {"ID", "Name", "Username", "Premium"};
    private List<Member> members = new ArrayList<>();

    public void setMembers(List<Member> members) {
        this.members = members;
        fireTableDataChanged();
    }

    @Override public int getRowCount() { return members.size(); }
    @Override public int getColumnCount() { return cols.length; }
    @Override public String getColumnName(int col) { return cols[col]; }
    @Override public Object getValueAt(int row, int col) {
        Member m = members.get(row);
        return switch (col) {
            case 0 -> m.getMemberId();
            case 1 -> m.getName();
            case 2 -> m.getUsername();
            case 3 -> m.isPremium();
            default -> null;
        };
    }
}
