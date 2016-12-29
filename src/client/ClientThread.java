package client;

import javax.swing.*;
import java.net.*;
import java.io.*;

public class ClientThread extends Thread {

    private PrintWriter out = null;
    private BufferedReader in = null;
    private Client client;
    private JList userList;
    private JTextPane chatBox;

    public ClientThread(Client client, JList userList, JTextPane chatBox) {
        this.client = client;
        this.userList = userList;
        this.chatBox = chatBox;

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
                if((response = client.readServerResponse()) != null) {
                    if (response.startsWith("USERLIST::")) {
                        String[] usernames = response.substring(response.indexOf(':')).split(":");
                        userList.setListData(usernames);
                    } else {
                        chatBox.setText(chatBox.getText() + response + "\n");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}