package dao;

import utils.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO для работы с записями участников на занятия.
 */
public class EnrollmentDAO {

    /** Записывает участника на класс */
    public void create(int memberId, int classId) throws SQLException {
        String sql = "INSERT INTO Enrollment(member_id, class_id, enrolled_at) VALUES(?,?,CURRENT_TIMESTAMP)";
        try (Connection c = DataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ps.setInt(2, classId);
            ps.executeUpdate();
        }
    }

    /** Отменяет запись участника на класс */
    public void delete(int memberId, int classId) throws SQLException {
        String sql = "DELETE FROM Enrollment WHERE member_id = ? AND class_id = ?";
        try (Connection c = DataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ps.setInt(2, classId);
            ps.executeUpdate();
        }
    }

    /** Возвращает список ID занятий, на которые записан участник */
    public List<Integer> findClassIdsByMember(int memberId) throws SQLException {
        String sql = "SELECT class_id FROM Enrollment WHERE member_id = ?";
        List<Integer> list = new ArrayList<>();
        try (Connection c = DataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getInt("class_id"));
                }
            }
        }
        return list;
    }

    // Тестовый main для проверки работы DAO без UI
    public static void main(String[] args) {
        EnrollmentDAO dao = new EnrollmentDAO();
        int testMemberId = 1;
        int testClassId = 1;
        try {
            dao.create(testMemberId, testClassId);
            System.out.println("Registered member " + testMemberId + " to class " + testClassId);
            List<Integer> enrolled = dao.findClassIdsByMember(testMemberId);
            System.out.println("Enrolled classes: " + enrolled);
            dao.delete(testMemberId, testClassId);
            System.out.println("Unregistered member " + testMemberId + " from class " + testClassId);
            enrolled = dao.findClassIdsByMember(testMemberId);
            System.out.println("Enrolled classes after delete: " + enrolled);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
