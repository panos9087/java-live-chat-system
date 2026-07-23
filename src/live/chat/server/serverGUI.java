/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package live.chat.server;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author panos
 */
public class serverGUI extends JFrame {

    private JTextField userInput;
    private JLabel usersLbl;
    private JTextField ipInput;
    private JLabel ipLbl;
    private JTextField portInput;
    private JLabel portLbl;
    private JButton serverState;
    private boolean serverSt = false;
    private String btnText;
    private JTextField msgHistoryN;
    private JLabel msgHistoryLbl;

    private int maxUsers;
    private String givenIP;
    private int givenPort;
    private int historyNumber;
    private Server server;
    private boolean passedChecks;

    public serverGUI() {
        this.setSize(800, 600);

        this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        userInput = new JTextField();
        userInput.setBounds(50, 50, 100, 30);

        usersLbl = new JLabel("maximum users - integer");
        usersLbl.setBounds(50, 20, 150, 30);

        ipInput = new JTextField();
        ipInput.setBounds(250, 50, 150, 30);

        ipLbl = new JLabel("ip to bound - string");
        ipLbl.setBounds(250, 20, 150, 30);
        
        portInput = new JTextField();
        portInput.setBounds(250, 110, 150, 30);
        
        portLbl = new JLabel("port to bound - int");
        portLbl.setBounds(250, 80, 150, 30);
        
        serverState = new JButton("start server");
        serverState.setBounds(500, 50, 150, 30);
        serverState.addActionListener(e -> {
            usersLbl.setForeground(Color.black);
            ipLbl.setForeground(Color.black);
            portLbl.setForeground(Color.black);
            if (!serverSt) { // perform input checks
                passedChecks = inputCheck();
            }
            if (passedChecks) {
                serverSt = !serverSt;
                if (serverSt) {
                    try {
                        server = new Server(this.maxUsers, this.givenIP, this.givenPort);
                        Thread serverThread = new Thread(server);
                        serverThread.start();
                    } catch (IOException ex) {
                        serverState.setForeground(Color.red);
                        ex.printStackTrace();
                        serverSt = false;
                    }
                }
            }
            btnText = serverSt == true ? "stop server" : "start server";
            serverState.setText(btnText);

        });

        msgHistoryN = new JTextField();
        msgHistoryN.setBounds(50, 200, 150, 30);

        msgHistoryLbl = new JLabel("number of history messages - integer");
        msgHistoryLbl.setBounds(50, 150, 250, 30);

        this.add(userInput);
        this.add(usersLbl);
        this.add(ipInput);
        this.add(ipLbl);
        this.add(serverState);
        this.add(msgHistoryN);
        this.add(msgHistoryLbl);
        this.add(portInput);
        this.add(portLbl);
        this.setVisible(true);
    }

    public boolean inputCheck() {
        if (userInput.getText().matches("\\d+")) {
            usersLbl.setForeground(Color.green);
            maxUsers = Integer.parseInt(userInput.getText());
        } else {
            usersLbl.setForeground(Color.red);
            return false;
        }
        if (ipInput.getText().matches("\\d+.\\d+.\\d+.\\d+")) {
            ipLbl.setForeground(Color.green);
            givenIP = ipInput.getText();
        } else {
            ipLbl.setForeground(Color.red);
            return false;
        }
        if (portInput.getText().matches("\\d+")){
            portLbl.setForeground(Color.green);
            givenPort = Integer.parseInt(portInput.getText());
            
        }else{
            portLbl.setForeground(Color.red);
            return false;
        }
        return true;
    }
}
