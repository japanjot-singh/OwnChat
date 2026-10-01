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

<img width="887" height="551" alt="NCH1" src="https://github.com/user-attachments/assets/efce754c-9ec0-4bae-87ce-6771a5515606" />

<img width="889" height="554" alt="NCH2" src="https://github.com/user-attachments/assets/b9f020ad-d4a9-4436-ac46-af6a7da56224" />

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

## Optional Cloud Deployment: Run Your Own Server on a Virtual Machine

> [!IMPORTANT]
> OwnChat stays self-hosted and decentralized. There is no shared central OwnChat server in this model.  
> Each user/group/school/organization/team runs **its own** independent OwnChat server + Oracle database.
>
> Cloud-hosted deployment has been tested successfully (including Azure VM setup), but that testing only proves deployability.  
> There is **no** public/shared OwnChat cloud instance provided by this project for general use.  
> If you want cross-network chat, you must create and operate your **own** Azure VM (or another cloud VM), database, and OwnChat server.

### Architecture note

- Client app runs on user devices.
- OwnChat Java server runs on **your** VM.
- Oracle DB runs on the **same VM** in Docker and should stay private (`127.0.0.1:1521`).
- Clients connect to your VM public IP on port `4567`.

### Prerequisites

- Cloud account (example: Azure subscription)
- Ubuntu VM (tested workflow: Azure Ubuntu)
- Public IP for the VM
- SSH access (username + SSH key or password)
- Enough resources (at least enough RAM/disk for Oracle + Java server; monitor disk usage)
- Java installed on VM
- Docker installed on VM
- Oracle Container Registry account/access for `container-registry.oracle.com/database/free:latest`

### 1) Create the Ubuntu VM and record details

Record these values for later commands:

- `YOUR_RESOURCE_GROUP`
- `YOUR_VM_NAME`
- `YOUR_VM_USERNAME`
- `YOUR_PUBLIC_IP`
- SSH method (password or key)

### 2) Configure VM inbound networking rules

Allow:

- TCP `22` (SSH)
- TCP `4567` (OwnChat server)

> [!WARNING]
> Do **not** expose Oracle TCP `1521` publicly. Keep Oracle private (localhost/private network only).

### 3) SSH in and install Docker

```bash
ssh YOUR_VM_USERNAME@YOUR_PUBLIC_IP
sudo apt update
sudo apt install -y docker.io
sudo systemctl enable --now docker
sudo usermod -aG docker $USER
```

If Docker commands fail without `sudo`, log out and reconnect:

```bash
exit
ssh YOUR_VM_USERNAME@YOUR_PUBLIC_IP
docker ps
```

### 4) Pull Oracle Free image and run Oracle container

```bash
docker login container-registry.oracle.com
docker pull container-registry.oracle.com/database/free:latest
docker volume create oracle-data
docker run -d \
  --name oracle-free \
  --restart unless-stopped \
  -p 127.0.0.1:1521:1521 \
  -e ORACLE_PWD='YOUR_STRONG_ORACLE_PASSWORD' \
  -v oracle-data:/opt/oracle/oradata \
  container-registry.oracle.com/database/free:latest
```

### 5) Wait for readiness and verify Oracle port locally

Wait until logs show the exact message:

`DATABASE IS READY TO USE!`

```bash
docker logs -f oracle-free
```

Stop log streaming with `Ctrl+C`, then verify:

```bash
docker ps
ss -ltn | grep 1521
```

Expected local binding:

- `127.0.0.1:1521`
- container status includes `healthy`

### 6) Create `HR` user in `FREEPDB1` and grant required privileges/quota

```bash
docker exec -it oracle-free sqlplus / as sysdba
```

```sql
ALTER SESSION SET CONTAINER=FREEPDB1;
CREATE USER HR IDENTIFIED BY YOUR_STRONG_HR_PASSWORD;
GRANT CONNECT, RESOURCE, CREATE VIEW TO HR;
ALTER USER HR QUOTA UNLIMITED ON USERS;
EXIT;
```

> [!WARNING]
> If quota is missing, account creation can fail with `ORA-01950: insufficient quota on tablespace USERS`.

### 7) Prepare and import `OwnChatDB.sql` safely

- Use schema creation statements from `OwnChatDB.sql`.
- If preserving existing data, remove destructive reset statements (`DELETE`, `DROP`, full reset blocks).
- Keep schema definitions and constraints (including `ON DELETE CASCADE`)—these are not executable delete operations by themselves.

Import:

```bash
sqlplus hr/YOUR_STRONG_HR_PASSWORD@//localhost:1521/FREEPDB1 @/path/to/OwnChatDB.sql
```

If a trigger shows compile warnings, investigate/fix it if related features fail:

```bash
sqlplus hr/YOUR_STRONG_HR_PASSWORD@//localhost:1521/FREEPDB1
```

```sql
SELECT object_name, object_type, status
FROM user_objects
WHERE status <> 'VALID';

SELECT name, type, line, position, text
FROM user_errors
ORDER BY name, sequence;
```

