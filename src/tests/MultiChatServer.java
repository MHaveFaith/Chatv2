/**
 * Created by Keno on 11/26/2016.
 */
package tests;

import java.io.*;
import java.net.*;
import java.util.*;

public class MultiChatServer {
    ArrayList<ServerHandler> clients = new ArrayList<ServerHandler>();
    private static ServerSocket serverSocket = null;
    private static Socket clientSocket = null;
    final static int DEFAULT_PORT = 1337;

    public static void main(String[] args) {
        new MultiChatServer();
    }

    public MultiChatServer() {
        try {
            serverSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("Waiting for connection...");
            while (true) {
                clientSocket = serverSocket.accept();
                System.out.println("Connected!");

                // Creating object sending the socket and a reference of this object (class).
                ServerHandler sh = new ServerHandler(clientSocket, this);
                sh.start(); // Starting the thread.

                clients.add(sh);  // Adding clients to the arraylist.
            }

        } catch (IOException ie) {
            ie.printStackTrace();
            closeConnection();
        }
        closeConnection();
    }

    /*
     * Lazy so writing this once ..
     */
    public void closeConnection() {
        try {
            clientSocket.close();

        } catch (IOException iii) {
            iii.printStackTrace();
        }
    }

}
