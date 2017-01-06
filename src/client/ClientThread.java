package client;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

                String response = null;

                if ((response = client.readServerResponse()) != null) {

                    if (response.startsWith("USERLIST:")) {
                        String[] usernames = response.substring(response.indexOf(':')).split(":");
                        userList.setListData(usernames);
                    }else if (response.startsWith(":DATE")){
                        chatBox.setText(chatBox.getText() + " \nCurrent Time: " + getLocalTime() + "\n");
                    }
                    else {
                        chatBox.setText(chatBox.getText() + response + "\n");
                    }
                } else {
                    client.closeConnection();
                    JOptionPane.showMessageDialog(null, "Server disconnected...closing the program",
                            "Connection Error!", JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getLocalTime(){
        String time = null;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        time = "[" + dtf.format(now) +"]";

        return time;
    }
}