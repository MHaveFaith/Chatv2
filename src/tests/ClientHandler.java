package tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Keno on 11/26/2016.
 */
public class ClientHandler extends Thread {

    private static Socket clientSocket = null;
    private static PrintWriter out = null;
    private static BufferedReader in = null;


    public ClientHandler(Socket socket) {
        clientSocket = socket;
    }

    public void sendToServer(String text) {
        out.println(text);
    }

    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while (true) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ee) {
                    ee.printStackTrace();
                }

                String line = in.readLine();
                System.out.println(line);
            }

        } catch (IOException ie) {
            ie.printStackTrace();
            closeConnection();
        }
    }

    /*
     * Lazy so writing this once ..
     */
    public void closeConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();

        } catch (IOException iii) {
            iii.printStackTrace();
        }
    }
}
