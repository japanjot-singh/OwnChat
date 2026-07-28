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
    public void table_User_details(String username,String password,String query,BufferedReader br,PrintWriter pw){

    }

    public void run(){
        boolean found=false;
        boolean lf=false;
        try{
            System.out.println("Connected");
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String action=br.readLine();
            Connection con = DriverManager.getConnection(url, user, pass);

            if("Create Account".equals(action)){
                String username = br.readLine();
                String password = br.readLine();

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
                }

            }
            if ("Log In".equals(action)) {
                String usernameL = br.readLine();
                String passwordL= br.readLine();
                String queryL = "SELECT USERNAME,USER_PASSWORD FROM USER_DETAILS WHERE USERNAME=? AND USER_PASSWORD=?";
                PreparedStatement pstmtl = con.prepareStatement(queryL);
                pstmtl.setString(1, usernameL);
                pstmtl.setString(2,passwordL);
                ResultSet rsl = pstmtl.executeQuery();
                while(rsl.next()){
                    String query="UPDATE LOG_STATUS SET STATUS=? WHERE USERNAME=?";
                    PreparedStatement pstmt=con.prepareStatement(query);
                    pstmt.setString(1,"LOGGED IN");
                    pstmt.setString(2,usernameL);
                    pstmt.executeUpdate();
                    out.println("found");
                    clientSession.login(usernameL);
                    lf=true;

                }
                if(!lf){
                    out.println("wrong");
                }
            }
            if("SLogOut".equals(action)){
                String queryu="UPDATE LOG_STATUS SET STATUS='LOGGED OUT' WHERE USERNAME=?";
                PreparedStatement pstmtu=con.prepareStatement(queryu);
                pstmtu.setString(1,clientSession.getUsername());
                pstmtu.executeUpdate();
            }
            if("Contacts".equals(action)){
                String cl="SELECT STATUS FROM LOG_STATUS WHERE USERNAME=?";
                PreparedStatement pstmtcl=con.prepareStatement(cl);
                String cuser=br.readLine();
                pstmtcl.setString(1,cuser);
                ResultSet rscl= pstmtcl.executeQuery();
                if(rscl.next()){
                    if("LOGGED IN".equals(rscl.getString("STATUS"))){
                        out.println("getting");
                        String queryc="SELECT CONTACT_NAME,CONTACT_IP FROM CONTACTS WHERE USERNAME=?";
                        PreparedStatement pstmtc=con.prepareStatement(queryc);
                        pstmtc.setString(1,cuser);
                        ResultSet rs= pstmtc.executeQuery();
                        if(rs.next()){
                            out.println("Has contacts");
                        }
                        else{
                            out.println("No contacts");
                        }
                    }
                    else{
                        out.println("not logged in");
                    }
                }

            }
            if("Add Contacts".equals(action)){
                String querya="INSERT INTO CONTACTS(USERNAME,CONTACT_NAME,CONTACT_IP) VALUES(?,?,?)";
                PreparedStatement psa= con.prepareStatement(querya);
                psa.setString(1, br.readLine());
                psa.setString(2, br.readLine());
                psa.setString(3,br.readLine());
                int n=psa.executeUpdate();
                if(n == 1){
                    out.println("Added");
                }
                else{
                    out.println("Not Added");
                }

            }
            if("Fetch contacts".equals(action)){
                String queryf="SELECT CONTACT_NAME,CONTACT_IP FROM CONTACTS WHERE USERNAME=?";
                PreparedStatement psf= con.prepareStatement(queryf);
                psf.setString(1,br.readLine());
                ResultSet rs=psf.executeQuery();
                while (rs.next()){
                    out.println(rs.getString("CONTACT_NAME"));
                    out.println(rs.getString("CONTACT_IP"));
                }
                out.println("END");
            }
            if("StartChat".equals(action)){

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
