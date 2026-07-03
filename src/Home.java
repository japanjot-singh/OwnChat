import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
public class Home extends JPanel implements ActionListener {
    JButton jbc,jbn,jbl;
    ImageIcon ii,ic,it;
    Home(){
        setLayout(new GridLayout(1,4,0,25));

        ImageIcon io3= new ImageIcon("ChatGPT Image Jun 4, 2026, 12_12_34 AM.png");
        Image ig3=io3.getImage();
        Image is3= ig3.getScaledInstance(240,120,Image.SCALE_SMOOTH);
        it= new ImageIcon(is3);
        jbl= new JButton(it);
        jbl.setPreferredSize(new Dimension(250,150));
        jbl.addActionListener(this);
        add(jbl);

        ImageIcon io= new ImageIcon("ChatGPT Image Jun 3, 2026, 01_32_36 PM.png");
        Image ig= io.getImage();
        Image is= ig.getScaledInstance(240,120,Image.SCALE_SMOOTH);
        ii= new ImageIcon(is);
        jbc= new JButton(ii);
        jbc.setPreferredSize(new Dimension(250,150));
        jbc.setFont(new Font("nf",Font.BOLD,26));
        add(jbc);
        jbc.addActionListener(this);

        ImageIcon io2= new ImageIcon("ChatGPT Image Jun 3, 2026, 01_43_51 PM.png");
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
            ca.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }
}