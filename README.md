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
> OwnChat stays self-hosted and decentralized.
> There is no OwnChat-managed central server in this model.
> If you use cloud, you still deploy and operate **your own** VM, **your own** Oracle database, and **your own** OwnChat server.

### When to choose cloud deployment

- **Same LAN / same local network:** run OwnChat server + Oracle locally/on-prem, then clients use the local/private address.
- **Different networks / Internet users:** deploy your own OwnChat stack on your own cloud VM (example: Microsoft Azure), then clients connect to your VM public IP on port `4567`.
- Cloud VM is optional and useful when you cannot host reliably on a local machine or need cross-network access.

### Architecture note (still self-hosted)

- Client app runs on user devices.
- OwnChat Java server runs on **your** VM.
- Oracle runs on the **same VM** in Docker and should stay private (`127.0.0.1:1521`).
- Clients connect to your VM public IP on `4567`.

### 1) Microsoft Azure setup: account, subscription, resource group, VM

1. Create/sign in to Azure account.
2. Select/create a subscription (`YOUR_SUBSCRIPTION`).
3. Create/select a resource group (`YOUR_RESOURCE_GROUP`).
4. Create VM (`YOUR_VM_NAME`) with image **Ubuntu 24.04 LTS**.
5. Choose a practical small size for initial testing (for example, a small general-purpose VM) and ensure enough RAM/disk for Docker + Oracle + Java. Do not assume fixed performance; monitor real usage.
6. Record these values for later:
   - `YOUR_SUBSCRIPTION`
   - `YOUR_RESOURCE_GROUP`
   - `YOUR_VM_NAME`
   - `YOUR_VM_USERNAME`
   - `YOUR_REGION`
   - authentication method (SSH key preferred)
   - `YOUR_PUBLIC_IP`

> [!NOTE]
> Prefer SSH key authentication for security. Password auth can be used for learning/testing, but is less secure.

### 2) Azure networking (NSG) and public IP behavior

Create/check NSG inbound rules:

- Allow TCP `22` for SSH (restrict source IP/CIDR where practical).
- Allow TCP `4567` for OwnChat client traffic (restrict source ranges where practical).
- Do **not** allow public inbound TCP `1521`.

> [!WARNING]
> Oracle port `1521` is for local VM/container DB access. Keep it bound to `127.0.0.1` and do not expose it to the Internet.

Find `YOUR_PUBLIC_IP` in Azure VM overview (Networking/Overview). If the VM is deallocated and IP is dynamic, it can change. Use a static/reserved public IP to keep it stable.

> [!NOTE]
> `ufw` being inactive does **not** replace Azure NSG rules. VM firewall and NSG are separate layers; enforce both as needed.

### 3) SSH from Windows and install Docker on Ubuntu VM

```powershell
ssh YOUR_VM_USERNAME@YOUR_PUBLIC_IP
```

```bash
sudo apt update
sudo apt install -y docker.io
sudo systemctl enable --now docker
sudo usermod -aG docker $USER
```

Reconnect so docker group membership applies:

```bash
exit
```

```powershell
ssh YOUR_VM_USERNAME@YOUR_PUBLIC_IP
```

```bash
docker ps
```

### 4) Oracle Container Registry login (credential pitfalls)

```bash
docker login container-registry.oracle.com
```

- This username/password is your Oracle account/registry credential (or generated registry token/secret), **not** your Ubuntu SSH password.
- It is also **not automatically** your Oracle database password.
- You may need to accept Oracle container license/terms in your Oracle account before pull succeeds.
- If you forgot registry credentials, recover/reset through Oracle sign-in/account flow; do not post secrets publicly.

### 5) Pull Oracle Free and run with persistent storage

```bash
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

> [!IMPORTANT]
> `ORACLE_PWD` is chosen by **you** at `docker run` time and sets initial Oracle administrative account passwords in the container.
> It is separate from Oracle registry login credentials/token, and not necessarily the `HR` app schema password unless you deliberately set them equal.

> [!WARNING]
> If you used a generated secret/token during registry login, store it securely now. Docker/Oracle may not show it again later.

### 6) Wait for DB readiness and verify local Oracle port

```bash
docker logs -f oracle-free
```

Wait for `DATABASE IS READY TO USE!`, then `Ctrl+C` and verify:

```bash
docker ps
nc -vz 127.0.0.1 1521
```

If `nc` is missing:

```bash
sudo apt install -y netcat-openbsd
```

### 7) SQL*Plus setup in FREEPDB1 (HR user, grants, quota)

```bash
docker exec -it oracle-free sqlplus / as sysdba
```

```sql
ALTER SESSION SET CONTAINER=FREEPDB1;

