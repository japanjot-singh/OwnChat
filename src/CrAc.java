import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.IOException;
import java.net.*;
import java.io.*;
public class CrAc extends JFrame implements ActionListener{
    JLabel jlu,jlp;
    JTextField jtf;
    JPasswordField jpf;
    JButton back,pr;
    JCheckBox kli;
    Socket socket;
    boolean KeepLogged;
    CrAc(){
        Container c=this.getContentPane();
        c.setLayout(new FlowLayout());
        jlu=new JLabel("Enter your username");
        jlu.setFont(new Font("nf",Font.BOLD,22));
        jlu.setBackground(Color.BLUE);
        jlu.setOpaque(true);
        jlp=new JLabel("Enter Secure Password");
        jlp.setFont(new Font("nf",Font.BOLD,22));
        jlp.setBackground(Color.BLUE);
        jlp.setOpaque(true);

        jtf= new JTextField(20);
        jpf= new JPasswordField(20);

        back= new JButton("Back");
        pr= new JButton("Proceed");

        kli=new JCheckBox("Keep me logged in");
        kli.addActionListener(this);
        back.addActionListener(this);
        pr.addActionListener(this);

        c.add(jlu);
        c.add(jtf);
        c.add(jlp);
        c.add(jpf);
        c.add(back);
        c.add(pr);
        c.add(kli);
    }
    public void actionPerformed(ActionEvent ae){
        if(ae.getSource() == back){
            this.dispose();
        }
        if(ae.getSource() == pr ){
            String us=jtf.getText();
            String ps=new String(jpf.getPassword());
            String lg=String.valueOf(kli.isSelected());
            new Thread(()->{
                SendCR(us,ps,lg);
            }).start();
        }
    }
    public void SendCR(String username,String password, String Setting_logged) {
            try{
                socket=new Socket("localhost",4567);
                System.out.println("Connected");
                PrintWriter pw=new PrintWriter(socket.getOutputStream(),true);
                pw.println(username);
                pw.println(password);
                pw.println(Setting_logged);
                BufferedReader br= new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String response=br.readLine();
                if("Exists".equals(response)){
                    JOptionPane.showMessageDialog(this,"Username Already Exists, Change Username");
                }
                else if("Saved".equals(response)){
                    JOptionPane.showMessageDialog(this,"Account Created go back and Chat");
                }
            }
            catch (UnknownHostException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}