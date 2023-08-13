package org.example.model;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import static org.example.model.ConnectionModel.getConnection;

public class Users {
    private int id;
    private String first_name;
    private String last_name;
    private long social_security_number;
    private String email;
    private long phone_number;
    private String password;
    private Date created;

    protected MysqlDataSource dataSource;
    public Users(String first_name, String last_name, long social_security_number, String email, long phone_number, String password) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.social_security_number = social_security_number;
        this.email = email;
        this.phone_number = phone_number;
        this.password = password;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }
    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }
    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public long getSocial_security_number() {
        return social_security_number;
    }
    public void setSocial_security_number(long social_security_number) {
        this.social_security_number = social_security_number;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public long getPhone_number() {
        return phone_number;
    }
    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreated() {
        return created;
    }
    public void setCreated(Date created) {
        this.created = created;
    }


    //hämta användarens user id från databasen
    public static int getUserIdBySocialSecurityNumber(String socialSecurityNumber) {
        int userId = -1;

        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT id FROM users WHERE social_security_number = ?")) {
            statement.setString(1, socialSecurityNumber);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    userId = result.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.out.println("Fel vid hämtning av user_id: " + e.getMessage());
        }
        return userId;
    }


}
