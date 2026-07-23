# AGENTS.md — java live chat system

## Build & run

- Ant-based NetBeans project, Java 17, no external deps.
- Build: `ant` (compiles to `build/classes/`, JAR to `dist/`).
- Server entrypoint: `live.chat.server.JavaLiveChatServer` — creates `Server` on `localhost:5200`.
- Client entrypoint: `live.chat.client.LiveChatClient` — hardcoded `localhost:5200`.
- No tests, no CI, no linter/formatter config.

## Project structure

```
src/live/chat/
  server/              ← server + per-client handler (serverGUI exists but is unused)
  client/              ← client + GUI stub + input receiver thread
  common/              ← Message (Serializable, shared)
```

## Architecture notes

- `Server` opens a `ServerSocket`, runs `usernameChallenge` (HANDSHAKE protocol) per connection, then spawns a `HandleClient` (Runnable) per client.
- `HandleClient` reads messages via polling loop (`available() > 0`) but **does not broadcast** — received messages are only logged.
- `Client` (`live.chat.client.Client`) wraps socket/streams, `usernameChallenge()`, `sendMsg()`, `startReceiverThread()`. LiveChatClient's `main()` does NOT use it — it's a standalone implementation that sends one HANDSHAKE then exits.
- `serverGUI` in `live.chat.server` is never instantiated — `JavaLiveChatServer` starts headless.
- `ClientGUI` is a stub (empty JFrame).
- `Message` uses `LocalDateTime` in `live.chat.common.Message` (constructor accepts `LocalDate` via auto-boxing in some callers — inconsistency).

## Known bugs / gotchas

- `Client.usernameChallenge()` compares strings with `==` instead of `.equals()` (line 91).
- `Client.startReceiverThread()` calls both `thread.start()` and `thread.run()` — the latter runs synchronously on the caller's thread (line 113).
- `LiveChatClient.main()` sends a single HANDSHAKE with a hardcoded username and exits — not a functional chat client.
- `serverGUI` button toggles `serverSt` state but never actually starts/stops a server — the button logic is purely cosmetic.
