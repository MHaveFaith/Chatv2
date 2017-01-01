package client;

import server.Server;

import javax.swing.*;
import java.net.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientThread extends Thread {

    private PrintWriter out = null;
    private BufferedReader in = null;
    private Client client;
    private JList userList;
    private JTextPane chatBox;
    private String oldUsername;
    private ClientGUI clientGUI;
    private LoginGUI loginGUI = new LoginGUI(clientGUI, true, client);

    public ClientThread(Client client, JList userList, JTextPane chatBox) {
        this.client = client;
        this.userList = userList;
        this.chatBox = chatBox;
        oldUsername = loginGUI.getUsername();
    }

    /**
     * Constantly read from the server
     */
    public void run() {
        try {
            while (true) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ee) {
                    ee.printStackTrace();
                }

                String response;
                if ((response = client.readServerResponse()) != null) {
                    if (response.startsWith("USERLIST::")) {
                        String[] usernames = response.substring(response.indexOf(':')).split(":");
                        userList.setListData(usernames);
                    }
                    else {
                        if (response.startsWith(client.getUsername() + " has connected")) {
                            chatBox.setText(chatBox.getText() + response + "\n");
                        }
                        else {
                            chatBox.setText(chatBox.getText() + response.replaceFirst(client.getUsername(), ">ME") + "\n");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}