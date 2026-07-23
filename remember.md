# remember.md — session log

## Changes made

### Client.java
- `usernameChallenge()`: `==` → `.equals()` for string comparison
- `startReceiverThread()`: removed redundant `.run()` call
- Constructor now initializes `this.messages`
- Added `getMessages()` getter
- `startReceiverThread()` passes existing `this.serverObjIn` to `ClientInputHandler`

### ClientInputHandler.java
- Constructor now accepts pre-existing `ObjectInputStream` instead of creating a duplicate (was causing `StreamCorruptedException`)
- Removed unused `OutputStream`, `BufferedReader`, `Comparator` fields

### Server.java
- Streams (OOS + OIS) created once per connection in `listen()`, then passed to `usernameChallenge()`
- `HandleClient` reuses the same OIS
- Removed duplicate OOS creation in both success/failure branches
- Removed stray `runClient.run()` (was running handler synchronously on caller thread)

### HandleClient.java
- Constructor now accepts pre-existing `ObjectInputStream` instead of creating a duplicate (was causing `EOFException`)

### ClientGUI.java
- Full Swing client UI with absolute layout (matching `serverGUI` style)
- Connection panel: username, server IP (default `localhost`), port (default `5200`), connect button, status label
- Scrollable `JTextArea` for message display
- Message input field + send button
- `Timer` polls `messages` list every 500ms to refresh display, auto-scrolls to bottom
- Cleanup on window close (stops timer, disconnects client)

### AGENTS.md
- Removed references to `javaLiveChatSystem` package
- Updated to focus on `live.chat.*` packages only
