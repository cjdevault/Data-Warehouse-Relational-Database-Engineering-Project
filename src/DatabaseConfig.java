/*
 * Author: Christopher De Vault
 * Teacher & TAs: Prof. Lester I. McCann, James Shen, Utkarsh Upadhyay
 * Assignment: Prog4
 * Course: CSc 460
 * Due: December 8, 2025
 *
 * Purpose: This file contains the configuration for the database including 
 * connecting to the database and executing queries on the database. 
 * It provides connection management for the Pet Cafe Management System.
 * 
 * Usage:
 *   javac DatabaseConfig.java
 *   java DatabaseConfig
 *
 * Java version: 16 
 */
import java.sql.*;

public class DatabaseConfig {
    // Oracle URL for the database connection
    private static final String ORACLE_URL = "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";
    
    // PLACEHOLDER!!! UPDATE THIS WHEN SCHEMA IS PUSHED
    // Oracle username prefix for schema 
    public static final String SCHEMA_PREFIX = "shaydenlowry";

    /*
     * Purpose: This method returns a connection to the database.
     * Parameters: username - Oracle database username
     *             password - Oracle database password
     * Returns: The connection to the database.
     * Throws: SQLException if connection cannot be established
     */
    public static Connection getConnection(String username, String password) throws SQLException {
        Connection dbconn = null;
        // Load driver (JDBC.java 64-75)
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("ClassNotFoundException:  "
                + "Error: Loading Oracle JDBC driver. \n"
                + "Perhaps the driver is not on the Classpath?");
            System.exit(-1);
        }
        // Create connection using the username and password
        try {
            dbconn = DriverManager.getConnection(ORACLE_URL, username, password);
        } catch (SQLException e) {
            System.err.println("SQLException: Could not open JDBC connection.");
            System.err.println("Message:   " + e.getMessage());
            System.err.println("SQLState:  " + e.getSQLState());
            System.err.println("ErrorCode: " + e.getErrorCode());
            throw e; // throw the exception to the caller
        }
        return dbconn; // return the connection 
    }
}
