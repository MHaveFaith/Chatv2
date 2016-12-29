package tests;

/**
 * Created by Keno on 11/21/2016.
 * Handler classs for the MultiEx2 Server... allows for multiple users to connect
 * at once.
 */

import java.net.*;
import java.util.*;
import java.io.*;


public class ServerHandler extends Thread {

    Socket connectSocket;
    MultiChatServer server;
    PrintWriter out = null;
    BufferedReader in = null;
    String username;

    public ServerHandler(Socket socket, MultiChatServer server) {
        connectSocket = socket;
        this.server = server;
    }

    /**
     * Going through the list and messaging every active connection...
     */
    public synchronized void sendToAllClients(String text) {
        for (int index = 0; index < server.clients.size(); index++) {
            ServerHandler sh = server.clients.get(index);
            sh.sendToClient(username.trim() + ": " + text);
        }
    }

    public void sendToClient(String text) {
        out.println(text);
    }

    public void run() {
        try {
            out = new PrintWriter(connectSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(connectSocket.getInputStream()));

            out.println("Enter your username: ");
            username = in.readLine();

            String line;
            synchronized(this) {
                while (true) {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ee) {
                        ee.printStackTrace();
                    }
                    line = in.readLine();

                    if (line == null) {
                        break;
                    }

                    System.out.println("Message to server from " + username + ": " + line); // Not needed but sends to the server..

                    sendToAllClients(line); // < Jumps to the method and sends this string.
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            closeConnection();
        }
        closeConnection();
    }

    /*
     * Lazy so writing this once ..
     */
    public void closeConnection() {
        try {
            in.close();
            out.close();
            connectSocket.close();

        } catch (IOException iii) {
            iii.printStackTrace();
        }
    }
}

