import java.io.IOException;
import java.net.*;
import java.io.*;
import java.sql.*;
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
class clientHandler implements Runnable{
    String url,user,pass;
    Socket socket;
    String euser,epass;
    PrintWriter out;
    clientHandler(Socket socket,String url,String user,String pass){
        this.socket=socket;
        this.url=url;
        this.user=user;
        this.pass=pass;
    }

    public void run(){
        boolean found=false;
        try{
            System.out.println("Connected");
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String action=br.readLine();
            Connection con = DriverManager.getConnection(url, user, pass);

            if("Create Account".equals(action)){
                String username = br.readLine();
                String password = br.readLine();
                String Setting_logged=br.readLine();

                String query = "SELECT USERNAME FROM USER_DETAILS WHERE USERNAME=?";
                PreparedStatement pstmt = con.prepareStatement(query);
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();

                while(rs.next()){
                    if(username.equals(rs.getString("username"))){
                        out.println("Exists");
                        found = true;
                    }
                }

                if(!found){
                    String sQuery = "INSERT INTO USER_DETAILS (USERNAME,USER_PASSWORD) VALUES(?,?)";
                    PreparedStatement pstmt2 = con.prepareStatement(sQuery);
                    pstmt2.setString(1, username);
                    pstmt2.setString(2, password);
                    pstmt2.executeUpdate();
                    out.println("Saved");
                    if("true".equals(Setting_logged)){
                        String query2="UPDATE SETTINGS SET SETTING_VALUE='TRUE' WHERE SETTING_NAME='KEEP LOGGED IN' AND USERNAME=?";
                        PreparedStatement pstmt3=con.prepareStatement(query2);
                        pstmt3.setString(1,username);
                        pstmt3.executeUpdate();
                    }
                }

            }
            if ("Log In".equals(action)) {
                String usernameL = br.readLine();
                String queryL = "SELECT SETTING_VALUE FROM SETTINGS WHERE USERNAME=? AND SETTING_NAME='KEEP LOGGED IN'";
                PreparedStatement pstmtl = con.prepareStatement(queryL);
                pstmtl.setString(1, usernameL);
                ResultSet rsl = pstmtl.executeQuery();
                while (rsl.next()) {
                    if ("TRUE".equals(rsl.getString(1))) {
                        String querySt = "INSERT INTO LOG_STATUS(USERNAME,STATUS) VALUES(?,'LOGGED IN')";
                        PreparedStatement pstmtST = con.prepareStatement(querySt);
                        pstmtST.setString(1, usernameL);
                        pstmtST.executeUpdate();
                        out.println("Keep logged in");
                    } else {
                        out.println("require password");
                        String psw = br.readLine();
                        String queryNL = "SELECT USER_PASSWORD FROM USER_DETAILS WHERE USERNAME=?";
                        PreparedStatement pstmtNL = con.prepareStatement(queryNL);
                        pstmtNL.setString(1, usernameL);
                        ResultSet rsn = pstmtNL.executeQuery();
                        while (rsn.next()) {
                            if (psw.equals(rsn.getString("USER_PASSWORD"))) {
                                String queryns = "INSERT INTO LOG_STATUS(USERNAME,STATUS) VALUES(?,'LOGGED IN')";
                                PreparedStatement pstmtns = con.prepareStatement(queryns);
                                pstmtns.setString(1, usernameL);
                                pstmtns.executeUpdate();
                                out.println("Logged in now");
                            } else {
                                out.println("wrong password");
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}