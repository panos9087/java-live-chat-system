/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package live.chat.client;

import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ObjectInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import live.chat.common.Message;
import java.util.Comparator;
/**
 *
 * @author panos
 */
public class ClientInputHandler implements Runnable {

    private Socket serverInstance;
    private InputStream serverIn;
    private Message msg;
    private boolean stopExecution;
    private ArrayList<Message> messages;
    private ObjectInputStream serverObjIn;
    
    public ClientInputHandler(Socket serverInstance, ArrayList<Message> messages, ObjectInputStream in) throws IOException {
        this.serverInstance = serverInstance;
        this.serverIn = this.serverInstance.getInputStream();
        this.serverObjIn = in;
        this.stopExecution = false;
        this.messages = messages;
    }

    public void run() {
        try {
            while(!this.serverInstance.isClosed() && !this.stopExecution){
                msg = (Message) this.serverObjIn.readObject();
                this.messages.add(msg);
                this.messages.sort(Comparator.comparing(Message::getTimestmp).reversed());
                if(this.messages.size() >1000){
                    this.messages.remove(0);
                }
                Thread.sleep(500);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ClientInputHandler.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        } catch (InterruptedException ex) {
            Logger.getLogger(ClientInputHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isStopExecution() {
        return stopExecution;
    }

    public void setStopExecution(boolean stopExecution) {
        this.stopExecution = stopExecution;
    }
    
    
}
