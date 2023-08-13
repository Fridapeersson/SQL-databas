package org.example.model;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.*;
import java.util.Date;

import static org.example.model.ConnectionModel.getConnection;


public class Accounts {
    private int id;
    private int user_id;
    private long account_number;
    private double balance;
    private Date created;

    protected MysqlDataSource dataSource;
    private Transactions transaction;

    public Accounts(int user_id, double balance) {
        this.user_id = user_id;
//        this.account_number = generateAccountNumber();
        this.balance = balance;
        this.transaction = new Transactions(account_number);
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

    public long getAccount_number() {
        return account_number;
    }
    public void setAccount_number(long account_number) {
        this.account_number = account_number;
        this.transaction.setAccount_number(account_number);
    }

    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Date getCreated() {
        return created;
    }
    public void setCreated(Date created) {
        this.created = created;
    }


    public void save() {
        try (Connection conn = getConnection()) {
            String saveAccountQuery = "INSERT INTO accounts SET user_id=?, account_number=?, balance=?";
            PreparedStatement prepStatement = conn.prepareStatement(saveAccountQuery, Statement.RETURN_GENERATED_KEYS);

            prepStatement.setInt(1, user_id);
            prepStatement.setLong(2, account_number);
            prepStatement.setDouble(3, balance);
            prepStatement.executeUpdate();

            // Get the generated account id and set it on the object
            ResultSet result = prepStatement.getGeneratedKeys();
            if (result.next()) {
                id = result.getInt(1);
            }
        } catch(SQLException e) {
            System.out.println(e);
        }
    }
}