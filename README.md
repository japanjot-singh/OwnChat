# OwnChat
A Decentralized Chat Application for Desktops (client->Server->client) using Java Swing and Sockets and Oracle Database.

## Features

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





