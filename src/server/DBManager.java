package server;

import java.sql.*;
import java.util.Properties;
import java.io.*;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Created by Keno on 12/12/2016.
 */

public class DBManager {

    private String db_host, db_name, db_pass;
    private Connection con;

    /***
     * Constructor
     * This establishes a connection with the H2 Database
     */
    public DBManager() {
        try {
            Class.forName("org.h2.Driver");

            try {
                Properties prop = new Properties();
                FileInputStream in = new FileInputStream("properties.prop");
                prop.load(in);

                db_host = prop.getProperty("db_host"); //DB_Host
                db_name = prop.getProperty("db_name"); //DB_Name
                db_pass = prop.getProperty("db_pass"); //DB_Pass
                con = DriverManager.getConnection(db_host, db_name, db_pass);

                System.out.println("DB Booted Succesfully.");

            } catch (FileNotFoundException e) {
                e.printStackTrace(); // FIXME: 12/12/2016

            } catch (SQLException se) {
                se.printStackTrace(); // FIXME: 12/12/2016
                System.out.println("Unable to connect to database");
                displaySQLErrors(se);
                System.exit(1);
            } catch (IOException ie) {
                ie.printStackTrace(); // FIXME: 12/12/2016
            }
        } catch (Exception e) {
            System.out.print("Unable to load h2 Driver");
            System.exit(1);
        }
    }


    /**
     * This method checks if the username and password match.
     * @param username
     * @param password
     * @return if it matches then return true.
     */
    public boolean authenticateLogin(String username, String password) {
        boolean accepted = false;
        try {
            PreparedStatement ps = null;
            String query;

            ps = con.prepareStatement("SELECT * FROM Users WHERE UserName='" + username + "';");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String readPass = rs.getString("Password");
                if (BCrypt.checkpw(password, readPass)) {
                    accepted = true;
                } else {
                    accepted = false;
                }
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
    public boolean registerAccount(String username, String password) {
        String hashed_pw = BCrypt.hashpw(password, BCrypt.gensalt(10));
        try {
            if (userExists(username)) {
                return false;
            } else {
                PreparedStatement ps = null;
                String query = "INSERT INTO Users VALUES('" + username + "','" + hashed_pw + "');";
                ps = con.prepareStatement(query);
                int rs = ps.executeUpdate();
                System.out.println("Account created.");

                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            displaySQLErrors(e);
            return false;
        }
    }


    /**
     * Simply checks if the username exists since the username is the PRIMARY KEY
     * @param username
     * @return true if it exists and false if it doesn't.
     */
    public boolean userExists(String username) {
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
    private void displaySQLErrors(SQLException e) {
        System.out.println("SQLException: " + e.getMessage() + "\n");
        System.out.println("SQLState: " + e.getSQLState() + "\n");
        System.out.println("VendorError: " + e.getErrorCode() + "\n");
    }


}