import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.net.*;
public class Contacts extends JFrame implements ActionListener {
    JButton jba;
    Socket socket;
    PrintWriter pw;
    BufferedReader br;
    Contacts(){
        Container c= this.getContentPane();
        c.setLayout(new GridLayout(2,2));
        jba= new JButton("Add Contacts");
        c.add(jba);
        jba.addActionListener(this);

    }
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == jba) {
            try {
                socket = new Socket("localhost", 4567);
                System.out.println("Connected");
                pw = new PrintWriter(socket.getOutputStream(), true);
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));


            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}

