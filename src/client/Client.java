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
    private int counter = 0;
    private String account_details; // <- Return account details.
    boolean alreadyLogged; //<<= Use to check if User already logged in.

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
        fileExist();
        String DEFAULT_HOST = null;
        try {
            Properties prop = new Properties();
            FileInputStream in = new FileInputStream("properties.prop");
            prop.load(in);

            DEFAULT_HOST = prop.getProperty("default_host");
        }catch (FileNotFoundException e){
            JOptionPane.showMessageDialog(null,"Cannot find properties.prop file \nCreating on. You welcome");
            fileExist();
        }catch (IOException e) {
            JOptionPane.showMessageDialog(null,"Opps Something went Wrong. \nI blame the Developers" );
        }
        return DEFAULT_HOST;
    }

    private void fileExist() {
        File f = new File("properties.prop");
        if (!f.exists()) {
            try {
                PrintWriter pw = new PrintWriter("properties.prop", "UTF-8");
                pw.println("db_host=jdbc:h2:~/test");
                pw.println("db_name=sa");
                pw.println("db_pass=sa");
                pw.println("default_host=localhost");
                pw.println("default_port=1339");
                pw.flush();
                pw.close();

            } catch (FileNotFoundException ignored) {
            } catch (UnsupportedEncodingException ignored) {
            }
        }
    }

    private int getPort(){
        fileExist();
        int DEFAULT_PORT = 0;
        try {
            Properties prop = new Properties();
            FileInputStream in = new FileInputStream("properties.prop");
            prop.load(in);

            try {
                DEFAULT_PORT = Integer.parseInt(prop.getProperty("default_port")); //Port to Connect
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null,"Port has to be an INTEGER.");
                fileExist();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return DEFAULT_PORT;
    }

    public String getAccountDetails() {
        return account_details;
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

                account_details = in.readLine();

            }else if(response.equals("ALREADYLOGGEDIN:")){
                //Set alreadyLogged to true if user is loggedIn.
                alreadyLogged = true;
            }else {
                login_attempt = false;
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

    boolean userAlreadyLoggedIN(){
        return readServerResponse().startsWith("ALREADYLOGGEDIN:");
    }

    /***
     * Method calls the sendToServer method above and sends the username along with the message.
     * @param message message to send to server.
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
        } catch (Exception ignored) {
            closeConnection();
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
