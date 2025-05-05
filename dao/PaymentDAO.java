package dao;

import model.Payment;
import utils.DataSource;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    /** Возвращает все платежи с именами участников || gave back all payments*/
    public List<Payment> findAll() throws SQLException {
        String sql = """
            SELECT p.payment_id, p.member_id, m.username, p.amount, p.paid_at
              FROM Payment p
              JOIN Member m ON p.member_id = m.member_id
             ORDER BY p.paid_at DESC
            """;
        List<Payment> list = new ArrayList<>();
        try (Connection c = DataSource.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Payment(
                        rs.getInt("payment_id"),
                        rs.getInt("member_id"),
                        rs.getString("username"),
                        rs.getBigDecimal("amount"),
                        rs.getTimestamp("paid_at")
                ));
            }
        }
        return list;
    }

    /** Создаёт новый платёж из MemberPanel || make new payment from MemberPanel */
    public void create(int memberId, BigDecimal amount) throws SQLException {
        String sql = "INSERT INTO Payment(member_id, amount, paid_at) VALUES(?,?,CURRENT_TIMESTAMP)";
        try (Connection c = DataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ps.setBigDecimal(2, amount);
            ps.executeUpdate();
        }
    }
}
