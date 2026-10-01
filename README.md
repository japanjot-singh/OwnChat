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
> OwnChat is still a **self-hosted/on-premise** project.
> - If all users are on the same LAN, run server + DB locally.
> - If users are on different networks, each user/team should host their **own** cloud VM server.
>
> Cloud deployment was tested successfully, but there is **no public/shared OwnChat server**.
> You must create and operate your own VM, Oracle DB, and OwnChat server.

### Architecture and scope

- Client runs on user devices.
- OwnChat Java server runs on your VM (port `4567`).
- Oracle DB runs on the same VM in Docker and stays private on `127.0.0.1:1521`.
- Flow: `client -> your VM OwnChat server -> your Oracle database`.

---

### 1) Exact files required on the VM

README commands below assume files are in `/home/azureuser`.

Required:
- `ServerL.java` (**cloud-modified server source**) with JDBC URL:
  - `jdbc:oracle:thin:@//localhost:1521/FREEPDB1`
  - not old XE URL (`jdbc:oracle:thin:@localhost:1521:xe`)
- `clientSession.java` (needed by the shown compile command)
- `OwnChatDB.sql` (schema setup)
- `ojdbc17.jar` (Oracle JDBC driver)
- Java runtime/compiler on VM (OpenJDK 17+ recommended)

Notes:
- Client UI files do **not** need to be copied to VM for server-only hosting.
- At the time of writing, a separate cloud-server bundle with the `FREEPDB1`-updated `ServerL.java` is not published in this repository.
- Build from source (`/src`) and copy the required files manually.
- If a release/server bundle is later published, use that bundle path instead.
- `ojdbc17.jar` must come from Oracle/authorized distribution. Do not commit drivers, tokens, or licenses to this repository.

Windows -> VM copy examples (PowerShell):

```powershell
scp C:\\path\\to\\ServerL.java YOUR_ADMIN_USERNAME@YOUR_PUBLIC_IP:/home/azureuser/
scp C:\\path\\to\\clientSession.java YOUR_ADMIN_USERNAME@YOUR_PUBLIC_IP:/home/azureuser/
scp C:\\path\\to\\OwnChatDB.sql YOUR_ADMIN_USERNAME@YOUR_PUBLIC_IP:/home/azureuser/
scp C:\\path\\to\\ojdbc17.jar YOUR_ADMIN_USERNAME@YOUR_PUBLIC_IP:/home/azureuser/
```

Verify on VM:

```bash
ls -lh /home/azureuser/ServerL.java /home/azureuser/clientSession.java /home/azureuser/OwnChatDB.sql /home/azureuser/ojdbc17.jar
```

---

### 2) Azure account, VM, and networking

1. Sign in to Azure and select/create `YOUR_SUBSCRIPTION`.
2. Create resource group: `YOUR_RESOURCE_GROUP`.
3. Create VM `YOUR_VM_NAME` with Ubuntu 24.04 LTS.
4. Record: subscription, resource group, VM name, `YOUR_ADMIN_USERNAME`, auth method, `YOUR_PUBLIC_IP`, region.
5. Prefer SSH keys; password auth can be used for learning/testing.
6. If client IP must stay stable, use a static/reserved public IP.

NSG inbound rules:
- Allow TCP `22` (SSH).
- Allow TCP `4567` (OwnChat server).
- Restrict source ranges where possible.
- **Do not** open TCP `1521` publicly; Oracle is mapped to `127.0.0.1:1521`.

Azure NSG and VM firewall (`ufw`) are separate layers; both can block traffic.

Windows SSH example:

```powershell
ssh YOUR_ADMIN_USERNAME@YOUR_PUBLIC_IP
```

Laptop shutdown != VM shutdown. VM keeps running unless you stop/deallocate it in Azure.

---

### 3) VM terminal bootstrap + Docker (Ubuntu 24.04)

```bash
sudo apt update && sudo apt upgrade -y
sudo apt install -y docker.io netcat-openbsd openjdk-17-jdk
sudo systemctl enable --now docker
sudo usermod -aG docker YOUR_ADMIN_USERNAME
```

Reconnect so docker group is applied:

```bash
exit
```

```powershell
ssh YOUR_ADMIN_USERNAME@YOUR_PUBLIC_IP
```

Check Docker:

```bash
docker ps
```

---

### 4) Oracle registry credentials vs DB passwords

These are different credentials:
- Ubuntu SSH login (VM access)
- Oracle Container Registry credentials/token (for `docker login`)
- Oracle DB password (`ORACLE_PWD` during container creation)

