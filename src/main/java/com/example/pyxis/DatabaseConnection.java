package com.example.pyxis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection extends ConfigsDataBase {  // Fixed class name
    private Connection dbConnection;  // Change type to java.sql.Connection

    public Connection getDbConnection() throws ClassNotFoundException, SQLException {
        String dbUrl = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
        Class.forName("com.mysql.cj.jdbc.Driver"); // Ensures MySQL driver is loaded

        dbConnection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
        return dbConnection;
    }
}
