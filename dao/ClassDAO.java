package dao;

import utils.DataSource;
import model.ClassSession;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassDAO {

    /** Возвращает все записи из таблицы Class вместе с именем инструктора */
    public List<ClassSession> findAll() throws SQLException {
        String sql = """
            SELECT c.class_id,
                   c.time,
                   c.instructor_id,             -- читаем ID инструктора
                   i.name AS instructor_name,
                   c.capacity
              FROM Class c
              JOIN Instructor i ON c.instructor_id = i.instructor_id
             ORDER BY c.time
            """;

        List<ClassSession> list = new ArrayList<>();
        try (Connection conn = DataSource.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new ClassSession(
                        rs.getInt("class_id"),
                        rs.getTimestamp("time"),
                        rs.getInt("instructor_id"),      // передаём ID инструктора
                        rs.getString("instructor_name"),
                        rs.getInt("capacity")
                ));
            }
        }
        return list;
    }

    /** Создаёт новый сеанс */
    public void create(Timestamp time, int instrId, int capacity) throws SQLException {
        String sql = "INSERT INTO Class(time, instructor_id, capacity) VALUES(?,?,?)";
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, time);
            ps.setInt(2, instrId);
            ps.setInt(3, capacity);
            ps.executeUpdate();
        }
    }

    /** Обновляет существующий сеанс */
    public void update(int id, Timestamp time, int instrId, int capacity) throws SQLException {
        String sql = "UPDATE Class SET time = ?, instructor_id = ?, capacity = ? WHERE class_id = ?";
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, time);
            ps.setInt(2, instrId);
            ps.setInt(3, capacity);
            ps.setInt(4, id);
            ps.executeUpdate();
        }
    }

    /** Удаляет сеанс по ID */
    public void delete(int classId) throws SQLException {
        String sql = "DELETE FROM Class WHERE class_id = ?";
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            ps.executeUpdate();
        }
    }
}
