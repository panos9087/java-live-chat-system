/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package live.chat.client;

import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import live.chat.common.Message;

/**
 *
 * @author panos
 */
public class Client {

    private String username;
    private String serverIp;
    private int serverPort;
    private Socket server;
    private InputStream serverIn;
    private OutputStream serverOut;
    private ObjectInputStream serverObjIn;
    private ObjectOutputStream serverObjOut;
    private ArrayList<Message> messages;

    public Client(String username, String serverIp, int serverPort) {
        this.username = username;
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.messages = new ArrayList<Message>();
    }

    public boolean connectToServer() {
        try {
            this.server = new Socket(getServerIp(), getServerPort());
            return this.server.isConnected();
        } catch (UnknownHostException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        }
    }

    public boolean setStreams() {
        try {
            this.serverIn = this.server.getInputStream();
            this.serverOut = this.server.getOutputStream();
            this.serverObjIn = new ObjectInputStream(this.serverIn);
            this.serverObjOut = new ObjectOutputStream(this.serverOut);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    public void disconnectFromServer() {
        LocalDateTime timestamp = LocalDateTime.now();
        String user = this.username;
        String dataType = "CONTROL";
        String data = "0";
        Message message = new Message(user, data, timestamp, dataType);
        try {
            this.serverObjOut.writeObject(message);
            this.serverObjOut.flush();
            if (this.server.isConnected() && !this.server.isClosed()) {
                this.server.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean usernameChallenge() {
        LocalDateTime date = LocalDateTime.now();
        Message handshake = new Message(this.username, this.username, date, "HANDSHAKE");
        if (this.server.isConnected() && !this.server.isClosed()) {
            try {
                this.serverObjOut.writeObject(handshake);
                this.serverObjOut.flush();
                Message response = (Message) this.serverObjIn.readObject();
                if ("1".equals(response.getData()) && "HANDSHAKE".equals(response.getDataType())) {
                    return true;
                }
                return false;
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        return false;
    }

    public void startReceiverThread() {
        try {
            ClientInputHandler handler = new ClientInputHandler(this.server, this.messages, this.serverObjIn);
            Thread receiverThread = new Thread(handler);
            receiverThread.start();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void sendMsg(String msg) {
        LocalDateTime timestamp = LocalDateTime.now();
        String user = this.username;
        String dataType = "TEXT";
        String data = msg;
        Message message = new Message(user, data, timestamp, dataType);
        try {
            this.serverObjOut.writeObject(message);
            this.serverObjOut.flush();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

}
