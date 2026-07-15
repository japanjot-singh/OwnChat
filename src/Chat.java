import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.IOException;
import java.net.*;
import java.io.*;
public class Chat extends JFrame{
    Label jls,jlc,jlsent,jlrecieve;
    JButton jb1,jb2;
    JTextArea jta1,jta2;
    Chat(){
        Container c= this.getContentPane();
        c.setLayout(new GridLayout(4,2,10,10));
        jls= new Label("You");
        jls.setForeground(Color.BLUE);
        jls.setFont(new Font("nf", Font.BOLD, 40));
        jlsent= new Label("Sending");
        jlsent.setFont(new Font("nf", Font.ITALIC, 40));
        jlsent.setBackground(Color.CYAN);
        jta1= new JTextArea(10,10);
        jb1= new JButton("Send");

        jlc= new Label("other");
        jlc.setForeground(Color.BLUE);
        jlc.setFont(new Font("nf", Font.BOLD, 40));
        jlrecieve= new Label("recieving");
        jlrecieve.setFont(new Font("nf", Font.ITALIC, 40));
        jlrecieve.setBackground(Color.YELLOW);

        jta2= new JTextArea(10,10);
        jb2= new JButton("Recieve");

        c.add(jls);
        c.add(jlc);
        c.add(jlsent);
        c.add(jlrecieve);
        c.add(jta1);
        c.add(jta2);
        c.add(jb1);
        c.add(jb2);

    }

}