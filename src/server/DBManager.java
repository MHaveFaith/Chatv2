package server;

import java.sql.*;
import java.util.Properties;
import java.io.*;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;

/**
 * Created by Keno on 12/12/2016.
 */

public class DBManager{

    private Connection con;

    /***
     * Constructor
     * This establishes a connection with the H2 Database
     */

    DBManager(JTextArea events) {
        JTextArea events1 = events;
        try {

            Class.forName("org.h2.Driver");

            try {
                Properties prop = new Properties();
                FileInputStream in = new FileInputStream("properties.prop");
                prop.load(in);

                String db_host = prop.getProperty("db_host");
                String db_name = prop.getProperty("db_name");
                String db_pass = prop.getProperty("db_pass");
                con = DriverManager.getConnection(db_host, db_name, db_pass);

                events.append("\nDB Booted Successfully.");


            } catch (FileNotFoundException e) {
                events.append("\nDB File Not found Exception.");
            } catch (SQLException se) {
                se.printStackTrace(); // FIXME: 12/12/2016
                events.append("\nUnable to Connect to Database.");
                displaySQLErrors(se);
                System.exit(1);
            } catch (IOException ie) {
                events.append("\nDB IO Exception Occurred");
            }
        } catch (Exception e) {
            events.append("\nUnable to Load Database Driver.");
            System.exit(1);
        }
    }


    /**
     * This method checks if the username and password match.
     * @param username
     * @param password
     * @return if it matches then return true.
     */
    boolean authenticateLogin(String username, String password) {
        boolean accepted = false;
        try {
            PreparedStatement ps = null;
            String query;

            ps = con.prepareStatement("SELECT * FROM Users WHERE UserName='" + username + "';");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String readPass = rs.getString("Password");
                //Simplify versiion instead of IF.
                accepted = BCrypt.checkpw(password, readPass);
            } else {
                return accepted;
            }
        } catch (SQLException e) {
            displaySQLErrors(e);
        }
        return accepted;
    }


    /***
     * This method attempts to register an account. But first it checks to see if the account exists
     * This method also uses BCrypt for encryption.
     * @param username
     * @param password
     * @return true if the account was created.
     */
    boolean registerAccount(String username, String password) {
        String hashed_pw = BCrypt.hashpw(password, BCrypt.gensalt(10));
        try {
            if (userExists(username)) {
                return false;
            } else {
                PreparedStatement ps;
                String query = "INSERT INTO Users VALUES('" + username + "','" + hashed_pw + "');";
                ps = con.prepareStatement(query);
                int rs = ps.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            displaySQLErrors(e);
            return false;
        }
    }

    /***
     * Create the table if it doesn't exit on system.
     */
    private void createTable() {
        try {
            PreparedStatement ps;
            String query = "CREATE TABLE IF NOT EXISTS Users"
                    + "  (username     VARCHAR(10) PRIMARY KEY,"
                    + "   password     VARCHAR,"
                    + "   rank         INTEGER(2),"
                    + "   ban          BOOLEAN,"
                    + "   reg_date     DATE)";

            ps = con.prepareStatement(query);
            int rs = ps.executeUpdate();
        } catch (SQLException e) {
            displaySQLErrors(e);
        }
    }

    /**
     * Used for deleting a users account.
     * @param username
     */
    public void deleteAccount(String username) {
        try {
            PreparedStatement ps;
            String query = "DELETE FROM Users"
                    + " WHERE username='" + username + "'";
            ps = con.prepareStatement(query);
            int rs = ps.executeUpdate();
        } catch (SQLException e) {
            displaySQLErrors(e);
        }

    }


    // FIXME: 1/1/2017  use loop to iterate through result set, get the column names then send message back
    public String getAccountInfo(String username) {
        String account_info = null;

        return account_info;
    }

    /**
     * Simply checks if the username exists since the username is the PRIMARY KEY
     * @param username
     * @return true if it exists and false if it doesn't.
     */
    private boolean userExists(String username) {
        boolean exists = false;
        try {
            PreparedStatement ps = null;
            String query = "SELECT * FROM Users WHERE Username='" + username + "';";
            ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                exists = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            displaySQLErrors(e);
        }
        return exists;
    }


    /**
     * Use this to save code writing, call when SQL Exception is triggered.
     */
    private void displaySQLErrors(SQLException e)
    {
        System.out.println("SQLException: " + e.getMessage() + "\n");
        System.out.println("SQLState: " + e.getSQLState() + "\n");
        System.out.println("VendorError: " + e.getErrorCode() + "\n");
    }

}