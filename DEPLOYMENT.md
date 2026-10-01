# Optional Cloud Deployment: Run Your Own Server on a Virtual Machine

> [!IMPORTANT]
> OwnChat remains self-hosted/on-premise first.  
> - For local/LAN usage, run the server locally on your own machine/network.  
> - If you need to chat with users on different networks, host **your own** server + database on a cloud VM (example: Microsoft Azure).  
>
> Cloud deployment has been tested successfully, but there is **no public/shared OwnChat server** provided by this project.  
> Every user/team must create and operate their own VM, Oracle database, and OwnChat server.

## Architecture note

- Client app runs on user devices.
- OwnChat Java server runs on **your** VM.
- Oracle DB runs on the same VM in Docker and should stay private on `127.0.0.1:1521`.
- End-to-end path: `client -> your VM OwnChat server (4567) -> your Oracle database`.

## 1) Azure setup (account, resource group, VM)

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

## 2) Azure networking (NSG + host firewall)

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

## 3) Windows terminal workflow to Ubuntu + Docker install

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

## 4) Oracle registry login vs VM login vs Oracle DB password

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

## 5) Run Oracle Free with persistent storage

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

## 6) Oracle/HR setup and common pitfalls

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

## 7) Schema import and data preservation

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

## 8) OwnChat server compile/start, process conflicts, and systemd

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

## 9) Client setup for cross-network chat

- In client settings, set server IP to `YOUR_PUBLIC_IP`.
- Use port `4567` if client asks for port.
- From another machine/network, do **not** use `localhost`; use your VM public IP.
- Successful cross-network testing means your self-hosted deployment works; it still does not imply a central public OwnChat service.

## 10) VM lifecycle and restart checks

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

## 13) Troubleshooting (common issues)

- **Port `4567` already in use**: duplicate `ServerL` process.
  Check `pgrep -af ServerL`; stop old process before restarting.
- **Oracle port refused**: container still starting.
  Wait for `DATABASE IS READY TO USE!` and `healthy`.
- **JDBC mismatch (`xe` vs `FREEPDB1`)**: update JDBC URL to `jdbc:oracle:thin:@//localhost:1521/FREEPDB1`.
- **`ORA-01950` quota error**: run `ALTER USER HR QUOTA UNLIMITED ON USERS;` in `FREEPDB1`.
- **Client cannot connect from internet**: verify Azure NSG allows inbound TCP `4567`.
- **Disk pressure on VM**: monitor with `df -h`; Oracle images/data need space.