If Oracle registry login fails, recover via Oracle sign-in/password reset and ensure Oracle DB Free container terms are accepted in Oracle Container Registry. Do not store real secrets in README/repo.

Login and pull:

```bash
echo 'YOUR_ORACLE_REGISTRY_TOKEN' | docker login container-registry.oracle.com -u 'YOUR_ORACLE_REGISTRY_USERNAME' --password-stdin
docker pull container-registry.oracle.com/database/free:latest
```

---

### 5) Run Oracle Free container (persistent + private listener)

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

`ORACLE_PWD` means:
- Chosen by you at `docker run` time
- Initializes Oracle admin credentials
- Not Oracle registry token
- Not automatically HR password unless you choose same value

Store it securely (password manager/secret manager).

Readiness checks:

```bash
docker ps
docker logs -f oracle-free
docker inspect --format='{{.State.Health.Status}}' oracle-free
nc -vz 127.0.0.1 1521
```

Continue only after log shows `DATABASE IS READY TO USE!` and health is `healthy`.

---

### 6) Oracle setup + schema import (including common issues)

Open SQL*Plus:

```bash
docker exec -it oracle-free sqlplus / as sysdba
```

Run one statement at a time (do not paste `SQL>`):

```sql
ALTER SESSION SET CONTAINER=FREEPDB1;
CREATE USER HR IDENTIFIED BY hr;
ALTER USER HR ACCOUNT UNLOCK;
GRANT CONNECT, RESOURCE, CREATE VIEW TO HR;
ALTER USER HR QUOTA UNLIMITED ON USERS;
EXIT;
```

If you hit `ORA-01950: user HR has insufficient quota on tablespace USERS`, apply:

```sql
ALTER USER HR QUOTA UNLIMITED ON USERS;
```

SQL*Plus paste mistakes:
- Never paste `SQL>` prompt text.
- Run statements separately.
- If malformed input gets stuck: `Ctrl+C`, then `CLEAR BUFFER`, then retry cleanly.

Back up schema file before edits:

```bash
cp /home/azureuser/OwnChatDB.sql /home/azureuser/OwnChatDB.sql.bak
```

About `OwnChatDB.sql` cleanup:
- Destructive `DELETE`/`DROP`/reset statements execute data changes.
- `ON DELETE CASCADE` inside table DDL is a safe FK behavior, not a standalone wipe command.

Import schema:

```bash
docker exec -i oracle-free sqlplus hr/hr@//localhost:1521/FREEPDB1 < /home/azureuser/OwnChatDB.sql
```

Do not rerun schema blindly once tables already exist.

If trigger compile warnings appear, inspect instead of ignoring:

```bash
docker exec -it oracle-free sqlplus hr/hr@//localhost:1521/FREEPDB1
```

```sql
SELECT object_name, object_type, status
FROM user_objects
WHERE status <> 'VALID';

SELECT name, type, line, position, text
FROM user_errors
ORDER BY name, sequence;
```

---

### 7) Java server build/run on VM

Use modified `ServerL.java` with `FREEPDB1` JDBC URL.

```bash
cd ~
javac -cp ojdbc17.jar:. ServerL.java clientSession.java
ls -lh ServerL.class clientHandler.class StatusPanel.class clientSession.class
```

Quick test start (`nohup`) :

```bash
nohup env DB_USER='hr' DB_PASSWORD='hr' java -cp ojdbc17.jar:. ServerL > ~/ownchat.log 2>&1 < /dev/null &
```

> [!WARNING]
> The quick test command is for validation only. For production, avoid putting real secrets in shell history.

Verify running server:

```bash
ps -ef | grep '[S]erverL'
sudo ss -ltnp | grep 4567
tail -n 50 ~/ownchat.log
```

If duplicate processes exist or you get `java.net.BindException: Address already in use`:

```bash
pgrep -af ServerL
kill PID_FROM_OUTPUT
# Safe cleanup if needed:
pkill -f 'java -cp ojdbc17.jar:. ServerL'
```

If Oracle is still booting, Java server may log connection errors; wait for Oracle healthy/ready and retry.

---

### 8) systemd service (recommended for continuous server)

Use **either** systemd **or** manual `nohup`, not both.

Protected env file:

```bash
sudo tee /etc/ownchat.env > /dev/null <<'EOF'
DB_USER=hr
DB_PASSWORD=hr
EOF
sudo chmod 600 /etc/ownchat.env
```

Service:

