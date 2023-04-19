package MyApp;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientHandler extends Thread {

    private static int i = 0;
    private Socket socket;
    private String name ;
    private BufferedReader in;
    private BufferedWriter out;

    SimpleDateFormat datetime = new SimpleDateFormat("y-M-dd h:m:s");



    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }


    @Override
    public void run() {
        System.out.println();
        try {
            boolean status = true;

            welcome();

            while (status) {
                String message = in.readLine();
                datetime = new SimpleDateFormat("y-M-dd h:m:s");

                if (message.strip().trim().equals("/leave")) {
                    status = false;
                    removeUser();
                } else {
                    Server.msgHistory.addMessage(new String[]{name, message, datetime.format(new Date())});
                    for (ClientHandler user : Server.users) {
                        user.clientSend(name, message);
                    }
                }
            }
        } catch (IOException | NullPointerException ioe) {
            Server.users.remove(this);
            throw new RuntimeException(ioe);
        }
    }

    private void welcome() throws IOException {
        System.out.println("======================= 1 ======================");
        String message = in.readLine();
        name = message.isEmpty() ? "User" + (++i) : message;

        out.write(Server.msgHistory.getLastMessages());
        System.out.println("======================= 2 ======================");

        for (ClientHandler user : Server.users) {
            user.serverSend(name + " has joined the server. ");
        }
        serverSend("Welcome! Use /leave to leave");

        out.flush();
    }

    private void clientSend(String from, String message) throws IOException {
        if (this.name.equals(from)) {
            message = String.format("%s You > %s%n", datetime.format(new Date()), message);
        } else message = String.format("%s %s > %s%n", datetime.format(new Date()), from, message);
        out.write(message);
        out.flush();
    }

    private void serverSend(String message) throws IOException {
        message = datetime.format(new Date()) + " Server > " + message + "\n";
        out.write(message);
        out.flush();
    }

    private void removeUser() {
        try {
            for (ClientHandler user : Server.users) {
                if (user.equals(this)) {
                    user.serverSend("You have left the server.");
                }
                user.serverSend(name + " has left the server.");
            }
            Server.users.remove(this);
            in.close();
            out.close();
            socket.close();
            System.out.println(name + " removed");
            this.interrupt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
