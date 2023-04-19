package MyApp;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    static ServerSocket serverSocket;
    static boolean serverStatus = true;
    static List<ClientHandler> users = new ArrayList<ClientHandler>();
    static MessageHistory msgHistory;

    public static void main(String[] args) throws IOException, InterruptedException {
        serverSocket = new ServerSocket(8080);
        msgHistory = new MessageHistory();
        msgHistory.start();
        msgHistory.join();

        try {
            while (serverStatus) {
                Socket input = serverSocket.accept();
                ClientHandler user = new ClientHandler(input);
                users.add(user);
                user.start();
            }
        } catch (IOException ignored) {
        }
        serverSocket.close();
        System.out.println("Server has stopped");


    }

}

