package MyApp;

import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        new ThreadActions();
    }

}


class ThreadActions {

    boolean status = true;
    Socket socket;
    BufferedReader in;
    BufferedWriter out;
    BufferedReader inputUser;
    Thread read = new ReadMsg();
    Thread write = new WriteMsg();

    public ThreadActions() {
        try {
            socket = new Socket("127.0.0.1", 8080);
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            inputUser = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.print("Text your name: ");

            read.start();
            write.start();

        } catch (IOException ignored) {
        }

    }

    private class ReadMsg extends Thread {
        @Override
        public void run() {
            String str;
            try {
                while (status) {
                    str = in.readLine();
                    if (str != null) System.out.println(str);
                }
            } catch (IOException ignored) {
            }
        }
    }


    private class WriteMsg extends Thread {

        @Override
        public void run() {
            while (status) {
                String userWord;
                try {
                    userWord = inputUser.readLine();
                    out.write(userWord + "\n");
                    out.flush();
                    if (userWord.equals("/leave")) {
                        in.close();
                        inputUser.close();
                        out.close();
                        socket.close();
                        status = false;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }

}