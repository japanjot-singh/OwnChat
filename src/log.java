import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
public class log extends JFrame implements ActionListener{
    JLabel jl;
    JTextField ju;
    JButton jbn,back;
    Socket socket;

    log(){
        Container c=this.getContentPane();
        setLayout(new FlowLayout());

        jl= new JLabel("Enter Username");
        ju= new JTextField(20);
        jbn= new JButton("Next");
        back= new JButton("Back");

        jbn.addActionListener(this);
        back.addActionListener(this);

        c.add(jl);
        c.add(ju);
        c.add(back);
        c.add(jbn);
    }
    public void actionPerformed(ActionEvent ae){
        if(ae.getSource() == jbn){
            String username=ju.getText();
            new Thread(()->{
                sendLG(username);
            }).start();
        }
        if(ae.getSource() == back){
            this.dispose();
        }
    }
    public void sendLG(String username){
        try{
            socket=new Socket("localhost",4567);
            System.out.println("Connected");
            PrintWriter pw=new PrintWriter(socket.getOutputStream(),true);
            BufferedReader br= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw.println("Log In");
            pw.println(username);
            String response=br.readLine();
            if("Keep logged in".equals(response)){
                JOptionPane.showMessageDialog(this,"Logged in Successfully, Go back and Chat");
            }
            if("require password".equals(response)){
                String ps=JOptionPane.showInputDialog(this,"Enter Password");
                pw.println(ps);
                String res=br.readLine();
                if("Logged in now".equals(res)){
                    JOptionPane.showMessageDialog(this,"Logged in Successfully, Go back and Chat");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}