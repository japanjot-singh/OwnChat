import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
public class log extends JFrame implements ActionListener{
    JLabel jl,jlp;
    JTextField ju;
    JPasswordField jpf;
    JButton jbn,back;
    Socket socket;

    log(){
        Container c=this.getContentPane();
        setLayout(new FlowLayout());

        jl= new JLabel("Enter Username");
        ju= new JTextField(20);
        jlp= new JLabel("Enter Passoword");
        jpf= new JPasswordField(20);
        jbn= new JButton("Next");
        back= new JButton("Back");

        jbn.addActionListener(this);
        back.addActionListener(this);

        c.add(jl);
        c.add(ju);
        c.add(jlp);
        c.add(jpf);
        c.add(back);
        c.add(jbn);
    }
    public void actionPerformed(ActionEvent ae){
        if(ae.getSource() == jbn){
            String username=ju.getText();
            String password=jpf.getPassword().toString();
            new Thread(()->{
                sendLG(username,password);
            }).start();
        }
        if(ae.getSource() == back){
            this.dispose();
        }
    }
    public void sendLG(String username,String password){
        try{
            if(ClientSession.createdUsername != null && !ClientSession.createdUsername.equals(username)){
                JOptionPane.showMessageDialog(this,"Please enter the same username by which you created your account");
                return;}
            socket=new Socket("localhost",4567);
            System.out.println("Connected");
            PrintWriter pw=new PrintWriter(socket.getOutputStream(),true);
            BufferedReader br= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw.println("Log In");
            pw.println(username);
            pw.println(password);
            String response=br.readLine();
            if("found".equals(response)){
                JOptionPane.showMessageDialog(this,"Logged in Successfully, Go back and Chat");
            }
            if("wrong".equals(response)){
                    JOptionPane.showMessageDialog(this,"Wrong Password or Username");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}