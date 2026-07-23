package live.chat.client;

import live.chat.common.Message;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ClientGUI extends JFrame {

    private JTextField usernameInput;
    private JTextField ipInput;
    private JTextField portInput;
    private JTextField msgInput;
    private JButton connectBtn;
    private JButton sendBtn;
    private JLabel statusLbl;
    private JTextArea msgArea;
    private JScrollPane scrollPane;

    private Client client;
    private ArrayList<Message> messages;
    private Timer refreshTimer;

    public ClientGUI() {
        this.setSize(800, 600);
        this.setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Live Chat Client");

        JLabel usernameLbl = new JLabel("username");
        usernameLbl.setBounds(50, 20, 100, 30);
        usernameInput = new JTextField();
        usernameInput.setBounds(50, 50, 100, 30);

        JLabel ipLbl = new JLabel("server ip");
        ipLbl.setBounds(200, 20, 100, 30);
        ipInput = new JTextField("localhost");
        ipInput.setBounds(200, 50, 130, 30);

        JLabel portLbl = new JLabel("port");
        portLbl.setBounds(380, 20, 60, 30);
        portInput = new JTextField("5200");
        portInput.setBounds(380, 50, 60, 30);

        connectBtn = new JButton("connect");
        connectBtn.setBounds(490, 50, 130, 30);
        connectBtn.addActionListener(e -> connectToServer());

        statusLbl = new JLabel("Disconnected");
        statusLbl.setBounds(50, 100, 600, 30);

        msgArea = new JTextArea();
        msgArea.setEditable(false);
        msgArea.setLineWrap(true);
        msgArea.setWrapStyleWord(true);
        scrollPane = new JScrollPane(msgArea);
        scrollPane.setBounds(50, 140, 700, 350);

        msgInput = new JTextField();
        msgInput.setBounds(50, 510, 590, 30);
        msgInput.setEnabled(false);
        msgInput.addActionListener(e -> sendMessage());

        sendBtn = new JButton("send");
        sendBtn.setBounds(650, 510, 100, 30);
        sendBtn.setEnabled(false);
        sendBtn.addActionListener(e -> sendMessage());

        this.add(usernameLbl);
        this.add(usernameInput);
        this.add(ipLbl);
        this.add(ipInput);
        this.add(portLbl);
        this.add(portInput);
        this.add(connectBtn);
        this.add(statusLbl);
        this.add(scrollPane);
        this.add(msgInput);
        this.add(sendBtn);

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                cleanup();
            }
        });

        this.setVisible(true);
    }

    private void connectToServer() {
        String username = usernameInput.getText().trim();
        String ip = ipInput.getText().trim();
        String portStr = portInput.getText().trim();

        if (username.isEmpty()) {
            statusLbl.setText("Username cannot be empty");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException ex) {
            statusLbl.setText("Invalid port number");
            return;
        }

        connectBtn.setEnabled(false);
        statusLbl.setText("Connecting...");

        client = new Client(username, ip, port);

        if (!client.connectToServer()) {
            statusLbl.setText("Connection failed");
            connectBtn.setEnabled(true);
            return;
        }
        statusLbl.setText("Connected to server");

        if (!client.setStreams()) {
            statusLbl.setText("Failed to set up streams");
            client.disconnectFromServer();
            connectBtn.setEnabled(true);
            return;
        }
        statusLbl.setText("Authenticating...");

        this.messages = client.getMessages();

        if (!client.usernameChallenge()) {
            statusLbl.setText("Username rejected or handshake failed");
            client.disconnectFromServer();
            connectBtn.setEnabled(true);
            return;
        }
        statusLbl.setText("Connected as " + username + " ✓");

        client.startReceiverThread();

        msgInput.setEnabled(true);
        sendBtn.setEnabled(true);
        msgInput.requestFocus();

        refreshTimer = new Timer(500, e -> refreshMessages());
        refreshTimer.start();
    }

    private void sendMessage() {
        String text = msgInput.getText().trim();
        if (text.isEmpty()) return;
        client.sendMsg(text);
        msgInput.setText("");
        msgInput.requestFocus();
    }

    private void refreshMessages() {
        if (messages == null || messages.isEmpty()) return;

        StringBuilder sb = new StringBuilder();
        for (int i = messages.size() - 1; i >= 0; i--) {
            Message msg = messages.get(i);
            sb.append(msg.getUsername()).append(": ").append(msg.getData()).append("\n");
        }
        String current = msgArea.getText();
        String updated = sb.toString();
        if (!current.equals(updated)) {
            msgArea.setText(updated);
            msgArea.setCaretPosition(msgArea.getDocument().getLength());
        }
    }

    private void cleanup() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
        if (client != null) {
            client.disconnectFromServer();
        }
    }
}
