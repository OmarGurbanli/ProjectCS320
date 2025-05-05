package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Payment {
    private final int id;
    private final int memberId;
    private final String username;
    private final BigDecimal amount;
    private final Timestamp paidAt;

    public Payment(int id, int memberId, String username, BigDecimal amount, Timestamp paidAt) {
        this.id = id;
        this.memberId = memberId;
        this.username = username;
        this.amount = amount;
        this.paidAt = paidAt;
    }

    public int getId() {
        return id;
    }

    public int getMemberId() {
        return memberId;
    }

    public String getUsername() {
        return username;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Timestamp getPaidAt() {
        return paidAt;
    }
}
