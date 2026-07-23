# Java Live Chat System

A simple TCP socket-based live chat system in Java with Swing GUIs. Multiple clients can connect to a central server, choose a username, and exchange messages in real time.

## Features

- **Username-based authentication** — handshake protocol ensures unique nicknames
- **Real-time broadcasting** — messages are instantly relayed to all connected clients
- **Swing GUIs** for both server and client
- **Pure Java** — no external dependencies, Java 17+

## Getting started

### Prerequisites

- Java 17+
- Apache Ant (or import into NetBeans)

### Build

```bash
ant
```

This compiles the source to `build/classes/` and packages a JAR into `dist/`.

### Run

**Start the server:**
```bash
java -cp build/classes live.chat.server.JavaLiveChatServer
```
This opens a Swing window where you can configure max users, IP, and port, then start the server.

**Start a client:**
```bash
java -cp build/classes live.chat.client.LiveChatClient
```
Enter a username, server IP (`server_ip`), port (`port_number`), and click connect.

## Project structure

```
src/live/chat/
  common/       Message.java              — shared data object (Serializable)
  server/       Server.java               — server socket loop, handshake, client dispatch
                HandleClient.java         — per-client reader & broadcast
                serverGUI.java            — server control panel (Swing)
                JavaLiveChatServer.java   — entry point
  client/       Client.java               — socket wrapper, streams, send/receive
                ClientGUI.java            — chat UI (Swing)
                ClientInputHandler.java   — background message receiver
                LiveChatClient.java       — entry point
```

## Protocol

| Step | Direction | Description |
|---|---|---|
| 1 | Client → Server | `Message{username, data=username, timestamp, dataType="HANDSHAKE"}` |
| 2 | Server → Client | `Message{username="server", data="1", timestamp, dataType="HANDSHAKE"}` on success, `data="0"` on failure |
| 3 | Client ↔ Server | Subsequent messages use `dataType="TEXT"` and are broadcast to all connected clients |
