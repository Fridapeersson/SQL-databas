package org.example.controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.example.model.ConnectionModel.getConnection;

public class LoginManager {
    public static Boolean login(String social_security_number, String password) {
        try(Connection conn = getConnection()) {
            String loginQuery = "SELECT * FROM users WHERE social_security_number=?";

            PreparedStatement prepStatement = conn.prepareStatement(loginQuery);

            prepStatement.setLong(1, Long.parseLong(social_security_number));

            ResultSet result = prepStatement.executeQuery();

            String hashedPassword = null;
            if (result.next()) {
                // Hämta det hashade lösenordet från databasen
                hashedPassword = result.getString("password");
            }
            // Skapa en hash av det angivna lösenordet
            String hashedInputPassword = hashPassword(password);

            if (hashedPassword != null && hashedPassword.equals(hashedInputPassword)) {
                return true;
            }
        } catch(SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hashedBytes = digest.digest(password.getBytes());

            StringBuilder builder = new StringBuilder();
            for(byte hashedByte : hashedBytes) {
                builder.append(String.format("%02x", hashedByte));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e){
            System.out.println(e);
            return null;
        }
    }
}
