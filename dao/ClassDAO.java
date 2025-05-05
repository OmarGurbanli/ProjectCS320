package dao;

import model.ClassSession;
import utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassDAO {

    /** Возвращает все записи из таблицы Class вместе с именем инструктора || Class table go back. */
    public List<ClassSession> findAll() throws SQLException {
        String sql = """
            SELECT c.class_id, c.time, i.name AS instructor_name, c.capacity
              FROM Class c
              JOIN Instructor i ON c.instructor_id = i.instructor_id
             ORDER BY c.time
            """;
        List<ClassSession> list = new ArrayList<>();
        try (Connection c = DataSource.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ClassSession(
                        rs.getInt("class_id"),
                        rs.getTimestamp("time"),
                        rs.getString("instructor_name"),
                        rs.getInt("capacity")
                ));
            }
        }
        return list;
    }
    // ClassDAO create stat:
    public void create(Timestamp time, int instrId, int capacity) throws SQLException {
        String sql = "INSERT INTO Class(time,instructor_id,capacity) VALUES(?,?,?)";
        try (Connection c = DataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setTimestamp(1, time);
            ps.setInt(2, instrId);
            ps.setInt(3, capacity);
            ps.executeUpdate();
        }
    }

    public void delete(int classId) throws SQLException {
        String sql = "DELETE FROM Class WHERE class_id = ?";
        try (Connection c = DataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, classId);
            ps.executeUpdate();
        }
    }

}
