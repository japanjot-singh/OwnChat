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
    boolean found=false;
    clientHandler(Socket socket,String url,String user,String pass){
        this.socket=socket;
        this.url=url;
        this.user=user;
        this.pass=pass;
    }

    public void run(){
        try{
            System.out.println("Connected");
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String username = br.readLine();
            String password = br.readLine();

            Connection con = DriverManager.getConnection(url, user, pass);
            String query = "SELECT USERNAME FROM USER_DETAILS WHERE USERNAME=?";
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            out = new PrintWriter(socket.getOutputStream(), true); // initialize before loop

            while(rs.next()){
                if(username.equals(rs.getString("username"))){
                    out.println("Exists");
                    found = true;
                }
            }

            if(!found){
                String sQuery = "INSERT INTO USER_DETAILS (USERNAME,PASSWORD) VALUES(?,?)";
                PreparedStatement pstmt2 = con.prepareStatement(sQuery);
                pstmt2.setString(1, username);
                pstmt2.setString(2, password);
                pstmt2.executeUpdate();
                out.println("Saved");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}