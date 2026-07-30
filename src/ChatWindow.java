import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.UIManager.*;
public class ChatWindow extends JFrame {
    JPanel jps,jpr;
    JLabel jls,jlr;
    JTextArea jtas,jtar;
    JButton jbs,jbr;
    ChatWindow(String othName){
        Container c= this.getContentPane();
        c.setLayout(new GridLayout(1,2));
        jps= new JPanel(new BorderLayout());
        jpr= new JPanel(new BorderLayout());

        jls= new JLabel("You");
        jls.setBorder(BorderFactory.createLineBorder(Color.GRAY,4));
        jlr= new JLabel(othName);//add real name later
        jlr.setBorder(BorderFactory.createLineBorder(Color.GRAY,4));

        jtas= new JTextArea(10,10);
        jtas.setBorder(BorderFactory.createLineBorder(Color.GRAY,5));
        jtar= new JTextArea(10,10);
        jtar.setBorder(BorderFactory.createLineBorder(Color.GRAY,5));

        jbs= new JButton("Send");
        jbs.setBorder(BorderFactory.createLineBorder(Color.GRAY,4));
        jbr= new JButton("Recieve");
        jbr.setBorder(BorderFactory.createLineBorder(Color.GRAY,4));

        jps.add(BorderLayout.NORTH,jls);
        jps.add(BorderLayout.CENTER,jtas);
        jps.add(BorderLayout.SOUTH,jbs);

        jpr.add(BorderLayout.NORTH,jlr);
        jpr.add(BorderLayout.CENTER,jtar);
        jpr.add(BorderLayout.SOUTH,jbr);

        c.add(jps);
        c.add(jpr);
    }
    /*public static void main(String args[]){
        try{
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            ChatWindow ch= new ChatWindow();
            ch.setSize(500,500);
            ch.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }*/
}