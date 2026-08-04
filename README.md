# OwnChat

OwnChat is a Java Swing desktop chat application that uses a TCP socket server and an Oracle database for accounts, login status, settings, and contacts.

## Features

- Create user accounts
- Log in and log out
- Save contacts
- View saved contacts
- Check whether a contact is online
- Open a chat window and send messages through the server
- Change the Swing look and feel from Settings

## Project Structure

```text
src/
  Welcome.java          App start screen
  App.java              Main tabbed application window
  Home.java             Home tab with login/create/contact actions
  CrAc.java             Create account form
  log.java              Login form
  Settings.java         Logout, username, and theme settings
  Add_Contacts.java     Add contact form
  Contacts_List.java    Saved contacts table
  ChatWindow.java       Chat UI and chat socket connection
  clientSession.java    Current client login state
  ServerL.java          Socket server and database operations
  OwnChatDB.sql         Oracle database table/trigger setup
```

The image files in the project root are used by `Welcome.java` and `Home.java`, so keep them beside the application when running from the project folder.

## Requirements

- Java JDK
- Oracle Database
- Oracle JDBC driver, for example `ojdbc.jar`
- Both clients and the server must be able to reach TCP port `4567`

## Database Setup

Run [src/OwnChatDB.sql](src/OwnChatDB.sql) in Oracle before starting the server.

The server currently connects with this hardcoded database configuration in `ServerL.java`:

```java
"jdbc:oracle:thin:@localhost:1521:xe", "hr", "hr"
```

If your Oracle username, password, host, port, or service name is different, update that line before running the server.

## Run Locally

Compile:

```powershell
javac -cp ".;path\to\ojdbc.jar" -d out\classes src\*.java
```

Start the server:

```powershell
java -cp "out\classes;path\to\ojdbc.jar" ServerL
```

Start the client:

```powershell
java -cp out\classes Welcome
```

For local testing on one machine, the current client code works because it connects to:

```java
new Socket("localhost", 4567)
```

## Testing On Another Machine

If Client 2 runs on another machine, `localhost` will not work there. On that machine, `localhost` means the other machine itself, not your server.

For LAN testing:

1. Start Oracle DB on the server machine.
2. Start `ServerL` on the server machine.
3. Find the server machine IPv4 address with:

```powershell
ipconfig
```

4. Replace client socket host values with that IP address, for example:

```java
new Socket("192.168.1.20", 4567)
```

5. Make sure Windows Firewall allows Java or port `4567`.

Client socket calls currently exist in:

```text
Add_Contacts.java
ChatWindow.java
Contacts_List.java
CrAc.java
Home.java
log.java
Settings.java
Welcome.java
```

## Packaging A Client JAR

For a client-only package, include:

```text
Welcome.java
App.java
Home.java
CrAc.java
log.java
Settings.java
Add_Contacts.java
Contacts_List.java
ChatWindow.java
clientSession.java
image files used by the UI
```

Do not include these in the shared client package:

```text
ServerL.java
OwnChatDB.sql
```

In IntelliJ:

1. Open **File > Project Structure > Artifacts**.
2. Click **+**.
3. Choose **JAR > From modules with dependencies**.
4. Set the main class to `Welcome`.
5. Build with **Build > Build Artifacts**.

Run the generated JAR with:

```powershell
java -jar OwnChatClient.jar
```

## Current Notes

- The server must be running before clients can log in, save contacts, or chat.
- A user receives chat messages only after their chat window connects to the server.
- `clientSession` is static, so testing multiple accounts inside the same running client app can overwrite the current local user. Testing from separate machines or separate app processes avoids that client-side issue.
