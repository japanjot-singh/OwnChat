# OwnChat 🔐🌎
## For those who yearn for freedom 🏳️
A Self-Hosted Chat Application for Desktops (client->Server->client) using Java Swing and Sockets and Oracle Database.

## Features
 
- **Account creation & login** — usernames/passwords stored in `USER_DETAILS`, checked on login.
- **Contacts** — add contacts, fetch a contact list, and see whether a contact is currently online before starting a chat.
- **Real-time messaging** — each logged-in client registers a socket with the server; messages are relayed live to the recipient if they have a chat window open, and every message is logged to `CHAT_HISTORY`.
- **Chat history** — reopening a chat with a contact replays prior messages, colored by sender, with date/time headers.
- **Themes** — Metal, Dark, and Light look-and-feels, persisted per user in the `SETTINGS` table and re-applied automatically on login.
- **Online/offline status tracking** — updated in `LOG_STATUS` whenever a user opens/closes the contacts list or logs in/out (including on window close, via an auto-logout hook).
- **Configurable server IP** — the client can point at any server host through a simple settings screen.
- **About screen** — developer/branding info panel.

## Database Schema
<img width="1536" height="1024" alt="ChatGPT Image Aug 14, 2026, 05_39_51 PM" src="https://github.com/user-attachments/assets/f63ea76c-3d8e-4ba3-9811-2f618416bd25" />

## At a Glance

### Welcome Screen
<img width="1779" height="1113" alt="Screenshot 2026-08-04 222154" src="https://github.com/user-attachments/assets/925c6ac1-4e85-449f-9c2a-6c57965e6bde" />

### Home
<img width="887" height="556" alt="NHOME" src="https://github.com/user-attachments/assets/99adb7f0-8ad3-495c-8f9c-c7d690286b5e" />

### Settings
<img width="887" height="552" alt="NSETTINGS" src="https://github.com/user-attachments/assets/1b15ea6a-7021-4a57-b43b-b95162398556" />

### Create Account
<img width="362" height="368" alt="NCREATE" src="https://github.com/user-attachments/assets/00cf0a5f-27ad-40e3-a871-b95841d33c8c" />

### Log In
<img width="364" height="369" alt="NLOG" src="https://github.com/user-attachments/assets/f18e1946-6099-4e42-97a1-c87f7fef06b7" />

### Add Contacts
<img width="362" height="368" alt="NADDC" src="https://github.com/user-attachments/assets/44927a1d-82b8-497e-911e-84c7c0b49824" />

### Contacts List
<img width="890" height="556" alt="NCL" src="https://github.com/user-attachments/assets/0f7f1382-7214-44f6-b382-cd8cf79dd408" />

### Chat Window

<img width="891" height="554" alt="NCH1" src="https://github.com/user-attachments/assets/04e47335-27ed-4a98-bfef-9004ce3553b3" />
<img width="889" height="554" alt="NCH2" src="https://github.com/user-attachments/assets/eed610a7-f523-4d96-b6ad-25dc6d48766f" />

### Server

<img width="365" height="372" alt="image" src="https://github.com/user-attachments/assets/5e16606b-294f-40d4-97d8-53dd3555947d" />

## Tech Stack
 
| Layer | Technology |
|---|---|
| Client UI | Java Swing (`JFrame`/`JPanel`, `JTabbedPane`, `JTextPane` for styled chat text) |
| Networking | Java `Socket` / `ServerSocket` over a single TCP port (`4567`), line-based text protocol |
| Server | Multi-threaded (`Thread` per client connection), Swing UI to configure DB credentials and show run status |
| Database | Oracle DB via JDBC (`jdbc:oracle:thin:@localhost:1521:xe`) |
| Concurrency | `ConcurrentHashMap<username, PrintWriter>` to track connected clients for message relaying |

## Project Structure
 
### Client
 
