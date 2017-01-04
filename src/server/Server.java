package server; /**
 * Created by Keno on 11/26/2016.
 */

import com.sun.xml.internal.bind.v2.model.annotation.RuntimeAnnotationReader;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Server implements Runnable {

    ArrayList<ServerThread> client_list = new ArrayList<>();
    private static ServerSocket serverSocket = null;
    private static Socket clientSocket = null;
    private DBManager dbManager;
    private Thread thread;
    private JTextArea events; //Updates events area
    private JTextArea chatMessages; //Updates chat area

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


    /*
    @JTextArea events, allows the class to be print text to the GUI class textArea
     */

    public Server(JTextArea events, JTextArea chatMessages) {
        this.events = events;
        this.chatMessages = chatMessages;
    }

    public void closeConnection() {
        try {
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            dbManager = new DBManager(events);
            serverSocket = new ServerSocket(getPort());
            events.append("\nServer Up.");
            events.append("\nListening on Port: " + getPort());

            while (true) {
                clientSocket = serverSocket.accept();
                ServerThread server_thread = new ServerThread(clientSocket, this, dbManager, client_list, chatMessages, events);
                server_thread.start(); // Starting the thread.
            }
        } catch (IOException ie) {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        closeConnection();
    }

    //Call to start class
    void start(){
        thread = new Thread(this);
        thread.start();
    }

    @SuppressWarnings("deprecation")
    void stop(){
        closeConnection();
        try {
            serverSocket.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (thread != null) thread.getThreadGroup().stop();
        thread = null;
    }
}