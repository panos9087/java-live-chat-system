/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package live.chat.server;

/**
 *
 * @author panos
 */
import live.chat.common.Message;
import java.io.*;
import java.util.ArrayList;
import java.net.*;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {

    private int maximumUsers;
    private String ip;
    private int port;
    private ArrayList<Message> messages;
    private ArrayList<String> usernames;
    private ArrayList<ObjectOutputStream> clients;
    private ServerSocket server;

    public Server(int maxUsers, String ip, int port) throws IOException {
        this.maximumUsers = maxUsers;
        this.ip = ip;
        this.port = port;
        this.messages = new ArrayList<Message>();
        this.usernames = new ArrayList<String>();
        this.clients = new ArrayList<ObjectOutputStream>();
        this.server = new ServerSocket(this.port);
        System.out.println("Server successfully initialized.");
    }

    public void run() {
        while (this.clients.size() <= this.maximumUsers) {
            System.out.println("Server listening...");
            Socket client = null;
            try {
                client = server.accept();
                System.out.println("client connected");
                ObjectOutputStream out = null;
                out = new ObjectOutputStream(client.getOutputStream());
                out.flush();
                ObjectInputStream in = null;
                in = new ObjectInputStream(client.getInputStream());

                boolean passedChallenge = usernameChallenge(client, in, out);

                System.out.println("Did client passed username challenge : " + passedChallenge);
                LocalDateTime localDate = LocalDateTime.now();
                if (!passedChallenge) {
                    Message msg = new Message("server", "0", localDate, "HANDSHAKE");

                    out.writeObject(msg);
                    out.flush();
                    client.close();

                } else {
                    Message msg = new Message("server", "1", localDate, "HANDSHAKE");

                    out.writeObject(msg);
                    out.flush();
                    clients.add(out);
                    HandleClient clientHandler = new HandleClient(client, clients, in, out, messages);
                    Thread runClient = new Thread(clientHandler);
                    runClient.start();
                    System.out.println("new client connected @" + client.getPort());

                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean usernameChallenge(Socket client, ObjectInputStream in, ObjectOutputStream out) {
        System.out.println("waiting for client HANDSHAKE");
        try {
            Message receivedMessage;
            receivedMessage = (Message) in.readObject();
            System.out.println("Got message from client");
            if (receivedMessage.getDataType().contains("HANDSHAKE")) {
                String username = receivedMessage.getUsername();
                if (getUsernames().contains(username)) {
                    return false;
                }
                this.usernames.add(receivedMessage.getData());
                return true;
            }
            return false;
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public int getMaximumUsers() {
        return maximumUsers;
    }

    public void setMaximumUsers(int maximumUsers) {
        this.maximumUsers = maximumUsers;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public ArrayList<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(ArrayList<String> usernames) {
        this.usernames = usernames;
    }

}
