# OwnChat
A Decentralized Chat Application for Desktops (client->Server->client) using Java Swing and Sockets and Oracle Database.

## Features

- It is Decentralized, every chat of you or your organization stays within the server on your machine
- Create user accounts
- Log in and log out
- Save contacts
- View saved contacts
- Open a chat window and send messages through the server
- Change the Swing look and feel from Settings
- Multiple users can chat at the same time

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

## Database Schema

<img width="1536" height="1024" alt="DB schema" src="https://github.com/user-attachments/assets/12605fb1-5ff9-4084-b897-36e2038e270c" />

## At a Glance

### Welcome Screen
<img width="1779" height="1113" alt="Screenshot 2026-08-04 222154" src="https://github.com/user-attachments/assets/925c6ac1-4e85-449f-9c2a-6c57965e6bde" />

### Home
<img width="1775" height="1114" alt="Screenshot 2026-08-04 222237" src="https://github.com/user-attachments/assets/7511d2c5-5771-455c-908a-e32888cd8387" />

### Settings
<img width="888" height="553" alt="image" src="https://github.com/user-attachments/assets/ff05b1ee-d4e6-4cc1-8c63-0dac2ca3761e" />

### Create Account
<img width="730" height="733" alt="Screenshot 2026-08-04 222452" src="https://github.com/user-attachments/assets/5a5c3cec-9ea0-4784-870a-077952aaddbb" />

### Log In
<img width="732" height="734" alt="Screenshot 2026-08-04 222546" src="https://github.com/user-attachments/assets/80501e35-cf2c-4348-9cdf-23a3e91d51e6" />

### Add Contacts
<img width="727" height="736" alt="Screenshot 2026-08-04 222654" src="https://github.com/user-attachments/assets/df8a4495-8903-40e5-822f-49508c6cdc9d" />

### Contacts List
<img width="1775" height="1114" alt="Screenshot 2026-08-04 222742" src="https://github.com/user-attachments/assets/9b70713d-a8d9-4ce8-9f18-c820bf2c4c18" />

### Chat Window
<img width="1278" height="765" alt="Screenshot 2026-08-04 223313" src="https://github.com/user-attachments/assets/10211cc3-83bb-4d5f-acd3-daa26da57380" />

<img width="1275" height="767" alt="Screenshot 2026-08-04 223335" src="https://github.com/user-attachments/assets/2bc801a8-256b-444f-97e1-ddc38caab34d" />

## How to Setup
- Copy code
- Make server any machine you want then run ServerL.java on that Machine
- Also save and compile other files
- In ItelliJ go to:
- File -> 








