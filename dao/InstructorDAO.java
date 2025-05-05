package dao;

import model.Instructor;
import utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstructorDAO {

    /** Возвращает всех инструкторов || back all instructors */
    public List<Instructor> findAll() throws SQLException {
        String sql = "SELECT instructor_id, name FROM Instructor ORDER BY name";
        List<Instructor> list = new ArrayList<>();
        try (Connection c = DataSource.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Instructor(
                        rs.getInt("instructor_id"),
                        rs.getString("name")
                ));
            }
        }
        return list;
    }
}
