import java.io.IOException;
import java.net.*;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.*;
public class ServerL{
    public static void main(String args[]){
        try{
            ServerSocket ss= new ServerSocket(4567);
            while (true){
                Socket clientSocket= ss.accept();
                clientHandler handler= new clientHandler(clientSocket,"jdbc:oracle:thin:@localhost:1521:xe","hr","hr");
                Thread thread= new Thread(handler);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class clientHandler implements Runnable {
    String url, user, pass;
    Socket socket;
    String euser, epass;
    PrintWriter out;
    Connection con;
    BufferedReader br;
    static Map<String, PrintWriter> chatUsers = new ConcurrentHashMap<>();

    clientHandler(Socket socket, String url, String user, String pass) {
        this.socket = socket;
        this.url = url;
        this.user = user;
        this.pass = pass;
    }

    public void table_User_details(String username, String password, String query, BufferedReader br, PrintWriter pw) {

    }

    public void run() {
        String currentUser=null;
        try {
            System.out.println("Connected");
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String action = br.readLine();
            con = DriverManager.getConnection(url, user, pass);
            if ("chat_connect".equals(action)) {
                    currentUser=br.readLine();
                    String targetUser=br.readLine();
                    if(currentUser != null){
                        chatUsers.put(currentUser,out);
                    }
                    String msgLine;
                    while((msgLine = br.readLine()) != null){
                        PrintWriter targetOut= chatUsers.get(targetUser);
                        if(targetOut != null){
                            targetOut.println(msgLine);
                            String qch="INSERT INTO CHAT_HISTORY(SENDER,MESSAGE,RECIEVER) VALUES(?,?,?)";
                            PreparedStatement psch= con.prepareStatement(qch);
                            psch.setString(1,currentUser);
                            psch.setString(2,msgLine);
                            psch.setString(3,targetUser);
                            psch.executeUpdate();
                        }

                    }
            } else {
                ohterOPS(action);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(currentUser != null){
                chatUsers.remove(currentUser,out);
            }
            try {
                if (con != null) con.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void ohterOPS(String action) throws Exception {
        boolean found = false;
        boolean lf = false;
        if ("Create Account".equals(action)) {
            String username = br.readLine();
            String password = br.readLine();

            String query = "SELECT USERNAME FROM USER_DETAILS WHERE USERNAME=?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                if (username.equals(rs.getString("username"))) {
                    out.println("Exists");
                    found = true;
                }
            }

            if (!found) {
                String sQuery = "INSERT INTO USER_DETAILS (USERNAME,DISPLAY_NAME,USER_PASSWORD) VALUES(?,?,?)";
                PreparedStatement pstmt2 = con.prepareStatement(sQuery);
                pstmt2.setString(1, username);
                pstmt2.setString(2, username);
                pstmt2.setString(3, password);
                pstmt2.executeUpdate();
                out.println("Saved");
            }

        }
        if ("Log In".equals(action)) {
            String usernameL = br.readLine();
            String passwordL = br.readLine();
            String queryL = "SELECT USERNAME,USER_PASSWORD FROM USER_DETAILS WHERE USERNAME=? AND USER_PASSWORD=?";
            PreparedStatement pstmtl = con.prepareStatement(queryL);
            pstmtl.setString(1, usernameL);
            pstmtl.setString(2, passwordL);
            ResultSet rsl = pstmtl.executeQuery();
            while (rsl.next()) {
                String query = "UPDATE LOG_STATUS SET STATUS=? WHERE USERNAME=?";
                PreparedStatement pstmt = con.prepareStatement(query);
                pstmt.setString(1, "LOGGED IN");
                pstmt.setString(2, usernameL);
                pstmt.executeUpdate();
                out.println("found");
                clientSession.login(usernameL);
                lf = true;

            }
            if (!lf) {
                out.println("wrong");
            }
        }
        if ("SLogOut".equals(action)) {
            String queryu = "UPDATE LOG_STATUS SET STATUS='LOGGED OUT' WHERE USERNAME=?";
            String ul=br.readLine();
            PreparedStatement pstmtu = con.prepareStatement(queryu);
            pstmtu.setString(1, ul);
            pstmtu.executeUpdate();
        }
        if ("Contacts".equals(action)) {
            String cl = "SELECT STATUS FROM LOG_STATUS WHERE USERNAME=?";
            PreparedStatement pstmtcl = con.prepareStatement(cl);
            String cuser = br.readLine();
            pstmtcl.setString(1, cuser);
            ResultSet rscl = pstmtcl.executeQuery();
            if (rscl.next()) {
                if ("LOGGED IN".equals(rscl.getString("STATUS"))) {
                    out.println("getting");
                    String queryc = "SELECT CONTACT_NAME,CONTACT_IP FROM CONTACTS WHERE USERNAME=?";
                    PreparedStatement pstmtc = con.prepareStatement(queryc);
                    pstmtc.setString(1, cuser);
                    ResultSet rs = pstmtc.executeQuery();
                    if (rs.next()) {
                        out.println("Has contacts");
                    } else {
                        out.println("No contacts");
                    }
                } else {
                    out.println("not logged in");
                }
            }

        }
        if ("Add Contacts".equals(action)) {
            String querya = "INSERT INTO CONTACTS(USERNAME,CONTACT_NAME) VALUES(?,?";
            PreparedStatement psa = con.prepareStatement(querya);
            psa.setString(1, br.readLine());
            psa.setString(2, br.readLine());
            int n = psa.executeUpdate();
            if (n == 1) {
                out.println("Added");
            } else {
                out.println("Not Added");
            }

        }
        if ("Fetch contacts".equals(action)) {
            String queryf = "SELECT CONTACT_NAME FROM CONTACTS WHERE USERNAME=?";
            PreparedStatement psf = con.prepareStatement(queryf);
            psf.setString(1, br.readLine());
            ResultSet rs = psf.executeQuery();
            while (rs.next()) {
                out.println(rs.getString("CONTACT_NAME"));
            }
            out.println("END");
        }
        if ("Change username".equals(action)) {

            String cl1 = "SELECT STATUS FROM LOG_STATUS WHERE USERNAME=?";
            PreparedStatement pstmtcl1 = con.prepareStatement(cl1);
            String cuser = br.readLine();
            pstmtcl1.setString(1, cuser);
            ResultSet rscl = pstmtcl1.executeQuery();
            if (rscl.next()) {
                out.println("logined");
                if ("change".equals(br.readLine())) {
                    String ch = "SELECT * FROM USER_DETAILS WHERE DISPLAY_NAME=?";
                    PreparedStatement psch = con.prepareStatement(ch);
                    String newName = br.readLine();
                    psch.setString(1, newName);
                    ResultSet rs = psch.executeQuery();
                    if (rs.next()) {
                        out.print("Display name already exists");
                    } else {
                        String cq = "UPDATE USER_DETAILS SET DISPLAY_NAME=? WHERE USERNAME=?";
                        PreparedStatement pscq = con.prepareStatement(cq);
                        pscq.setString(1, newName);
                        pscq.setString(2, cuser);
                        int n = pscq.executeUpdate();
                        System.out.println("n=0");
                        if (n == 1) {
                            System.out.println("n=1");
                            out.println("Changed");
                        }
                    }
                }
            } else {
                out.println("not login");
            }
        }
        if ("Set Theme".equals(action)) {
            String stt = "UPDATE SETTINGS SET SETTING_VALUE=? WHERE SETTING_NAME='THEME' AND USERNAME=?";
            PreparedStatement pstt = con.prepareStatement(stt);
            pstt.setString(1, br.readLine());
            pstt.setString(2, br.readLine());
            pstt.executeUpdate();
        }
        if ("Check Theme".equals(action)) {
            String cht = "SELECT SETTING_VALUE FROM SETTINGS WHERE SETTING_NAME='THEME' AND USERNAME=?";
            PreparedStatement ps = con.prepareStatement(cht);
            ps.setString(1, br.readLine());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                out.println(rs.getString("SETTING_VALUE"));
            }
        }
        if ("checkUser".equals(action)) {
            String cu = "SELECT USERNAME FROM LOG_STATUS WHERE USERNAME=? AND STATUS='LOGGED IN'";
            PreparedStatement pscu = con.prepareStatement(cu);
            String oname = br.readLine();
            pscu.setString(1, oname);
            ResultSet rs = pscu.executeQuery();
            if (rs.next()) {
                out.println("online");
            } else {
                out.println("offline");
            }
        }
    }
}
