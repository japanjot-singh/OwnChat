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
| Database | Oracle DB via JDBC (`jdbc:oracle:thin:@//localhost:1521/FREEPDB1`) |
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
> OwnChat remains self-hosted/on-premise first.  
> - For local/LAN usage, run the server locally on your own machine/network.  
> - If you need to chat with users on different networks, host **your own** server + database on a cloud VM (example: Microsoft Azure).  
>
> Cloud deployment has been tested successfully, but there is **no public/shared OwnChat server** provided by this project.  
> Every user/team must create and operate their own VM, Oracle database, and OwnChat server.

### Architecture note

- Client app runs on user devices.
- OwnChat Java server runs on **your** VM.
- Oracle DB runs on the same VM in Docker and should stay private on `127.0.0.1:1521`.
- End-to-end path: `client -> your VM OwnChat server (4567) -> your Oracle database`.

### 1) Azure setup (account, resource group, VM)

1. Create/sign in to Azure and ensure you have an active subscription (placeholder: `YOUR_SUBSCRIPTION`).
2. Create a resource group (`YOUR_RESOURCE_GROUP`) in your chosen region.
3. Create a VM (`YOUR_VM_NAME`) using **Ubuntu 24.04 LTS**.
4. Record these values for later commands and troubleshooting:
   - `YOUR_SUBSCRIPTION`
   - `YOUR_RESOURCE_GROUP`
   - `YOUR_VM_NAME`
   - region
   - `YOUR_VM_USERNAME`
   - authentication method (SSH key or password)
   - `YOUR_PUBLIC_IP`
5. Public IP can be dynamic by default. If you need stable reconnect/client settings, reserve/associate a static IP.

Auth guidance:
- **SSH key auth** is recommended for security.
- **Password auth** can be used for learning/testing if your policy allows it.

Sizing/storage guidance:
- Pick VM size/disk with enough RAM/CPU/storage for Docker + Oracle + Java.
- Monitor disk usage regularly:

```bash
df -h
```

### 2) Azure networking (NSG + host firewall)

Create custom inbound NSG rules:
- Allow TCP `22` (SSH) for administration.
- Allow TCP `4567` (OwnChat server) for clients.
- Restrict source IP ranges where practical (avoid `Any` when you can).

Do **not** expose Oracle TCP `1521` publicly. Keep Oracle on localhost/private networking only.

Important: Azure NSG and the VM firewall (for example `ufw`) are separate security layers. Both can block traffic.

Windows connectivity test:

```powershell
Test-NetConnection YOUR_PUBLIC_IP -Port 4567
```

### 3) Windows terminal workflow to Ubuntu + Docker install

SSH from Windows PowerShell:

```powershell
ssh YOUR_VM_USERNAME@YOUR_PUBLIC_IP
```

Install Docker on Ubuntu:

```bash
sudo apt update
sudo apt install -y docker.io
sudo systemctl enable --now docker
sudo usermod -aG docker $USER
```

Reconnect if group membership has not applied yet:

```bash
exit
```

```powershell
ssh YOUR_VM_USERNAME@YOUR_PUBLIC_IP
```

```bash
docker ps
```

### 4) Oracle registry login vs VM login vs Oracle DB password

These are different credentials:
- Ubuntu SSH login (`YOUR_VM_USERNAME` + SSH key/password): only for VM access.
- Oracle Container Registry login (`container-registry.oracle.com`): for pulling Oracle image.
- Oracle database password (`ORACLE_PWD`): initial Oracle administrative DB password inside container.

Get your Oracle registry token/credential from your own Oracle account + container registry sign-in flow (including Oracle password reset/recovery if needed).  
Never publish registry tokens or real passwords in README/source code.

Login and pull image:

```bash
echo 'YOUR_ORACLE_REGISTRY_TOKEN' | docker login container-registry.oracle.com -u 'YOUR_ORACLE_REGISTRY_USERNAME' --password-stdin
docker pull container-registry.oracle.com/database/free:latest
```

### 5) Run Oracle Free with persistent storage

```bash
docker volume create oracle-data
docker run -d \
  --name oracle-free \
  --restart unless-stopped \
  -p 127.0.0.1:1521:1521 \
  -e ORACLE_PWD='YOUR_STRONG_ORACLE_PASSWORD' \
  -v oracle-data:/opt/oracle/oradata \
  container-registry.oracle.com/database/free:latest
```

`ORACLE_PWD` is chosen by you when creating the container.  
It is the initial Oracle admin DB password, **not** automatically an Oracle registry token, and not necessarily the HR password unless you intentionally set HR to the same value.  
Save it in your secure secret manager/password manager immediately; Docker will not display it later.

Readiness checks:

```bash
docker logs -f oracle-free
docker ps
docker inspect --format='{{.State.Health.Status}}' oracle-free
ss -ltn | grep 1521
```

Wait until logs show `DATABASE IS READY TO USE!` and health is `healthy`.

### 6) Oracle/HR setup and common pitfalls

Connect as SYSDBA:

```bash
docker exec -it oracle-free sqlplus / as sysdba
```

Run commands in order:

```sql
ALTER SESSION SET CONTAINER=FREEPDB1;
CREATE USER HR IDENTIFIED BY YOUR_STRONG_ORACLE_PASSWORD;
ALTER USER HR ACCOUNT UNLOCK;
GRANT CONNECT, RESOURCE, CREATE VIEW TO HR;
ALTER USER HR QUOTA UNLIMITED ON USERS;
EXIT;
```

If `ALTER USER HR ...` fails because HR does not exist, create it first.