CREATE USER HR IDENTIFIED BY YOUR_STRONG_HR_PASSWORD;
ALTER USER HR ACCOUNT UNLOCK;
GRANT CONNECT, RESOURCE, CREATE VIEW TO HR;
ALTER USER HR QUOTA UNLIMITED ON USERS;
```

If HR already exists, use:

```sql
ALTER USER HR IDENTIFIED BY YOUR_STRONG_HR_PASSWORD;
ALTER USER HR ACCOUNT UNLOCK;
```

Then:

```sql
EXIT;
```

> [!WARNING]
> Missing quota can cause `ORA-01950: insufficient quota on tablespace USERS` during object/account creation.

### 8) SQL*Plus paste pitfalls and recovery

- Do **not** paste `SQL>` prompts.
- Enter commands as plain SQL, one statement at a time.
- If paste breaks command buffer, use `Ctrl+C`, then run:

```sql
CLEAR BUFFER;
```

### 9) Schema import safety (`OwnChatDB.sql`)

- Back up your SQL file before editing:

```bash
cp /path/to/OwnChatDB.sql /path/to/OwnChatDB.sql.bak
```

- `FREEPDB1` is required for Oracle Free. Do not use old `xe` JDBC/service naming.
- If preserving existing data, remove executable destructive reset statements (`DELETE`, `DROP`, reset blocks).
- Do **not** remove safe relational constraints such as `ON DELETE CASCADE`.
- Import once carefully; do not rerun blindly after objects already exist.

Import:

```bash
sqlplus hr/YOUR_STRONG_HR_PASSWORD@//localhost:1521/FREEPDB1 @/path/to/OwnChatDB.sql
```

### 10) Trigger compile warnings check

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

### 11) OwnChat server compile/start/verify

Use `FREEPDB1` JDBC URL format in server code/config:

- `jdbc:oracle:thin:@//localhost:1521/FREEPDB1`

Compile and run:

```bash
javac -cp ojdbc17.jar:. ServerL.java clientSession.java
nohup env DB_USER='HR' DB_PASSWORD='YOUR_STRONG_HR_PASSWORD' \
java -cp ojdbc17.jar:. ServerL > ~/ownchat.log 2>&1 < /dev/null &
```

Verify:

```bash
ps -ef | grep '[S]erverL'
ss -ltnp | grep 4567
tail -n 50 ~/ownchat.log
```

If you get `java.net.BindException: Address already in use`, stop old instance and keep only one server process:

```bash
pgrep -af ServerL
pkill -f 'java -cp ojdbc17.jar:. ServerL'
```

If Oracle connection is refused, database is likely still starting; wait for healthy status in `docker ps`.

### 12) systemd service (recommended for restart recovery)

Store credentials in protected env file (avoid putting secrets in README/service body in real deployments):

```bash
sudo tee /etc/ownchat.env > /dev/null <<'EOF'
DB_USER=HR
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
journalctl -u ownchat -n 50 --no-pager
```

### 13) Client setup for cross-network chat

- On each client machine, set server IP to `YOUR_PUBLIC_IP`.
- Use port `4567` where client asks for port.
- Do **not** use `localhost` from a different machine/network.

Windows reachability test:

```powershell
Test-NetConnection YOUR_PUBLIC_IP -Port 4567
```

Expect:

- `TcpTestSucceeded : True`

Successful chat across different networks confirms: `client -> your VM OwnChat server -> your VM local Oracle database`.

### 14) Lifecycle, start/stop, and recovery operations

To reduce compute cost, stop/deallocate VM when idle and start it when needed.

After VM start, SSH in and verify:

```bash
docker ps
sudo systemctl status ownchat --no-pager
ss -ltnp | grep 4567
```

If VM was deallocated and public IP is dynamic, `YOUR_PUBLIC_IP` may change unless static/reserved.

> [!NOTE]
> Shutting down your laptop does not necessarily stop the Azure VM.

Safe stop/start examples (when managing app manually):

```bash
# stop app + db container manually
pkill -f 'java -cp ojdbc17.jar:. ServerL' || true
docker stop oracle-free

# start db container manually
docker start oracle-free
```

> [!WARNING]
> If `ownchat.service` is enabled, do not start another manual `nohup java ... ServerL`; duplicate starts can cause bind conflicts.

Operational checks:

```bash
df -h
```

Watch for disk pressure and OS notices like pending restart/reboot requirements.

### 15) Backup/retrieval: live data vs schema SQL

`OwnChatDB.sql` is schema/setup SQL, not a live-data backup.

Concise Data Pump export/retrieval example:

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
