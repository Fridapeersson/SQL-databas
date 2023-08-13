package org.example.controller;

import org.example.model.ConnectionModel;
import org.example.model.Transactions;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static org.example.Main.loggedInUserId;

public class TransactionManager extends ConnectionModel {
    public List<Transactions> getTransactionsByUserId(int userId) {
        List<Transactions> transactions = new ArrayList<>();

        try (Connection conn = getConnection()) {
            String transactionQuery = "SELECT user_id, amount, created " +
                    "FROM transactions " +
                    "WHERE user_id=? " +
                    "AND created >= ? " +
                    "AND created <= ? " +
                    "AND user_id = ? " +
                    "ORDER BY created DESC";

            PreparedStatement prepStatement = conn.prepareStatement(transactionQuery);
            prepStatement.setInt(1, userId);

            ResultSet result = prepStatement.executeQuery();

            while(result.next()) {
                int id = result.getInt("user_id");
                float amount = result.getFloat("amount");
                Date created = result.getDate("created");

                Transactions transaction = new Transactions(userId, amount, created);
                transaction.setId(id);
                transaction.setAmount(amount);

                transactions.add(transaction);
            }
        } catch(SQLException e) {
            System.out.println(e);
        }
        return transactions;
    }



    public List<Transactions> getTransactionsByUserIdAndDateRange(int userId, Date startDate, Date endDate) {
        List<Transactions> transactions = new ArrayList<>();

        try (Connection conn = getConnection()) {
            String transactionQuery = "SELECT * FROM transactions WHERE user_id=? AND created >= ? AND created <= ? ORDER BY created DESC";

            PreparedStatement prepStatement = conn.prepareStatement(transactionQuery);
            prepStatement.setInt(1, userId);
            prepStatement.setDate(2, new java.sql.Date(startDate.getTime()));
            prepStatement.setDate(3, new java.sql.Date(endDate.getTime()));

            ResultSet result = prepStatement.executeQuery();

            while (result.next()) {
                int id = result.getInt("user_id");
                float amount = result.getFloat("amount");
                Date created = result.getDate("created");

                Transactions transaction = new Transactions(userId, amount, created);
                transaction.setId(id);
                transaction.setAmount(amount);

                transactions.add(transaction);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return transactions;
    }

    public List<Transactions> getTransactionsByUserIdAndDateRangeReceived(int userId, Date startDate, Date endDate) {
        List<Transactions> transactions = new ArrayList<>();

        try (Connection conn = getConnection()) {
            String transactionQuery = "SELECT * FROM transactions WHERE user_id!=? AND created >= ? AND created <= ? ORDER BY created DESC";

            PreparedStatement prepStatement = conn.prepareStatement(transactionQuery);
            prepStatement.setInt(1, userId);
            prepStatement.setDate(2, new java.sql.Date(startDate.getTime()));
            prepStatement.setDate(3, new java.sql.Date(endDate.getTime()));

            ResultSet result = prepStatement.executeQuery();

            while (result.next()) {
                int id = result.getInt("id");
                int receivedUserId = result.getInt("user_id");
                float amount = result.getFloat("amount");
                Date created = result.getDate("created");

                Transactions transaction = new Transactions(receivedUserId, amount, created);
                transaction.setId(id);
                transaction.setAmount(amount);

                transactions.add(transaction);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return transactions;
    }


    public void saveTransactions(Transactions transaction) {
        try(Connection conn = getConnection()) {
            String saveTransactionsQuery = "INSERT INTO transactions SET user_id=?, amount=?";

            PreparedStatement prepStatement = conn.prepareStatement(saveTransactionsQuery);
            prepStatement.setInt(1, transaction.getUser_id());
            prepStatement.setFloat(2, transaction.getAmount());

            prepStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }


    public static void handleSendMoney() {
        Scanner scanner = new Scanner(System.in);

        if (loggedInUserId == 0) {
            System.out.println("Ingen användare inloggad");
            return;
        }

        System.out.println("Ange mottagarens telefonnummer");
        String recipientPhoneNumber = scanner.nextLine();

        if (!hasSwoshAccount(recipientPhoneNumber)) {
            System.out.println("Kunde inte skicka eftersom mottagaren inte har ett Swosh konto");
            return;
        }

        System.out.println("Belopp att skicka: ");
        double amountToSend = scanner.nextDouble();

        if (moneyTransfer(loggedInUserId, recipientPhoneNumber, amountToSend)) {
            System.out.println("Överföringen lyckades!");
        } else {
            System.out.println("Överföringen misslyckades. Kontrollera ditt saldo eller försök igen senare.");
        }
    }

    // Kolla om användaren har ett Swosh-konto baserat på telefonnummer
    private static boolean hasSwoshAccount(String phoneNumber) {
        try (Connection conn = getConnection()) {
            String query = "SELECT COUNT(*) FROM users WHERE phone_number=?";
            PreparedStatement prepStatement = conn.prepareStatement(query);
            prepStatement.setString(1, phoneNumber);

            ResultSet result = prepStatement.executeQuery();
            if (result.next()) {
                int count = result.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            System.out.println("Något gick snett! " + e);
        }
        return false;
    }

    // Utför överföringen av pengar från avsändarens konto till mottagarens konto
    private static boolean moneyTransfer(int senderId, String recipientPhoneNumber, double amount) {
        try (Connection conn = getConnection()) {
            // Starta en transaktion
            conn.setAutoCommit(false);

            // Kontrollera att avsändaren har tillräckligt med pengar på kontot
            if (!checkBalanceBeforeSend(senderId, amount)) {
                System.out.println("Du har inte tillräckligt med pengar på ditt konto");
                conn.rollback();
                return false;
            }

            // Hämta avsändarens konto från databasen
            String selectSenderAccountQuery = "SELECT balance FROM accounts WHERE user_id=?";
            PreparedStatement prepStatement = conn.prepareStatement(selectSenderAccountQuery);
            prepStatement.setInt(1, senderId);
            ResultSet result = prepStatement.executeQuery();

            if (result.next()) {
                double senderBalance = result.getDouble("balance");

                // Uppdatera beloppet
                double updatedSenderBalance = senderBalance - amount;

                // Uppdatera avsändarens konto i databasen med det nya saldot
                String updateSenderAccountQuery = "UPDATE accounts SET balance=? WHERE user_id=?";
                PreparedStatement prepStatement2 = conn.prepareStatement(updateSenderAccountQuery);
                prepStatement2.setDouble(1, updatedSenderBalance);
                prepStatement2.setInt(2, senderId);
                prepStatement2.executeUpdate();

                // Hämta mottagarens konto från databasen baserat på telefonnummer
                String selectRecipientAccountQuery = "SELECT balance FROM accounts WHERE user_id=(SELECT id FROM users WHERE phone_number=?)";
                PreparedStatement prepStatement3 = conn.prepareStatement(selectRecipientAccountQuery);
                prepStatement3.setString(1, recipientPhoneNumber);
                ResultSet recipientResult = prepStatement3.executeQuery();

                if (recipientResult.next()) {
                    double recipientBalance = recipientResult.getDouble("balance");

                    // Uppdatera beloppet
                    double updatedRecipientBalance = recipientBalance + amount;

                    // Uppdatera mottagarens konto i databasen med det nya saldot
                    String updateRecipientAccountQuery = "UPDATE accounts SET balance=? WHERE user_id=(SELECT id FROM users WHERE phone_number=?)";
                    PreparedStatement prepStatement4 = conn.prepareStatement(updateRecipientAccountQuery);
                    prepStatement4.setDouble(1, updatedRecipientBalance);
                    prepStatement4.setString(2, recipientPhoneNumber);
                    prepStatement4.executeUpdate();

                    // Commit transaktionen
                    conn.commit();

                    // Skapa ett Transactions-objekt för att representera transaktionen
                    Transactions transaction = new Transactions(senderId, (float) amount, new Date());

                    // Spara transaktionen i databasen
                    TransactionManager transactionManager = new TransactionManager();
                    transactionManager.saveTransactions(transaction);

                    return true;
                }
            }

            // Rollback transaktionen om något gick fel
            conn.rollback();
            return false;
        } catch (SQLException e) {
            System.out.println("Något gick snett! " + e);
            return false;
        }
    }

    // Kontrollera om användaren har tillräckligt med pengar på kontot
    private static boolean checkBalanceBeforeSend(int userId, double amount) {
        try (Connection conn = getConnection()) {
            String query = "SELECT balance FROM accounts WHERE user_id=?";
            PreparedStatement prepStatement = conn.prepareStatement(query);
            prepStatement.setInt(1, userId);

            ResultSet result = prepStatement.executeQuery();
            if (result.next()) {
                double balance = result.getDouble("balance");
                return balance >= amount;
            }
        } catch (SQLException e) {
            System.out.println("Något gick snett! " + e);
        }
        return false;
    }
}
