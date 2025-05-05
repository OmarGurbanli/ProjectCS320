package dao;

import model.GymStatus;
import utils.DataSource;

import java.sql.*;

public class GymStatusDAO {

    /** Читает глобальный флаг из первой строки GymStatus || getting gym status */
    public GymStatus getStatus() throws SQLException {
        String sql = "SELECT is_active FROM GymStatus LIMIT 1";
        try (Connection c = DataSource.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return new GymStatus(rs.getBoolean("is_active"));
            }
        }
        // если таблица пуста — считаем, что зал открыт
        return new GymStatus(true);
    }

    /** Устанавливает флаг открыт/закрыт для всех строк (должна быть только одна) || setting gym status */
    public void setStatus(boolean active) throws SQLException {
        String sql = "UPDATE GymStatus SET is_active = ?";
        try (Connection c = DataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.executeUpdate();
        }
    }
}
