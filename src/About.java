import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.IOException;
import java.net.*;
import java.io.*;
public class About extends JPanel {
    JLabel jl1,jl2,jl3,jl4;
    ImageIcon i1,i2;
    JLabel jli1,jli2;
    JPanel jp1;
    About(){
        setLayout(new GridLayout(1,3));
        Font font= new Font("about",Font.BOLD,18);
        jl1= new JLabel("Official Licensed Software");
        jl1.setFont(font);
        jl2= new JLabel("Developed by: Japanjot Singh");
        jl2.setFont(font);
        jl3= new JLabel("Email: japanjotsingh90@outlook.com");
        jl3.setFont(font);
        jl4= new JLabel("Github:https://github.com/japanjot-singh");
        jl4.setFont(font);
        jp1= new JPanel(new GridLayout(4,1));
        jp1.add(jl1);
        jp1.add(jl2);
        jp1.add(jl3);
        jp1.add(jl4);


        ImageIcon original = new ImageIcon(getClass().getResource("/images/Singhware.png"));
        Image scaled = original.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
        i1 = new ImageIcon(scaled);
        jli1= new JLabel(i1);

        ImageIcon original1 = new ImageIcon(getClass().getResource("/images/Dev photo.png"));
        Image scaled1 = original1.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
        i2 = new ImageIcon(scaled1);
        jli2= new JLabel(i2);

        add(jp1);
        add(jli1);
        add(jli2);

    }
}