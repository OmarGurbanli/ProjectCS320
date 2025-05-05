package dao;

import model.User;
import utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    /** Вернёт список всех пользователей из трёх таблиц */
    public List<User> findAll() throws SQLException {
        List<User> out = new ArrayList<>();
        out.addAll(findMembers());
        out.addAll(findInstructors());
        out.addAll(findAdmins());
        return out;
    }

    private List<User> findMembers() throws SQLException {
        String sql = "SELECT member_id AS id, username, password_hash, 'MEMBER' AS role FROM Member";
        return fetchUsers(sql);
    }

    private List<User> findInstructors() throws SQLException {
        String sql = "SELECT instructor_id AS id, name AS username, password_hash, 'TRAINER' AS role FROM Instructor";
        return fetchUsers(sql);
    }

    private List<User> findAdmins() throws SQLException {
        String sql = "SELECT admin_id AS id, name AS username, password_hash, 'ADMIN' AS role FROM Admin";
        return fetchUsers(sql);
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
                        rs.getString("password_hash"),
                        rs.getString("role")
                ));
            }
        }
        return list;
    }

    /** Ищем одного пользователя по логину (username/name) во всех трёх таблицах */
    public User findByLogin(String login) throws SQLException {
        User u = findMember(login);
        if (u != null) return u;
        u = findInstructor(login);
        if (u != null) return u;
        return findAdmin(login);
    }

    private User findMember(String username) throws SQLException {
        String sql = "SELECT member_id AS id, username, password_hash, 'MEMBER' AS role FROM Member WHERE username = ?";
        try (Connection c = DataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("role")
                    );
                }
            }
        }
        return null;
    }

    private User findInstructor(String name) throws SQLException {
        String sql = "SELECT instructor_id AS id, name AS username, password_hash, 'TRAINER' AS role FROM Instructor WHERE name = ?";
        try (Connection c = DataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("role")
                    );
                }
            }
        }
        return null;
    }

    private User findAdmin(String name) throws SQLException {
        String sql = "SELECT admin_id AS id, name AS username, password_hash, 'ADMIN' AS role FROM Admin WHERE name = ?";
        try (Connection c = DataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("role")
                    );
                }
            }
        }
        return null;
    }

    /** Создание нового юзера в соответствующей таблице */
    public void create(String username, String passwordHash, String role) throws SQLException {
        String sql;
        switch (role) {
            case "MEMBER":
                // раньше было только username, теперь вставляем name и username
                sql = "INSERT INTO Member(name, username, password_hash) VALUES(?,?,?)";
                try (Connection c = DataSource.getConnection();
                     PreparedStatement ps = c.prepareStatement(sql)) {
                    ps.setString(1, username);       // name = username
                    ps.setString(2, username);       // username
                    ps.setString(3, passwordHash);
                    ps.executeUpdate();
                }
                return;
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
                        // 1) Удаляем все записи об участии в классах
                        try (PreparedStatement ps = c.prepareStatement(
                                "DELETE FROM Enrollment WHERE member_id = ?")) {
                            ps.setInt(1, id);
                            ps.executeUpdate();
                        }
                        // 2) Удаляем все записи об оплатах
                        try (PreparedStatement ps = c.prepareStatement(
                                "DELETE FROM Payment WHERE member_id = ?")) {
                            ps.setInt(1, id);
                            ps.executeUpdate();
                        }
                        // 3) Удаляем статус в GymStatus (если есть)
                        try (PreparedStatement ps = c.prepareStatement(
                                "DELETE FROM GymStatus WHERE member_id = ?")) {
                            ps.setInt(1, id);
                            ps.executeUpdate();
                        }
                        // 4) Наконец, сам Member
                        try (PreparedStatement ps = c.prepareStatement(
                                "DELETE FROM Member WHERE member_id = ?")) {
                            ps.setInt(1, id);
                            ps.executeUpdate();
                        }
                        break;

                    case "TRAINER":
                        try (PreparedStatement ps = c.prepareStatement(
                                "DELETE FROM Instructor WHERE instructor_id = ?")) {
                            ps.setInt(1, id);
                            ps.executeUpdate();
                        }
                        break;

                    case "ADMIN":
                        try (PreparedStatement ps = c.prepareStatement(
                                "DELETE FROM Admin WHERE admin_id = ?")) {
                            ps.setInt(1, id);
                            ps.executeUpdate();
                        }
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

}
