import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Home extends JPanel implements ActionListener {
    JButton jbc,jbn,jbl;
    ImageIcon ii,ic,it;
    Socket socket;
    PrintWriter pw;
    BufferedReader br;
    Home(){
        setLayout(new GridLayout(1,4,0,25));

        ImageIcon io3= new ImageIcon("log in.png");
        Image ig3=io3.getImage();
        Image is3= ig3.getScaledInstance(240,120,Image.SCALE_SMOOTH);
        it= new ImageIcon(is3);
        jbl= new JButton(it);
        jbl.setPreferredSize(new Dimension(250,150));
        jbl.addActionListener(this);
        add(jbl);

        ImageIcon io= new ImageIcon("crac.png");
        Image ig= io.getImage();
        Image is= ig.getScaledInstance(240,120,Image.SCALE_SMOOTH);
        ii= new ImageIcon(is);
        jbc= new JButton(ii);
        jbc.setPreferredSize(new Dimension(250,150));
        jbc.setFont(new Font("nf",Font.BOLD,26));
        add(jbc);
        jbc.addActionListener(this);

        ImageIcon io2= new ImageIcon("chat now.png");
        Image ig2=io2.getImage();
        Image is2= ig2.getScaledInstance(240,120,Image.SCALE_SMOOTH);
        ic= new ImageIcon(is2);
        jbn= new JButton(ic);
        jbn.setPreferredSize(new Dimension(250,150));
        add(jbn);
        jbn.addActionListener(this);
    }
    public void actionPerformed(ActionEvent ae){
        if(ae.getSource() == jbc){
            CrAc ca= new CrAc();
            ca.setTitle("Create Account");
            ca.setSize(500,500);
            ca.setVisible(true);
            ca.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
        if(ae.getSource() == jbl){
            log fl= new log();
            fl.setTitle("Log In");
            fl.setSize(500,500);
            fl.setVisible(true);
            fl.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }
        if(ae.getSource() == jbn){
            new Thread(()->{
                openList();
            }).start();
        }
    }
    public void openList(){
        try{
            socket=new Socket("localhost",4567);
            System.out.println("Connected");
            pw=new PrintWriter(socket.getOutputStream(),true);
            br= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw.println("Contacts");
            pw.println(clientSession.getUsername());
            String resp=br.readLine();
            if("getting".equals(resp)){
                String res= br.readLine();
                if("Has contacts".equals(res)){
                    openClist(true);
                }
                if("No contacts".equals(res)){
                    Add_Contacts cl= new Add_Contacts();
                    cl.setTitle("Add Contacts");
                    cl.setSize(500,500);
                    cl.setVisible(true);
                    cl.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                }
            }
            if("not logged in".equals(resp)){
                JOptionPane.showMessageDialog(this,"Login first");
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        catch (IOException ie){
            ie.printStackTrace();
        }
    }
    public static void openClist(boolean value){
        Contacts_List clf= new Contacts_List();
        clf.setTitle("Contacts");
        clf.setSize(1200,750);
        clf.setVisible(value);
        clf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}