`ORA-01950: insufficient quota on tablespace USERS` means quota is missing; own account creation inserts can fail until `ALTER USER HR QUOTA UNLIMITED ON USERS;` is applied.

SQL*Plus paste pitfalls:
- Do not paste `SQL>` prompts.
- Enter statements separately.
- If malformed pasted PL/SQL leaves SQL*Plus in a bad buffer state, use `Ctrl+C` and/or `CLEAR BUFFER`, then re-enter commands cleanly.

JDBC service name pitfall:
- Older XE-style URL: `jdbc:oracle:thin:@localhost:1521:xe`
- Correct Free PDB URL: `jdbc:oracle:thin:@//localhost:1521/FREEPDB1`

### 7) Schema import and data preservation

Before editing import SQL, back up:

```bash
cp /path/to/OwnChatDB.sql /path/to/OwnChatDB.sql.bak
```

When preserving existing data:
- Remove executable destructive reset statements (`DELETE`, `DROP`, full reset blocks) before running.
- Do **not** remove `ON DELETE CASCADE` just because it contains the word `DELETE`; it is a constraint definition.

Import schema:

```bash
sqlplus hr/YOUR_STRONG_ORACLE_PASSWORD@//localhost:1521/FREEPDB1 @/path/to/OwnChatDB.sql
```

Do not blindly rerun schema creation on an already-initialized database with existing tables/data.

If trigger compilation warnings appear, inspect:

```sql
SELECT object_name, object_type, status
FROM user_objects
WHERE status <> 'VALID';

SELECT name, type, line, position, text
FROM user_errors
ORDER BY name, sequence;
```

`OwnChatDB.sql` is schema/setup SQL and does not include live chat/account data.  
For actual data backup/retrieval, use Data Pump (concise example):

```bash
docker exec oracle-free mkdir -p /opt/oracle/dpump
docker exec -it oracle-free expdp \
  'hr/YOUR_STRONG_ORACLE_PASSWORD@//localhost:1521/FREEPDB1' \
  DIRECTORY=DATA_PUMP_DIR \
  DUMPFILE=ownchat_hr.dmp \
  LOGFILE=ownchat_hr.log \
  SCHEMAS=HR
docker cp oracle-free:/opt/oracle/admin/FREE/dpdump/ownchat_hr.dmp ~/ownchat_hr.dmp
```

### 8) OwnChat server compile/start, process conflicts, and systemd

Compile:

```bash
javac -cp ojdbc17.jar:. ServerL.java clientSession.java
```

Quick start with environment variables:

```bash
nohup env DB_USER='hr' DB_PASSWORD='YOUR_STRONG_ORACLE_PASSWORD' \
java -cp ojdbc17.jar:. ServerL > ~/ownchat.log 2>&1 < /dev/null &
```

Verify:

```bash
ps -ef | grep '[S]erverL'
ss -ltnp | grep 4567
tail -n 50 ~/ownchat.log
```

If you see `Address already in use`, an older `ServerL` is still running:

```bash
pgrep -af ServerL
pkill -f 'java -cp ojdbc17.jar:. ServerL'
```

If Oracle is still starting, you may see connection refused errors in logs until container becomes ready.

Systemd service (recommended for auto-restart and boot startup):

```bash
sudo tee /etc/ownchat.env > /dev/null <<'EOF'
DB_USER=hr
DB_PASSWORD=YOUR_STRONG_ORACLE_PASSWORD
EOF
sudo chmod 600 /etc/ownchat.env
```

```bash
sudo tee /etc/systemd/system/ownchat.service > /dev/null <<'EOF'
[Unit]
Description=OwnChat Java Server
After=docker.service network-online.target
Wants=docker.service network-online.target

[Service]
Type=simple
User=YOUR_VM_USERNAME
WorkingDirectory=/home/YOUR_VM_USERNAME
EnvironmentFile=/etc/ownchat.env
ExecStartPre=/bin/sh -c 'until nc -z 127.0.0.1 1521; do sleep 2; done'
ExecStart=/usr/bin/java -cp /home/YOUR_VM_USERNAME/ojdbc17.jar:. ServerL
Restart=on-failure
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
journalctl -u ownchat -n 100 --no-pager
```

Do not place real credentials in README or commit them to source control.

### 9) Client setup for cross-network chat

- In client settings, set server IP to `YOUR_PUBLIC_IP`.
- Use port `4567` if client asks for port.
- From another machine/network, do **not** use `localhost`; use your VM public IP.
- Successful cross-network testing means your self-hosted deployment works; it still does not imply a central public OwnChat service.

### 10) VM lifecycle and restart checks

- Shutting down your laptop does not necessarily stop/deallocate Azure VM.
- Use Azure start/stop/deallocate controls intentionally for cost and availability.
- Example Azure CLI lifecycle commands:

```powershell
az vm start --subscription YOUR_SUBSCRIPTION --resource-group YOUR_RESOURCE_GROUP --name YOUR_VM_NAME
az vm deallocate --subscription YOUR_SUBSCRIPTION --resource-group YOUR_RESOURCE_GROUP --name YOUR_VM_NAME
```

- Reconnect after start:

```powershell
ssh YOUR_VM_USERNAME@YOUR_PUBLIC_IP
```

- Dynamic public IP may change after deallocation unless static/reserved IP is used.

After restart, verify:

```bash
docker ps
docker inspect --format='{{.State.Health.Status}}' oracle-free
sudo systemctl status ownchat --no-pager
ss -ltnp | grep 4567
```

If `ownchat` systemd service is enabled, do not start an extra manual `nohup` Java server in parallel.

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