```bash
sudo tee /etc/systemd/system/ownchat.service > /dev/null <<'EOF'
[Unit]
Description=OwnChat Java Server
After=docker.service network-online.target
Wants=docker.service network-online.target

[Service]
Type=simple
User=azureuser
WorkingDirectory=/home/azureuser
EnvironmentFile=/etc/ownchat.env
ExecStartPre=/bin/sh -c 'until nc -z 127.0.0.1 1521; do sleep 2; done'
ExecStart=/usr/bin/java -cp /home/azureuser/ojdbc17.jar:. ServerL
Restart=on-failure
RestartSec=5
StandardOutput=append:/home/azureuser/ownchat.log
StandardError=append:/home/azureuser/ownchat.log

[Install]
WantedBy=multi-user.target
EOF
```

Enable/start/check:

```bash
sudo systemctl daemon-reload
sudo systemctl enable --now ownchat
sudo systemctl status ownchat --no-pager
journalctl -u ownchat -n 100 --no-pager
```

---

### 9) Client setup and cross-network test

- In OwnChat client, set server IP to `YOUR_PUBLIC_IP`.
- Use port `4567` if requested.
- From a different machine/network, never use `localhost` for server address.

Windows port test:

```powershell
Test-NetConnection YOUR_PUBLIC_IP -Port 4567
```

Expected output includes:

```text
TcpTestSucceeded : True
```

---

### 10) VM lifecycle, backup/retrieval, and restart checks

Stop/deallocate VM when not needed; start when needed. Dynamic IP may change unless static/reserved.

After VM start:

```powershell
ssh YOUR_ADMIN_USERNAME@YOUR_PUBLIC_IP
```

```bash
docker ps
docker inspect --format='{{.State.Health.Status}}' oracle-free
sudo systemctl status ownchat --no-pager
sudo ss -ltnp | grep 4567
```

If systemd is enabled, do not launch extra manual Java copy.

Disk/reboot checks:

```bash
df -h
# optional reboot hint check
[ -f /var/run/reboot-required ] && cat /var/run/reboot-required
```

`OwnChatDB.sql` is schema setup, not live chat data backup. For live data, export HR schema (Data Pump), then copy out:

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
  'hr/hr@//localhost:1521/FREEPDB1' \
  DIRECTORY=OWNCHAT_DUMP \
  DUMPFILE=ownchat_hr.dmp \
  LOGFILE=ownchat_hr.log \
  SCHEMAS=HR

docker cp oracle-free:/opt/oracle/dpump/ownchat_hr.dmp /home/azureuser/ownchat_hr.dmp
docker cp oracle-free:/opt/oracle/dpump/ownchat_hr.log /home/azureuser/ownchat_hr.log
```

Download to Windows:

```powershell
scp YOUR_ADMIN_USERNAME@YOUR_PUBLIC_IP:/home/azureuser/ownchat_hr.dmp "$env:USERPROFILE\Downloads\"
scp YOUR_ADMIN_USERNAME@YOUR_PUBLIC_IP:/home/azureuser/ownchat_hr.log "$env:USERPROFILE\Downloads\"
```

---

### 11) Troubleshooting quick reference

| Symptom | Likely cause | What to check/fix |
|---|---|---|
| Clients cannot connect to `4567` | NSG/host firewall rule missing | Allow inbound TCP `4567` in NSG, check `ufw`, verify `ss -ltnp | grep 4567` |
| Oracle `1521` visible publicly | Incorrect port mapping/rule | Keep Docker mapping `127.0.0.1:1521:1521`; remove public NSG 1521 rule |
| Oracle connection refused | DB still starting/container down | `docker ps`, `docker logs -f oracle-free`, wait for `DATABASE IS READY TO USE!` |
| JDBC URL errors or login failures | XE URL used instead of Free PDB | Use `jdbc:oracle:thin:@//localhost:1521/FREEPDB1` in cloud `ServerL.java` |
| `ORA-01950` on HR operations | No USERS quota | `ALTER USER HR QUOTA UNLIMITED ON USERS;` |
| SQL*Plus command chaos after paste | Prompt text/malformed paste | Never paste `SQL>` prompts; `Ctrl+C`, `CLEAR BUFFER`, re-run cleanly |
| Trigger warning during import | Invalid trigger body | Query `USER_OBJECTS`/`USER_ERRORS`, fix invalid objects before proceeding |
| `Address already in use` on server start | Duplicate `ServerL` process | `pgrep -af ServerL`, stop old PID, keep exactly one server instance |
| Client worked before, now fails after VM restart | Public IP changed | Update client server IP or assign static/reserved public IP |
| Random failures due low space | Disk nearly full | `df -h`, clean Docker/images/logs or increase disk |

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
