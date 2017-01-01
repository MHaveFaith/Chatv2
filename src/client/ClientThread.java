package client;

import server.Server;

import javax.swing.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClientThread extends Thread {

    private PrintWriter out = null;
    private BufferedReader in = null;
    private Client client;
    private JList userList;
    private JTextPane chatBox;
    private ClientGUI2 clientGUI;
    private LoginGUI loginGUI = new LoginGUI(clientGUI, true, client);

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

                String response = null;

                if ((response = client.readServerResponse()) != null) {
                    if (response.startsWith("USERLIST::")) {
                        String[] usernames = response.substring(response.indexOf(':')).split(":");
                        userList.setListData(usernames);
                    }
                    else{

                        if (response.startsWith(client.getUsername() + " has connected")) {
                            chatBox.setText(chatBox.getText() + response + "\n");
                        }

                        else {
                            chatBox.setText(chatBox.getText() + response.replaceFirst(client.getUsername(), ">") + "\n");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String profCheck(String input, ArrayList<String> al) {

        for (String s : al) {
            if (input.toLowerCase().contains(s)) {
                input = input.replaceAll(s, "****");
            }
        }
        return input;
    }

    //Reads a file full of Profanity.
    private static List<String> profanityList(){
        List<String> records = new ArrayList<String>();
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader("badWords.txt"));
            String line;
            while ((line = reader.readLine()) != null)
            {
                records.add(line);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

}