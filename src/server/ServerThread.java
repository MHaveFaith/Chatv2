package server;

/**
 * UEL Chat Project by Ade, Godfrey,
 * Muhammad & Keno.
 */

import javax.swing.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class ServerThread extends Thread {

    private Socket connectSocket;
    private Server server;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private String username;
    private DBManager dbManager;
    private JTextArea chatMessages;
    private JTextArea events;
    private ArrayList<ServerThread> client_list;

    public ServerThread(Socket connectSocket, Server server, DBManager dbManager, ArrayList client_list, JTextArea chatMessages, JTextArea events) {
        this.connectSocket = connectSocket;
        this.server = server;
        this.dbManager = dbManager;
        this.client_list = client_list;
        this.chatMessages = chatMessages;
        this.events = events;
    }

    public String getUsername() {
        return username;
    }


    private void setUsername(String username) {
        this.username = username;
    }


    /**
     * Read from the client, if the client is just logging in then it's stuck in a loop
     * until the boolean 'accepted' is changed to true. Once it's true then it reaches
     * another loop where it's constantly reading messages.
     */
    public void run() {
        try {
            out = new PrintWriter(connectSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(connectSocket.getInputStream()));

            boolean accepted = false;
            String message = null;
            /** The messages, LOGIN:, REGISTER:, EXIT: */
            while (!accepted) {
                message = in.readLine();
                if (message.startsWith("LOGIN:")) {
                    accepted = authenticateUser(message);
                } else if (message.startsWith("REGISTER:")) {
                    registerAccount(message);
                } else if (message.startsWith("EXIT:")) { // FIXME: 12/27/2016 - for when login doesnt take place
//                    System.out.println("it reaches..."); 
//                    in.close();
//                    out.close();
//                    connectSocket.close();
//                    server.closeConnection();
                }
            }

            while (true) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ee) {
                    ee.printStackTrace();
                }
                String line = null;
                try {
                    line = in.readLine();
                } catch (SocketException se) {

                }
                if (line == null) {
                    break; }
                if (line.startsWith(username + ": :WHOSIN")) {
                    whosOnline();
                } else if (line.startsWith(username + ": :PM")) {
                    privateMessage(line);
                }
                else {
                    sendToAllClients(line);// Sends message to everyone connected.
                    chatMessages.append("\n" + line); //Send message to server message box.
                }
            }

            server.closeConnection();
            exitUser();

        } catch (IOException e) {
            e.printStackTrace();
            closeConnection();
        }
        closeConnection();
    }

    /**
     * This method sends message to every active connection
     *
     * @param - a message which the user has sent.
     */
    public synchronized void sendToAllClients(String text) {
        for (int index = 0; index < server.client_list.size(); index++) {
            ServerThread sh = server.client_list.get(index);
            sh.sendToClient(text);
        }
    }


    /**
     * Used with sendToAllClients
     */
    public void sendToClient(String text) {
        out.println(text);
        out.flush();
    }


    /**
     * Command for private messaging
     * @param message looks like USERNAME: :PM USERNAME SOME_MESSAGE_TO_SEND_HERE
     */
    private synchronized void privateMessage(String message) {
        String split_message[] = message.split(" ", 4);

        String username_split = split_message[2];
        String msg = split_message[3];

        for(int index = 0; index < client_list.size(); index++ ) {
            if(client_list.get(index).getUsername().equals(username_split)) {
                ServerThread sh = server.client_list.get(index);
                sh.sendToClient("PM from: " + getUsername() + ": " + msg);
               if(client_list.get(index).getUsername().equals(username)) {
                    // Sad enough to msg yourself ?
               } else {
                sendToClient("PM to: " + username_split + " " + msg); }
            }
        }
    }


    /**
     * Cheeky command for showing all that's connected.
     */
    private synchronized void whosOnline() {  // FIXME: 12/24/2016
        String clients_connected = "Online users: ";
        for (int index = 0; index < client_list.size(); index++) {
            if (client_list.size() == 1) {
                clients_connected += client_list.get(index).getUsername();
            } else {
                clients_connected += client_list.get(index).getUsername() + " ";
            }
        }
        out.println(clients_connected);
        out.flush();
    }


    /***
     * Alerts the user when someone has exited the chat room.
     * This method also removes this person from the list.
     */
    private synchronized void exitUser() {
        String exit_message = getUsername() + " has left the chat.";
        client_list.remove(this);
        sendToAllClients(exit_message);
        events.append("\n" + exit_message);
        // Send list with updated userlist to all clients.
        sendUserList();

        closeConnection();
    }


    /***
     * Method is for checking is an account can be registered or not.
     * @param message - takes a message which resembles - LOGIN:USERNAME:PASSWORD then it's split.
     * @return returns true if the account was made or false if it wasn't.
     *         If the account was added then it gets added to an ArrayList for later.
     */
    private synchronized boolean authenticateUser(String message) {
        boolean accepted = false;
        // LOGIN:USERNAME:PASSWORD
        String split_message[] = message.split(":");
        String username = split_message[1]; // USERNAME
        String password = split_message[2]; // PASSWORD

        if (dbManager.authenticateLogin(username, password)) {
            setUsername(username);
            out.println("ACCEPTED:");
            out.flush();
            accepted = true;
            client_list.add(this);
            sendUserList();
            sendToAllClients(username + " has connected");
            events.append("\n" +username+ " has connected to chat."); //Writes this to server.
        } else {
            out.println("REJECTED:");
            out.flush();
        }
        // FIXME: 12/23/2016  DB CONNECTION
        return accepted;
    }

    private synchronized void sendUserList() {
        String userList = "USERLIST:";
        for (int i = 0; i < client_list.size(); i++) {
            userList += ":" + client_list.get(i).getUsername();
        }
        sendToAllClients(userList);
    }

    /***
     * RegisterGUI an account.
     * This method uses the DBManager's registerAccount boolean method which
     * returns true if the account is created or false if an error occured...(i.e user already exists)
     * @param - Message resembles REGISTER:USERNAME:PASSWORD
     */
    private synchronized void registerAccount(String message) {
        boolean accepted = false;
        String split_message[] = message.split(":");
        String username = split_message[1];
        String password = split_message[2];

        if (dbManager.registerAccount(username, password)) {
            out.println("CREATED:");
            out.flush();
            events.append("\n"+ username + " just registered"); // FIXME: 12/27/2016  write to server GUI
        } else {
            out.println("REJECTED:");
            out.flush();
        }
    }

    /*
     *  Method to close the connections once finished with them
     *  Call this method when an exception is thrown.
     */
    private void closeConnection() {
        try {
            in.close();
            out.close();
            connectSocket.close();

        } catch (IOException ie) {
            ie.printStackTrace();
        }
    }
}



