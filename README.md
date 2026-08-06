# OwnChat 🔐🌎
## For those who yearn for freedom 🏳️
A Decentralized Chat Application for Desktops (client->Server->client) using Java Swing and Sockets and Oracle Database.\

## Features📲

- It is Decentralized, every chat of you or your organization stays within the server on your machine
- Create user accounts
- Log in and log out
- Save contacts
- View saved contacts
- Open a chat window and send messages through the server
- Change the Swing look and feel from Settings
- Multiple users can chat at the same time

## Database Schema

<img width="1536" height="1024" alt="ChatGPT Image Aug 6, 2026, 01_18_21 AM" src="https://github.com/user-attachments/assets/f76ffa2f-7d2e-4a45-81f5-5a1569dd7851" />

## At a Glance

### Welcome Screen
<img width="1779" height="1113" alt="Screenshot 2026-08-04 222154" src="https://github.com/user-attachments/assets/925c6ac1-4e85-449f-9c2a-6c57965e6bde" />

### Home
<img width="888" height="557" alt="Screenshot 2026-08-06 005143" src="https://github.com/user-attachments/assets/cc25ec9e-9bf1-449e-ac47-378bba18196f" />

### Settings
<img width="888" height="554" alt="setting" src="https://github.com/user-attachments/assets/b28391cd-8e10-4e3f-8b09-03e30438abcc" />

### Create Account
<img width="730" height="733" alt="Screenshot 2026-08-04 222452" src="https://github.com/user-attachments/assets/5a5c3cec-9ea0-4784-870a-077952aaddbb" />

### Log In
<img width="364" height="368" alt="image" src="https://github.com/user-attachments/assets/c9961bb6-702b-4265-b588-3ce41079c0d6" />

### Add Contacts
<img width="727" height="729" alt="Screenshot 2026-08-05 225450" src="https://github.com/user-attachments/assets/0265bdf3-7be9-4204-9eeb-fee45571dab5" />

### Contacts List
<img width="889" height="560" alt="image" src="https://github.com/user-attachments/assets/72b53cc5-adf0-4551-ab85-86d0f4fdef87" />

### Chat Window
<img width="1278" height="765" alt="Screenshot 2026-08-04 223313" src="https://github.com/user-attachments/assets/10211cc3-83bb-4d5f-acd3-daa26da57380" />

<img width="1275" height="767" alt="Screenshot 2026-08-04 223335" src="https://github.com/user-attachments/assets/2bc801a8-256b-444f-97e1-ddc38caab34d" />

### Server

<img width="365" height="372" alt="image" src="https://github.com/user-attachments/assets/5e16606b-294f-40d4-97d8-53dd3555947d" />

## Project Structure

| File | Purpose |
|---|---|
| `Welcome.java` | Splash screen with a loading bar; entry point (`main`) that launches `App`. Also handles auto-logout on exit. |
| `App.java` | Main application window; hosts a `JTabbedPane` with Home, Settings, and Set Server tabs. |
| `Home.java` | Landing tab — Log In, Create Account, and Chat Now actions; checks whether the user has contacts before opening chat/contacts list. |
| `log.java` | Login form; sends credentials to the server and applies the user's saved theme on success. |
| `CrAc.java` | Create-account form. |
| `SetServerIP.java` | Lets the user configure the server's IP address (stored statically for all socket calls). |
| `Settings.java` | Log out, change username, and change theme (Metal/Dark/Light). |
| `Add_Contacts.java` | Form to add a new contact for the logged-in user. |
| `Contacts_List.java` | Displays the user's contacts in a `JTable`; lets the user check status and start a chat. |
| `ChatWindow.java` | Two-pane chat UI (send/receive) that connects to the server and streams messages in a background thread. |
| `clientSession.java` | Static holder for the current client-side session (username, logged-in flag). |
| `ServerL.java` | Server entry point: GUI for DB credentials + status panel, `ServerSocket` accept loop, and `clientHandler` — the per-connection request router and all DB operations. |
| `OwnChatDB.sql` | Oracle schema: `USER_DETAILS`, `SETTINGS`, `LOG_STATUS`, `CONTACTS`, `CHAT_HISTORY`, plus triggers that seed default settings/log status when a user is created. |

## How To Setup

### (1) Build Database:

-Download Oracle
-Run the OwnchatDB.sql File (excluding the statements of drop table; are only for dropping in case there is an issue or you want to alter)

### (2) Setup the Server

--Download JDK
--Downlaod IntelliJ and set it up
--Download Ojdbc driver for oracle
--Run the ServerL.java file
--The Window will open then input the database username and password to connect

### (3) Setting up the Client(OwnChat App)

--Download and then extract the file
--Drive Link: https://drive.google.com/drive/folders/1rVhccv8CuPzHBdt1-s6ONpIPHXCPwwUF?usp=drive_link
--Run the exe file
--Go to set Server then input the IP Address of your Sever
--Create account
--Log in
--Go to Chat Now and add contacts 
--Then again click chat now
--Contacts list will arrive 
--Select the contact then hit connect if the user is online the chat window will open
--Chat Freely






