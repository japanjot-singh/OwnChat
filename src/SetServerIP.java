import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
public class SetServerIP extends JPanel implements ActionListener {
    JLabel jl;
    JTextField jtf;
    JButton jb;
    static String ip;
    SetServerIP(){
        setLayout(new FlowLayout());
        jl= new JLabel("Set Server's IP Address");
        jtf= new JTextField(30);
        jb= new JButton("SET");
        jb.addActionListener(this);
        add(jl);
        add(jtf);
        add(jb);
    }
    public void actionPerformed(ActionEvent ae){
        if (ae.getSource() ==jb){
            new Thread (()->{
                ip=jtf.getText();
                JOptionPane.showMessageDialog(this,"Set Successfully");
            }).start();
        }
    }
}