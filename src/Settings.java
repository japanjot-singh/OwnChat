import javax.swing.event.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class Settings extends JPanel implements ActionListener {
    JLabel[] labels;
    JButton jb1,jb2,jb3,jb4,jb5;
    Socket socket;
    Settings(){
        setLayout(new GridLayout(5,2));
        labels = new JLabel[5];
        labels[0]= new JLabel("Log Out");
        jb1= new JButton("Log out");
        jb1.addActionListener(this);
        labels[1]= new JLabel("Change username");
        jb2= new JButton("Change username");
        labels[2]= new JLabel("Change theme");
        jb3= new JButton("Change theme");
        labels[3]= new JLabel("Change theme");
        jb4= new JButton("Change theme");
        labels[4]= new JLabel("Change theme");
        jb5= new JButton("Change theme");
        for(JLabel label:labels){
            label.setFont(new Font("nf", Font.BOLD, 22));
            label.setBackground(Color.CYAN);
            label.setOpaque(true);
        }

        add(labels[0]);
        add(jb1);
        add(labels[1]);
        add(jb2);
        jb2.addActionListener(this);
        add(labels[2]);
        add(jb3);
        add(labels[3]);
        add(jb4);
        add(labels[4]);
        add(jb5);

    }
    public void getCon(String message){
        try{
            socket=new Socket("localhost",4567);
            System.out.println("Connected");
            PrintWriter pw=new PrintWriter(socket.getOutputStream(),true);
            BufferedReader br= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw.println(message);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        catch (IOException ie){
            ie.printStackTrace();
        }
    }
    public void actionPerformed(ActionEvent ae){
        if(ae.getSource() == jb1){
            getCon("SLogOut");
        }
        if(ae.getSource() == jb2){
            String nu=JOptionPane.showInputDialog(this,"Enter new username");
            CUser("Change username",nu);
        }
    }
    public void CUser(String message,String nu){
        try{
            socket=new Socket("localhost",4567);
            System.out.println("Connected");
            PrintWriter pw=new PrintWriter(socket.getOutputStream(),true);
            BufferedReader br= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw.println(message);
            pw.println(clientSession.getUsername());
            String res= br.readLine();
            if("logined".equals(res)){
                pw.println("change");
                pw.println(nu);
                String r=br.readLine();
                if("Changed".equals(r)){
                    JOptionPane.showMessageDialog(this,"Username Changed Successfully");
                }
                if("username already exists".equals(r)){
                    JOptionPane.showMessageDialog(this,"Username Already Exists");
                }
            }
            if("not login".equals(res)){
                JOptionPane.showMessageDialog(this,"Login first");
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        catch (IOException ie){
            ie.printStackTrace();
        }

    }
}
