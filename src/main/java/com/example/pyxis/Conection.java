package com.example.pyxis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conection extends ConfigsDataBase {

    Connection dbConnection ;

    public  Connection getDbConnection()
            throws ClassNotFoundException, SQLException {
        String dbUrl = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName;
        Class.forName("com.mysql.cj.jdbc.Driver");

        dbConnection = DriverManager.getConnection(dbUrl, dbUser, dbPass);
        return dbConnection;

    }

}