### 8) Update JDBC URL, compile, and start OwnChat server

In `ServerL.java`, use `FREEPDB1` service format instead of old XE format:

- Old: `jdbc:oracle:thin:@localhost:1521:xe`
- New: `jdbc:oracle:thin:@//localhost:1521/FREEPDB1`

Compile and run:

```bash
javac -cp ojdbc17.jar:. ServerL.java
nohup env DB_USER='hr' DB_PASSWORD='YOUR_STRONG_HR_PASSWORD' \
java -cp ojdbc17.jar:. ServerL > ~/ownchat.log 2>&1 < /dev/null &
```

Verify:

```bash
pgrep -af ServerL
tail -n 50 ~/ownchat.log
ss -ltnp | grep 4567
```

### 9) Configure systemd so server auto-starts after VM reboot

Avoid hardcoding secrets in the service file. Put credentials in a root-protected env file.

```bash
sudo tee /etc/ownchat.env > /dev/null <<'EOF'
DB_USER=hr
DB_PASSWORD=YOUR_STRONG_HR_PASSWORD
EOF
sudo chmod 600 /etc/ownchat.env
```

```bash
sudo tee /etc/systemd/system/ownchat.service > /dev/null <<'EOF'
[Unit]
Description=OwnChat Java Server
After=docker.service network-online.target
Wants=network-online.target

[Service]
User=YOUR_VM_USERNAME
WorkingDirectory=/home/YOUR_VM_USERNAME
EnvironmentFile=/etc/ownchat.env
ExecStartPre=/bin/sh -c 'until nc -z 127.0.0.1 1521; do sleep 2; done'
ExecStart=/usr/bin/java -cp /home/YOUR_VM_USERNAME/ojdbc17.jar:. ServerL
Restart=always
RestartSec=5
StandardOutput=append:/home/YOUR_VM_USERNAME/ownchat.log
StandardError=append:/home/YOUR_VM_USERNAME/ownchat.log

[Install]
WantedBy=multi-user.target
EOF
```

```bash
sudo apt install -y netcat-openbsd
sudo systemctl daemon-reload
sudo systemctl enable --now ownchat
sudo systemctl status ownchat --no-pager
```

### 10) Configure clients to use your VM and test from another network

- In OwnChat client, set server IP to `YOUR_PUBLIC_IP`
- Use port `4567` if the UI asks for port
- Test from a different network/device when possible

Windows check:

```powershell
Test-NetConnection YOUR_PUBLIC_IP -Port 4567
```

`TcpTestSucceeded : True` indicates network reachability.

> [!NOTE]
> The client connects to **your own VM server**, not to a central OwnChat service.

### 11) Lifecycle and cost control

- Start/deallocate VM as needed to reduce compute cost.
- Public IP may change after stop/deallocate unless you reserve a static IP.
- After start/reboot, verify stack:

```bash
docker ps
sudo systemctl status ownchat --no-pager
ss -ltnp | grep 4567
```

### 12) Backup and retrieval guidance

Recommended: Oracle Data Pump export of live schema data.

```bash
docker exec oracle-free mkdir -p /opt/oracle/dpump
docker exec -it oracle-free sqlplus / as sysdba
```

```sql
ALTER SESSION SET CONTAINER=FREEPDB1;
CREATE OR REPLACE DIRECTORY OWNCHAT_DUMP AS '/opt/oracle/dpump';
GRANT READ, WRITE ON DIRECTORY OWNCHAT_DUMP TO HR;
EXIT;
```

```bash
docker exec -it oracle-free expdp \
  'hr/YOUR_STRONG_HR_PASSWORD@//localhost:1521/FREEPDB1' \
  DIRECTORY=OWNCHAT_DUMP \
  DUMPFILE=ownchat_hr.dmp \
  LOGFILE=ownchat_hr.log \
  SCHEMAS=HR
docker cp oracle-free:/opt/oracle/dpump/ownchat_hr.dmp ~/ownchat_hr.dmp
docker cp oracle-free:/opt/oracle/dpump/ownchat_hr.log ~/ownchat_hr.log
```

At minimum, you can copy `OwnChatDB.sql`, but remember:

- `OwnChatDB.sql` is schema/setup SQL
- it does **not** contain live chat/account data

### 13) Troubleshooting (common issues)

- **Port `4567` already in use**: duplicate `ServerL` process.  
  Check `pgrep -af ServerL`; stop old process before restarting.
- **Oracle port refused**: container still starting.  
  Wait for `DATABASE IS READY TO USE!` and `healthy`.
- **JDBC mismatch (`xe` vs `FREEPDB1`)**: update URL to `jdbc:oracle:thin:@//localhost:1521/FREEPDB1`.
- **`ORA-01950` quota error**: run `ALTER USER HR QUOTA UNLIMITED ON USERS;` in `FREEPDB1`.
- **Client cannot connect from internet**: verify Azure NSG allows inbound TCP `4567`.
- **Disk pressure on VM**: monitor with `df -h`; Oracle images/data need space.

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
