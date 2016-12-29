/**
 * Created by Keno on 11/26/2016.
 */
package tests;

import java.io.*;
import java.net.*;

public class TestClient extends Thread {

    private static final int DEFAULT_PORT = 1337;
    private static final String DEFAULT_HOST = "localhost";
    ClientHandler ch;
    BufferedReader stdIn;
    Socket clientSocket;

    public static void main(String[] args) {
        new TestClient();
    }

    public TestClient() {
        try {
            clientSocket = new Socket(DEFAULT_HOST, DEFAULT_PORT);

            // Creating object sending the socket and a reference of this object (class).
            ch = new ClientHandler(clientSocket);
            ch.start(); // Starting the thread.

            /*
             * Here I'm reading input from the user (messages) to pass to the server so that
             * it can relay to all clients connected.
             */
            while (true) {
                try {
                    stdIn = new BufferedReader(new InputStreamReader(System.in));

                    String input = stdIn.readLine();
                    ch.sendToServer(input); // < Send to server

                } catch (IOException iie) {
                    stdIn.close();
                    iie.printStackTrace();
                }
            }

        } catch (IOException ie) {
            ie.printStackTrace();
            closeConnection(); // Close connection...
        }

    }

    /*
     * Lazy so writing this once ..
     */
    public void closeConnection() {
        try {
            stdIn.close();
            clientSocket.close();
        } catch (IOException iii) {
            iii.printStackTrace();
        }
    }
}
