package com.polling.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection 
{
    private static final String URL = "jdbc:mysql://localhost:3306/polling_system";
    private static final String USER = "root"; // Your MySQL username
    private static final String PASSWORD = "Jalandhar@2002"; // Your MySQL password

    public static Connection getConnection() 
    {
        Connection connection = null;
        try 
        {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } 
        catch (SQLException e) 
        {
            e.printStackTrace();
        }
        return connection;
    }
}