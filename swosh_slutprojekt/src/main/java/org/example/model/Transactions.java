package org.example.model;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.util.Date;

import static org.example.Main.loggedInUserId;

public class Transactions {
    private int id;
    private int user_id;
    private float amount;
    private Date created;
    private long account_number;

    protected MysqlDataSource dataSource;
    private String transactionType;

    public Transactions(int user_id, float amount, Date created) {
        this.user_id = user_id;
        this.amount = amount;
        this.created = created;

        // Bestäm transactionType baserat på user_id
        if (user_id == loggedInUserId) {
            this.transactionType = "Skickad";
        } else {
            this.transactionType = "Mottagen";
        }

    }

    public Transactions(long accountNumber) {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public void setAccount_number(long accountNumber) {
    }
}