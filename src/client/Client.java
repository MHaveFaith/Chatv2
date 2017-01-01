package client;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.Properties;


/***
 * This class contains all the logic for the Client.
 */

public class Client extends  Thread {

    private Socket socket = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private String username;
    private JList userList;
    private JTextPane chatBox;
    private int counter = 0;

    /**
     * Constructor which tries to initate the connection
     * to the server.
     */

    int getConnectionCount(){
        return counter;
    }

    void Connect() {
        do {
                try {
                    socket = new Socket(getHost(), getPort());
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream(), true);
                    if (socket.isConnected())break;
                } catch (IOException e){
                    counter++;
//                    e.printStackTrace();
                }
            } while (!(counter == 3));
    }

    private String getHost(){
        String DEFAULT_HOST = null;
        try {
            Properties prop = new Properties();
            FileInputStream in = new FileInputStream("properties.prop");
            prop.load(in);

            DEFAULT_HOST = prop.getProperty("default_host");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return DEFAULT_HOST;
    }

    private int getPort(){
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

    String getUsername(){
        return username;
    }

    /***
     * Attempt to logintoAccount, method is used in LoginGUI class
     * Sends message 'LOGIN:USERNAME:PASSSWORD' to the server
     * @param username
     * @param password
     * @return true if logged in was successful.
     */
    boolean logintoAccount(String username, String password) {
        boolean login_attempt = false;
        out.println("LOGIN:" + username + ":" + password);
        out.flush();
        String response;

        try {
            response = in.readLine();
            if (response.equals("ACCEPTED:")) {
                login_attempt = true;
                this.username = username;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return login_attempt;
    }

    /***
     * RegisterGUI account which is called in the 'RegisterGUI' class.
     * Sends message 'REGISTER:USERNAME:PASSWORD' to the server
     * @param username
     * @param password
     * @return return true if the account was made
     */
    boolean registerAccount(String username, String password) {
        boolean was_created = false;
        out.println("REGISTER:" + username + ":" + password);
        out.flush();
        String response;
        try {
            response = in.readLine();
            if(response.equals("CREATED:")) {
               was_created = true;
                // FIXME: 12/24/2016  some more logic here
            }
        } catch(IOException ie) {
            ie.printStackTrace();
        }
        return was_created;
    }

    /**
     * Send a message to the server...
     */
    private void sendToServer(String message) {
        out.println(message);
        out.flush();
    }

    /***
     * Method calls the sendToServer method above and sends the username along with the message.
     * @param message
     */
    void sendToChatBox(String message) {
       sendToServer(username + ": " + message);
    }

    /***
     * Read the servers response
     */
    String readServerResponse() {
        String line = null;
        try {
            line = in.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return line;
    }

    void quitMessage() {
       out.println("EXIT:");
       out.flush();
    }

    public void closeConnection() {
      try {
          socket.close();
          out.close();
          in.close();
      } catch (IOException ie) {
          ie.printStackTrace();
      }
    }
}
