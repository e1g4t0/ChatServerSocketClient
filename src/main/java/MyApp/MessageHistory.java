package MyApp;

import java.sql.*;

public class MessageHistory extends Thread {

    Connection conn;

    @Override
    public void run() {
        try {
            String url = "jdbc:mysql://localhost/SCProject?serverTimezone=Europe/Moscow&useSSL=false";
            String username = "root";
            String password = "regina16";
            conn = DriverManager.getConnection(url, username, password);
        } catch (Exception ex) {
            System.out.println("Connection failed...");

            System.out.println(ex);
        }
    }

    public String getLastMessages() {
        String query = "select * from (select * from chat_history order by id desc limit 10) sub order by sub.id;";
        StringBuffer prepareMsgs = new StringBuffer("");

        try (Statement stmt = conn.createStatement()) {
            ResultSet messages = stmt.executeQuery(query);
            while (messages.next()) {
                String nextMsg = String.format("%s %s > %s %n",
                        messages.getString("message_datetime"),
                        messages.getString("username"), messages.getString("message"));
                prepareMsgs.append(nextMsg);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return prepareMsgs.toString();
    }

    public void addMessage(String[] data) {
        String query = "INSERT INTO chat_history (username, message, message_datetime) VALUE (?, ?, ?)";

        try (PreparedStatement prStmt = conn.prepareStatement(query)) {
            prStmt.setString(1, data[0]);
            prStmt.setString(2, data[1]);
            prStmt.setString(3, data[2]);

            prStmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

}
