package org.example.model;

import java.sql.Connection;
import java.sql.Statement;

import static org.example.model.ConnectionModel.getConnection;

public class CreateTables {

    public static Connection createTables() {
        try {
            Connection conn = getConnection();

            Statement statement = conn.createStatement();

            String users = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "first_name VARCHAR(50) NOT NULL, " +
                    "last_name VARCHAR(50)NOT NULL, " +
                    "social_security_number LONG NOT NULL, " +
                    "email VARCHAR(100) NOT NULL, " +
                    "phone_number LONG NOT NULL, " +
                    "password VARCHAR(100) NOT NULL, " +
                    "created DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ");";

            String accounts = "CREATE TABLE IF NOT EXISTS accounts (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "user_id INT NOT NULL, " +
                    "account_number LONG NOT NULL, " +
                    "balance DOUBLE , " +
                    "created DATETIME DEFAULT CURRENT_TIMESTAMP " +
                    ");";

            String transactions = "CREATE TABLE IF NOT EXISTS transactions (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "user_id INT NOT NULL, " +
                    "amount FLOAT NOT NULL, " +
                    "created DATETIME DEFAULT CURRENT_TIMESTAMP " +
                    ");";

            String[] usersAccountsTransactions = {users, accounts, transactions};

            for (int i = 0; i < usersAccountsTransactions.length; i++) {
                int result = statement.executeUpdate(usersAccountsTransactions[i]);
                System.out.println("Result = " + result);
            }

            return conn;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
}
