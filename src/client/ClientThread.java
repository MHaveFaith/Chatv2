package client;

import javax.swing.*;

public class ClientThread extends Thread {

    private Client client;
    private JList userList;
    private JTextPane chatBox;

    ClientThread(Client client, JList userList, JTextPane chatBox) {
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
}