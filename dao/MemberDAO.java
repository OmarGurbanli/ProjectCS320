package dao;

import model.Member;
import utils.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// get all members
public class MemberDAO {
    public List<Member> findAll() throws SQLException {
        String sql = "SELECT member_id, name, username, is_premium FROM Member";
        try (Connection conn = DataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Member> list = new ArrayList<>();
            while (rs.next()) {
                Member m = new Member();
                m.setMemberId(rs.getInt("member_id"));
                m.setName(rs.getString("name"));
                m.setUsername(rs.getString("username"));
                m.setPremium(rs.getBoolean("is_premium"));
                list.add(m);
            }
            return list;
        }
    }
}
