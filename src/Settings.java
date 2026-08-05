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
    JButton jb1,jb2;
    Socket socket;
    JRadioButton cm,cd,cl;
    ButtonGroup bg;
    JPanel jp;
    Settings(){
        setLayout(new GridLayout(3,2));
        labels = new JLabel[3];
        labels[0]= new JLabel("Log Out");
        jb1= new JButton("Log out");
        labels[1]= new JLabel("Change username");
        jb2= new JButton("Change username");
        labels[2]= new JLabel("Change theme");
        cm= new JRadioButton("Metal");
        cd= new JRadioButton("Dark");
        cl= new JRadioButton("Light");
        bg= new ButtonGroup();
        bg.add(cm);
        bg.add(cd);
        bg.add(cl);
        cm.addActionListener(this);
        cd.addActionListener(this);
        cl.addActionListener(this);
        jp = new JPanel();
        jp.add(cm);
        jp.add(cd);
        jp.add(cl);

        for(JLabel label:labels){
            label.setFont(new Font("nf", Font.BOLD, 18));
            label.setBorder(BorderFactory.createLineBorder(Color.GRAY,4));
            label.setOpaque(true);
        }

        add(labels[0]);
        add(jb1);
        jb1.addActionListener(this);
        add(labels[1]);
        add(jb2);
        jb2.addActionListener(this);
        add(labels[2]);
        add(jp);


    }
    public void getCon(String message){
        try{
            socket=new Socket("localhost",4567);
            System.out.println("Connected");
            PrintWriter pw=new PrintWriter(socket.getOutputStream(),true);
            BufferedReader br= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw.println(message);
            pw.println(clientSession.getUsername());
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
        if(ae.getSource() == cm){
            applyTheme("METAL");
        }
        if(ae.getSource() == cd){
            applyTheme("DARK");
        }
        if(ae.getSource() == cl){
            applyTheme("LIGHT");
        }
    }

    public static void applyTheme(String theme) {
        try {
            if("LIGHT".equals(theme)){
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                setTheme(theme);
            }
            else if("DARK".equals(theme)){
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                setTheme(theme);
            }
            else if("METAL".equals(theme)){
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                setTheme(theme);
            }

            for(Window w : Window.getWindows()){
                SwingUtilities.updateComponentTreeUI(w);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    public static void setTheme(String value){
        try {
            Socket socket = new Socket("localhost", 4567);
            System.out.println("Connected");
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw.println("Set Theme");
            pw.println(value);
            pw.println(clientSession.getUsername());

        }
        catch (Exception e){
            e.printStackTrace();
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
