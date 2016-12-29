package tests;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by Keno on 12/23/2016.
 */
public class testing {
    Connection con;
    String db_host, db_name, db_pass;
   static ArrayList<String> testingx = new ArrayList<String>();


    public static void main(String[] args) throws SQLException {
        testing t = new testing();
        t.init();

        testingx.add("yes");
        testingx.add("no");
        String testing = "USERSON:";
        for (int index = 0; index <testingx.size(); index++) {
            testing += testingx.get(index) + ":";
        }
        String[] lmfao = testing.split(":");
        System.out.println(lmfao[1]);

        System.out.println( t.registerAccount("Hankz", "123456"));
    }

    public boolean registerAccount(String username, String password) throws SQLException {
        if(ifUserExists(username)) {
            return false;
        }
        else {
            try {
                PreparedStatement ps = null;
                Connection con = DriverManager.getConnection(db_host, db_name, db_pass);
                String query = "INSERT INTO Users VALUES('" + username + "','" + password + "');";
                ps = con.prepareStatement(query);
               int rs = ps.executeUpdate();
                System.out.println("Account created.");
            } catch (SQLException se) {


            }
            return true;
        }
    }

    private boolean ifUserExists(String username) throws SQLException{

       boolean exists = false;
        PreparedStatement ps = null;
        Connection con = DriverManager.getConnection(db_host, db_name, db_pass);
        String query = "SELECT * FROM Users WHERE Username='" + username + "';";
        ps = con.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        if(rs.next()) {
            exists = true;
        }


       return exists;
    }


    private void init() {
        try {
            Class.forName("org.h2.Driver");

            try {
                Properties prop = new Properties();
                FileInputStream in = new FileInputStream("properties.prop");
                prop.load(in);

                db_host = prop.getProperty("db_host"); //DB_Host
                db_name = prop.getProperty("db_name"); //DB_Name
                db_pass = prop.getProperty("db_pass"); //DB_Pass

                Connection con = DriverManager.getConnection(db_host, db_name, db_pass);
                System.out.println("DB Booted");

            } catch (FileNotFoundException e) {
                e.printStackTrace(); // FIXME: 12/12/2016

            } catch (SQLException se) {
                se.printStackTrace(); // FIXME: 12/12/2016
                System.out.println("Unable to connect to database");
                System.exit(1);

            } catch (IOException ie) {
                ie.printStackTrace(); // FIXME: 12/12/2016
            }
        } catch (Exception e) {
            System.out.print("Unable to load h2 Driver");
            System.exit(1);
        }

    }


}


