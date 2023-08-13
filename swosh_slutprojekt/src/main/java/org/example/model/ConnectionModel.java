package org.example.model;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public abstract class ConnectionModel {
    private static Connection conn;
    private static Properties properties;
    private static MysqlDataSource dataSource;

//Läsa in databas från extern fil
    static {
        properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream("C:\\Users\\frida\\OneDrive\\Skrivbord\\databas\\swosh_slutprojekt\\database.properties.txt")) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    static String url = properties.getProperty("url");
    static int port = Integer.parseInt(properties.getProperty("port"));
    static String database = properties.getProperty("database");
    static String username = properties.getProperty("username");
    static String password = properties.getProperty("password");

//    static String url = "localhost";
//    static int port = 3306;
//    static String database = "swosh_slutprojekt";
//    static String username = "root";
//    static String password = "Kaffekanna11!";

    protected ConnectionModel() {

    }

    public static void InitializeDatabase() {
        try {
            System.out.print("Configuring data source...");
            dataSource = new MysqlDataSource();
            dataSource.setUser(username);
            dataSource.setPassword(password);
            dataSource.setUrl("jdbc:mysql://" + url + ":" + port + "/" + database + "?serverTimezone=UTC");
            dataSource.setUseSSL(false);
            System.out.print("done!\n");
        } catch (SQLException e) {
            System.out.print("failed!\n");
            System.exit(0);
        }
    }

    public static Connection getConnection() {
        if (dataSource == null) {
            InitializeDatabase();
        }
        try {
            Connection conn = dataSource.getConnection();
            return conn;
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    protected MysqlDataSource getDataSource() {
        return dataSource;
    }
}