| File | Responsibility |
|---|---|
| `Welcome.java` | App entry point (`main`). Shows a splash/loading screen, then launches `App`. |
| `App.java` | Main window; hosts a `JTabbedPane` with Home, Settings, Set Server, and About tabs. |
| `Home.java` | Landing tab — Log In, Create Account, and Chat Now entry points. |
| `log.java` | Login form; sends credentials to the server and applies the user's saved theme on success. |
| `CrAc.java` | "Create Account" form. |
| `SetServerIP.java` | Lets the user set the server's IP address (stored statically for all socket calls). |
| `Settings.java` | Log out, change username, change theme, add contacts. |
| `Contacts_List.java` | Fetches and displays the logged-in user's contacts in a `JTable`; lets the user check if a contact is online and open a chat. |
| `Add_Contacts.java` | Form to add a new contact by name. |
| `ChatWindow.java` | The live chat UI — connects to the server, loads history, sends/receives messages, appends colored/styled text. |
| `About.java` | Static info/credits panel. |
| `clientSession.java` | Simple static holder for the current logged-in username and login state. |
 
### Server
 
| File | Responsibility |
|---|---|
| `ServerL.java` | Server entry point. UI to enter DB credentials, then listens on port `4567` and spawns a `clientHandler` thread per connection. |
| `clientHandler` (in `ServerL.java`) | Parses the first line of each connection as an action (e.g. `Log In`, `Create Account`, `Contacts`, `Add Contacts`, `Fetch contacts`, `checkUser`, `Change online status`, `ChatHistory`, `Set Theme`, `Check Theme`, `Change username`, `SLogOut`, `chat_connect`) and executes the matching SQL/relay logic. |
 
### Database
 
| File | Responsibility |
|---|---|
| `OwnChatDB.sql` | Schema: `USER_DETAILS`, `SETTINGS`, `LOG_STATUS`, `CONTACTS`, `CHAT_HISTORY`, plus triggers that auto-seed default settings and an initial "logged out" status when a new user is created. |
 
## How To Setup

### (1) Build Database:

- Download Oracle
- Run the OwnchatDB.sql File (excluding the statements of drop table; are only for dropping in case there is an issue or you want to alter)

#### Oracle XE capacity tuning for higher user-count tests

If load testing fails with Oracle session errors (for example `ORA-12519`), increase Oracle process/session limits and restart Oracle services.

1. Open SQL*Plus as SYSDBA:

```cmd
sqlplus / as sysdba
```

2. Check current limits:

```sql
SELECT name, value FROM v$parameter WHERE name IN ('processes','sessions');
```

3. Increase limits (example values used during testing):

```sql
ALTER SYSTEM SET processes=300 SCOPE=SPFILE;
ALTER SYSTEM SET sessions=335 SCOPE=SPFILE;
EXIT;
```

4. Restart Oracle XE services (Windows Command Prompt as Administrator):

```cmd
net stop OracleXETNSListener
net stop OracleServiceXE
net start OracleServiceXE
net start OracleXETNSListener
```

5. Verify updated values again in SQL*Plus.

### (2) Setup the Server

-  Download and then extract the file(OwnChatS-Portable.zip) from Releases section
- Run exe file
- The Window will open then input the database username and password to connect

### (3) Setting up the Client(OwnChat App)

- Download and then extract the file(OwnChat-Portable.zip) from Releases section
- Run the exe file
- Go to set Server then input the IP Address of your Sever
- Create account
- Log in
- Go to Chat Now and add contacts 
- Then again click chat now
- Contacts list will arrive 
- Select the contact then hit connect if the user is online the chat window will open
- Chat Freely

## Load Testing

`src/OwnChatLoadTest.java` is a self-contained utility that reproduces the real OwnChat flow for generated test users:

1. Create Account
2. Log In
3. Add Contacts (both directions for each pair/triad)
4. Open persistent `chat_connect` sockets
5. Send and receive messages for the configured duration
6. (Triad mode) periodically switch active chat target to simulate going back and choosing another contact
7. Close sockets and shut down executors

### Prerequisites

Before running the load test:

1. Start Oracle Database.
2. Initialize the schema with `src/OwnChatDB.sql`.
3. Start `ServerL` and set DB username/password in the server UI.
4. Ensure your server and DB credentials are configured for your own environment.

### Argument Order

