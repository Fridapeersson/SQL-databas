package org.example.controller;

import org.example.model.Accounts;
import org.example.model.ConnectionModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountManager extends ConnectionModel {
    private List<Accounts> accounts;

    public List<Accounts> getAccountByUserId(int userId) {
        List<Accounts> userAccounts = new ArrayList<>();

        try (Connection conn = getConnection()) {
            PreparedStatement prepStatement = conn.prepareStatement("SELECT * FROM accounts WHERE user_id=?");
            prepStatement.setInt(1, userId);

            try (ResultSet result = prepStatement.executeQuery()) {
                while (result.next()) {
                    int accountId = result.getInt("id");
                    long accountNumber = result.getLong("account_number");
                    float balance = result.getFloat("balance");

                    // Skapa ett nytt Accounts-objekt och lägg till i listan
                    Accounts account = new Accounts(userId, balance);
                    account.setId(accountId);
                    account.setAccount_number(accountNumber);
                    userAccounts.add(account);
                }
            }
        } catch (SQLException e) {
            System.out.println("Något gick fel! " + e);
        }

        return userAccounts;
    }

    public void removeAccountByNumber(int userId, long accountNumber) {
        List<Accounts> userAccounts = getAccountByUserId(userId);

        Accounts accountToRemove = null;
        for (Accounts account : userAccounts) {
            if (account.getAccount_number() == accountNumber) {
                accountToRemove = account;
                break;
            }
        }

        if (accountToRemove != null) {
            // Ta bort kontot från användarens lista av konton
            userAccounts.remove(accountToRemove);
            System.out.println(userId);
            try (Connection conn = getConnection()) {
                String deleteAccountQuery = "DELETE FROM accounts WHERE id=?";
                PreparedStatement prepStatement = conn.prepareStatement(deleteAccountQuery);
                prepStatement.setInt(1, accountToRemove.getId());
                prepStatement.executeUpdate();

                System.out.println("Kontot med kontonummer " + accountNumber + " har tagits bort.");
            } catch (SQLException e) {
                System.out.println("Ett fel uppstod vid borttagning av kontot från databasen: " + e.getMessage());
            }
        } else {
            System.out.println("Kontot med kontonummer " + accountNumber + " hittades inte.");
        }
    }

}
