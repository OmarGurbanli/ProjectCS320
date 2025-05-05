package dao;

import utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EnrollmentDAO {

    /** Записывает участника на класс || registering member to a class*/
    public void create(int memberId, int classId) throws SQLException {
        String sql = "INSERT INTO Enrollment(member_id, class_id, enrolled_at) VALUES(?,?,CURRENT_TIMESTAMP)";
        try (Connection c = DataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ps.setInt(2, classId);
            ps.executeUpdate();
        }
    }
}
