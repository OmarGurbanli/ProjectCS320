package dao;

import model.User;
import utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    /** Вернёт список всех пользователей из трёх таблиц // gives lists of all users */


    public List<User> findAll() throws SQLException {
        List<User> out = new ArrayList<>();
        out.addAll(fetchUsers(
                "SELECT member_id AS id, username, password_hash, 'MEMBER' AS role FROM Member"));
        out.addAll(fetchUsers(
                "SELECT instructor_id AS id, name AS username, password_hash, 'TRAINER' AS role FROM Instructor"));
        out.addAll(fetchUsers(
                "SELECT admin_id AS id, name AS username, password_hash, 'ADMIN' AS role FROM Admin"));
        return out;
    }

    /** Ищем одного пользователя по логину во всех трёх таблицах // After Login shows the correct user */
    public User findByLogin(String login) throws SQLException {
        User u = fetchSingleUser(
                "SELECT member_id AS id, username, password_hash, 'MEMBER' AS role FROM Member WHERE username = ?",
                login);
        if (u != null) return u;

        u = fetchSingleUser(
                "SELECT instructor_id AS id, name AS username, password_hash, 'TRAINER' AS role FROM Instructor WHERE name = ?",
                login);
        if (u != null) return u;

        return fetchSingleUser(
                "SELECT admin_id AS id, name AS username, password_hash, 'ADMIN' AS role FROM Admin WHERE name = ?",
                login);
    }


    private List<User> fetchUsers(String sql) throws SQLException {
        List<User> list = new ArrayList<>();
        try (Connection c = DataSource.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),  // <- здесь правильное имя столбца
                        rs.getString("role")
                ));
            }
        }
        return list;
    }

    private User fetchSingleUser(String sql, String param) throws SQLException {
        try (Connection c = DataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password_hash"),  // <- и здесь
                            rs.getString("role")
                    );
                }
            }
        }
        return null;
    }



    /** Создание нового юзера в соответствующей таблице // Create a new user  */
    public void create(String username, String passwordHash, String role) throws SQLException {
        String sql;
        switch (role) {
            case "MEMBER":
                sql = "INSERT INTO Member(name, username, password_hash) VALUES(?,?,?)";
                break;
            case "TRAINER":
                sql = "INSERT INTO Instructor(name, password_hash) VALUES(?,?)";
                break;
            case "ADMIN":
                sql = "INSERT INTO Admin(name, password_hash) VALUES(?,?)";
                break;
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
        try (Connection c = DataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.executeUpdate();
        }
    }

    /** Удаление по ID и роли */
    public void delete(int id, String role) throws SQLException {
        try (Connection c = DataSource.getConnection()) {
            c.setAutoCommit(false);
            try {
                switch (role) {
                    case "MEMBER":
                        executeUpdate(c, "DELETE FROM Enrollment WHERE member_id = ?", id);
                        executeUpdate(c, "DELETE FROM Payment WHERE member_id = ?", id);
                        executeUpdate(c, "DELETE FROM GymStatus WHERE member_id = ?", id);
                        executeUpdate(c, "DELETE FROM Member WHERE member_id = ?", id);
                        break;
                    case "TRAINER":
                        executeUpdate(c, "DELETE FROM Instructor WHERE instructor_id = ?", id);
                        break;
                    case "ADMIN":
                        executeUpdate(c, "DELETE FROM Admin WHERE admin_id = ?", id);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown role: " + role);
                }
                c.commit();
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    private void executeUpdate(Connection c, String sql, int param) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, param);
            ps.executeUpdate();
        }
    }
}
