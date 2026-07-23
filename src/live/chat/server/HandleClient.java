/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package live.chat.server;

import live.chat.common.Message;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author panos
 */
public class HandleClient implements Runnable {

    private ArrayList<ObjectOutputStream> clients;
    private ArrayList<Message> messages;
    private Socket currentClient;
    private InputStream clientInput;
    private OutputStream clientOutput;
    private ObjectInputStream inMsg;
    private ObjectOutputStream outMsg;

    public HandleClient(Socket clientSocket, ArrayList<ObjectOutputStream> clientList, ObjectInputStream in, ObjectOutputStream out, ArrayList<Message> messages) throws IOException {
        this.clients = clientList;
        this.currentClient = clientSocket;
        this.clientInput = currentClient.getInputStream();
        this.clientOutput = currentClient.getOutputStream();
        this.inMsg = in;
        this.outMsg = out;
        this.messages = messages;

    }

    public void run() {
        try {
            if (!this.messages.isEmpty()) {
                for (Message msg : this.messages) {
                    outMsg.writeObject(msg);
                    outMsg.flush();
                }
            }
            while (!this.currentClient.isClosed()) {
                try {

                    Message msg = (Message) inMsg.readObject();
                    this.messages.add(msg);
                    //System.out.println("got a message "+msg.toString());
                    for (ObjectOutputStream client : this.clients) {
                        if (client.equals(outMsg)) {
                            outMsg.writeObject(msg);
                            outMsg.flush();
                        } else {
                            client.writeObject(msg);
                            client.flush();
                            System.out.println(clients);
                        }
                    }
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(HandleClient.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("Thread has reached the end of it's execution for client: " + this.currentClient.getInetAddress().toString());
    }

    public ArrayList<ObjectOutputStream> getClients() {
        return clients;
    }

    public void setClients(ArrayList<ObjectOutputStream> clients) {
        this.clients = clients;
    }

}
