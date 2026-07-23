/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package live.chat.client;
import java.io.*;
import java.net.*;
import java.time.LocalDate;
import live.chat.common.Message;


public class LiveChatClient {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException{
        // the client to connect to the server port and send messages while message is not \bye and while condition is socket !isClosed()
        // if message is \bye sent the message and close the connection
        new ClientGUI();
    }
    
}
