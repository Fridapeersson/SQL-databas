package org.example.controller;

import org.example.model.Users;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import static org.example.Main.*;
import static org.example.model.ConnectionModel.getConnection;
import static org.example.view.viewManager.handleMenu;
import static org.example.view.viewManager.startMenu;

public class UserManager {


    //Lägg till användare
    public void addUser(Users user) {
        try (Connection conn = getConnection()) {
            // Kontrollera om användaren redan finns i databasen baserat på personnummer, telefonnummer och e-postadress
            if (userExists(user.getSocial_security_number(), user.getPhone_number(), user.getEmail())) {
                System.out.println("En användare med samma personnummer, telefonnummer eller e-postadress finns redan");
                startMenu();
                return;
            }

            String phoneNumberString = Long.toString(user.getPhone_number());
            if (phoneNumberString.length() != 10 || !phoneNumberString.matches("\\d{10}")) {
                System.out.println("Ogiltigt telefonnummer. Var god ange ett 10-siffrigt telefonnummer utan bokstäver eller specialtecken.");
                startMenu();
                return;
            }


            String createUsersQuery = "INSERT INTO users SET first_name=?, last_name=?, social_security_number=?, email=?, phone_number=?, password=?";
            PreparedStatement prepStatement = conn.prepareStatement(createUsersQuery);

            prepStatement.setString(1, user.getFirst_name());
            prepStatement.setString(2, user.getLast_name());
            prepStatement.setLong(3, user.getSocial_security_number());
            prepStatement.setString(4, user.getEmail());
            prepStatement.setLong(5, user.getPhone_number());
            // Skapa hash av lösenordet
            String hashedPassword = hashPassword(user.getPassword());
            prepStatement.setString(6, hashedPassword);

            int rowsAffected = prepStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Användaren har lagts till i databasen");
            } else {
                System.out.println("Något blev fel!");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }

    }

    //kollar om användaren redan finns
    private boolean userExists(long socialSecurityNumber, long phoneNumber, String email) throws SQLException {
        try (Connection conn = getConnection()) {
            String checkUserQuery = "SELECT COUNT(*) FROM users WHERE social_security_number=? OR phone_number=? OR email=?";
            PreparedStatement prepStatement = conn.prepareStatement(checkUserQuery);
            prepStatement.setLong(1, socialSecurityNumber);
            prepStatement.setLong(2, phoneNumber);
            prepStatement.setString(3, email);
            ResultSet resultSet = prepStatement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }

            return false;
        }
    }

    //Ta bort användaren
    public void deleteUser(int id) {
        try (Connection conn = getConnection()) {
            String deleteQuery = "DELETE users, accounts, transactions FROM users "
                    + "LEFT JOIN accounts ON users.id = accounts.user_id "
                    + "LEFT JOIN transactions ON users.id = transactions.user_id "
                    + "WHERE users.id = ?";
            PreparedStatement prepStatement = conn.prepareStatement(deleteQuery);
            prepStatement.setInt(1, id);
            prepStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public static void handleUpdateDetails() {
        Scanner scanner = new Scanner(System.in);

        // Kontrollera om användaren är inloggad
        if (loggedInUserId == 0) {
            System.out.println("Ingen användare är för närvarande inloggad.");
            return;
        }

        // Hämta användaruppgifter från databasen
        Users user = getUserById(loggedInUserId);

        if (user == null) {
            System.out.println("Kunde inte hitta användaren.");
            return;
        }

        System.out.println("Välj vilken uppgift du vill uppdatera");
        System.out.println("1. Förnamn");
        System.out.println("2. Efternamn");
        System.out.println("3. Epost");
        System.out.println("4. Telefonnummer");
        System.out.println("5. Lösenord");
        System.out.println("6. Tillbaka till menyn");

        int choice = scanner.nextInt();
        scanner.nextLine();

        try (Connection conn = getConnection()) {
            String query = "UPDATE users SET first_name=?, last_name=?, email=?, phone_number=?, password=? WHERE id=?";
            PreparedStatement prepStatement = conn.prepareStatement(query);

            switch (choice) {
                case 1:
                    System.out.println("Ange det nya förnamnet");
                    String newFirstName = scanner.nextLine();
                    user.setFirst_name(newFirstName);
                    break;
                case 2:
                    System.out.println("Ange det nya efternamnet");
                    String newLastName = scanner.nextLine();
                    user.setLast_name(newLastName);
                    break;
                case 3:
                    System.out.println("Ange den nya eposten");
                    String newEmail = scanner.nextLine();
                    user.setEmail(newEmail);
                    break;
                case 4:
                    System.out.println("Ange det nya telefonnumret (10 siffror)");
                    try{
                        String phoneNumberInput = scanner.nextLine();
                        if(phoneNumberInput.length() != 10) {
                            System.out.println("Ogiltligt telefonnummer, måste innehålla 10 siffror");
                            return;
                        }

                    long newPhoneNumber = Long.parseLong(phoneNumberInput);
//                    scanner.nextLine();
                    user.setPhone_number(newPhoneNumber);
                    } catch (NumberFormatException e) {
                        System.out.println("Ogiltligt telefonnummer. Prova igen");
                        return;
                    }
                    break;
                case 5:
                    System.out.println("Ange det nya lösenordet");
                    String newPassword = scanner.nextLine();
                    String hashedPassword = hashPassword(newPassword);
                    user.setPassword(hashedPassword);
                    break;
                case 6:
                    handleMenu();
                    return;
                default:
                    System.out.println("Ogiltligt val, prova igen");
                    return;
            }

            prepStatement.setString(1, user.getFirst_name());
            prepStatement.setString(2, user.getLast_name());
            prepStatement.setString(3, user.getEmail());
            prepStatement.setLong(4, user.getPhone_number());
            prepStatement.setString(5, user.getPassword());
            prepStatement.setInt(6, user.getId());

            int rowsAffected = prepStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Dina uppgifter har uppdaterats!");
            } else {
                System.out.println("Det gick inte att uppdatera uppgifterna");
            }
        } catch (SQLException e) {
            System.out.println("Något blev fel! " + e);
        }
    }

    private static Users getUserById(int id) {
        try (Connection conn = getConnection()) {
            String query = "SELECT * FROM users WHERE id=?";
            PreparedStatement prepStatement = conn.prepareStatement(query);
            prepStatement.setInt(1, id);
            ResultSet result = prepStatement.executeQuery();

            if (result.next()) {
                Users user = new Users(
                        result.getString("first_name"),
                        result.getString("last_name"),
                        result.getLong("social_security_number"),
                        result.getString("email"),
                        result.getLong("phone_number"),
                        result.getString("password")
                );
                user.setId(result.getInt("id"));
                user.setCreated(result.getDate("created"));
                return user;
            }
        } catch (SQLException e) {
            System.out.println("Något blev fel! " + e);
        }
        return null;
    }


    // skapa en hash av lösenordet
    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes());

            StringBuilder builder = new StringBuilder();
            for(byte hashedByte : hashedBytes) {
                builder.append(String.format("%02x", hashedByte));
            }
            String hashedPassword = builder.toString();
            System.out.println("Hashed Password: " + hashedPassword); // Skriv ut det hashade lösenordet
            return hashedPassword;
        } catch (NoSuchAlgorithmException e){
            System.out.println(e);
            return null;
        }
    }
}