`OwnChatLoadTest` accepts positional arguments in this exact order:

1. `host` (default: `127.0.0.1`)
2. `users` (default: `10`, must be even)
3. `durationSeconds` (default: `60`)
4. `messageIntervalMs` (default: `1000`)
5. `password` (default: `LoadTestPassword123`)
6. `setupTimeoutMs` (default: `10000`)
7. `chatReadTimeoutMs` (default: `2000`)
8. `chatMode` (`pair` or `triad`, default: `pair`)
9. `switchIntervalMs` (default: `10000`; used for `triad` mode target switching)

### IntelliJ Run

1. Open the project.
2. Run `ServerL` first.
3. Create a run configuration for `OwnChatLoadTest`.
4. Example Program Arguments:
   - `127.0.0.1 10 60 1000`
   - `127.0.0.1 20 60 1000`
   - `127.0.0.1 50 60 1000`
   - `127.0.0.1 6 60 1000 LoadTestPassword123 30000 5000 triad 8000`

### Command Line Build/Run

From repository root:

```bash
mkdir -p out
javac -d out src/OwnChatLoadTest.java
java -cp out OwnChatLoadTest 127.0.0.1 10 60 1000
```

More examples:

```bash
java -cp out OwnChatLoadTest 127.0.0.1 20 60 1000
java -cp out OwnChatLoadTest 127.0.0.1 50 60 1000
java -cp out OwnChatLoadTest 127.0.0.1 6 60 1000 LoadTestPassword123 30000 5000 triad 8000
```

Mode notes:

- `pair` mode: users chat in fixed pairs (`u0↔u1`, `u2↔u3`, ...).
- `triad` mode: users are grouped in 3s and each user alternates chat targets (example: `u0→u1`, then `u0→u2`, then back to `u1`), simulating contact switching.
- For `pair` mode, users must be even. For `triad` mode, users must be a multiple of 3.

What triad-mode success means:

- A successful triad run indicates the backend protocol path for switching active chat targets is working under load.
- It does **not** by itself guarantee every GUI behavior (window open/close timing, user click races, rendering) is perfect; run manual GUI checks for those.

More advanced combinations to test:

- `pair` mode with high fan-out users (for example 50/100/200 users).
- `triad` mode with shorter switch intervals (for example `2000` ms, `1000` ms).
- Longer-duration runs (10+ minutes) to observe stability/leaks.
- Bursty traffic by lowering `messageIntervalMs` (for example `200`, `100`).
- Mixed runs: first pair mode baseline, then triad mode at same user count to compare error rates and throughput.

To rebuild all project classes (including the new load-test class) without omitting files:

```bash
mkdir -p out
javac -d out src/*.java
```

If your environment does not have Oracle JDBC configured, full-project compilation may fail for server/client classes. In that case, compile `src/OwnChatLoadTest.java` directly as shown above.

### What to Watch During the Test

The utility prints periodic progress and final summary metrics:

- setup failures
- chat connection failures
- messages sent
- messages received
- send errors
- receive errors
- elapsed time

Failure signals to monitor:

- setup responses not matching expected protocol (`Saved`/`Exists`, `found`, `Added`)
- rising connection/send/receive errors
- server exceptions
- Oracle session/connection limit issues (the server opens a DB connection per request/handler)

### Safety Notes

- Test only systems and infrastructure you own or are explicitly authorized to test.
- Increase load gradually (for example 10 → 20 → 50 users) instead of jumping directly to high concurrency.

## Important Points

- For the client running on the same machine as server you do not need set server IP Address or just set as localhost if needed

## Like OwnChat?
If you like this project, please consider giving it a ⭐.

<img width="600" height="338" alt="ownchat github video (2)" src="https://github.com/user-attachments/assets/c8f64ee1-f320-4639-aeae-3b3de2972f16" />

## Roadmap
 
A web-based version of OwnChat is planned, built up in stages: Servlets → JSP → J2EE → Spring Boot, with an HTML/CSS/Bootstrap frontend.

## Author
 
**Japanjot Singh**

Email: japanjotsingh90@outlook.com
