package server; /**
 * Created by Keno on 11/26/2016.
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    ArrayList<ServerThread> client_list = new ArrayList<ServerThread>();
    private static ServerSocket serverSocket = null;
    private static Socket clientSocket = null;
    DBManager dbManager;

    public static void main(String[] args) {
        new Server();
    }

    /***
     * Start the thread in this Constructor and pass in a Socket, a reference to this class
     * and a list of all active connections so far.
     */
    private int getPort() {
        int DEFAULT_PORT = 0;
        try {
            Properties prop = new Properties();
            FileInputStream in = new FileInputStream("properties.prop");
            prop.load(in);

            DEFAULT_PORT = Integer.parseInt(prop.getProperty("default_port")); //Port to Connect

        } catch (IOException e) {
            e.printStackTrace();
        }
        return DEFAULT_PORT;

    }



    public Server() {
        try {
            dbManager = new DBManager();
            serverSocket = new ServerSocket(getPort());
            System.out.println("Waiting for connection...");
            while (true) {
                clientSocket = serverSocket.accept();
                System.out.println("Connected!");

                ServerThread server_thread = new ServerThread(clientSocket, this, dbManager, client_list);
                server_thread.start(); // Starting the thread.
            }

        } catch (IOException ie) {
            ie.printStackTrace();
            closeConnection();
        }
        closeConnection();
    }

    public void closeConnection() {
        try {
            clientSocket.close();
        } catch (IOException iii) {
            iii.printStackTrace();
        }
    }
}