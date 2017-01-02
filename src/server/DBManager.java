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

                Properties prop = new Properties();
                FileInputStream in = new FileInputStream("properties.prop");
                prop.load(in);

                String db_host = prop.getProperty("db_host");
                String db_name = prop.getProperty("db_name");
                String db_pass = prop.getProperty("db_pass");
                con = DriverManager.getConnection(db_host, db_name, db_pass);

                events.append("\nDB Booted Successfully.");
                createTable(); // Create the table if it's not already on the system.

            } catch (FileNotFoundException e) {
                events.append("\nUnable to load configuration file.");
            } catch (SQLException se) {
                events.append("\nError connecting to your database.");
                // FIXME: 1/2/2017  TURN THE START BUTTON TO DISABLED
                //displaySQLErrors(se);
            } catch (IOException ie) {
                events.append("\nError reading configuration file");
            } catch (ClassNotFoundException ce) {
                events.append("Unable to Load Database Driver.");
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

                //Simplify version instead of IF.
                accepted = BCrypt.checkpw(password, readPass);
            } else {
                return accepted;
            }
        } catch (SQLException e) {
            displaySQLErrors(e);
            accepted = false;
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
                String query = "INSERT INTO Users (username, password, rank, ban, reg_date) " +
                        "VALUES('" + username + "','" + hashed_pw + "',1, FALSE, NOW());";
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
                    + "   password     VARCHAR(60),"
                    + "   rank         INTEGER(2),"
                    + "   ban          BOOLEAN,"
                    + "   reg_date     DATE)";

            ps = con.prepareStatement(query);
            int rs = ps.executeUpdate();

            // FIXME: 1/2/2017  Message user if table HAD to be created.
        } catch (SQLException e) {
            //events.append("\nError creating the database table."); fixme
            e.printStackTrace();
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
    public String accountInfo(String username ) throws SQLException {
        String info = null;
//        String query = ("SELECT  * FROM Users WHERE Username='" + username + "';");
//        ResultSet resultSet;
//        Statement st;

        return info;
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