import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
public class Welcome extends JFrame{
    static JProgressBar jpb;
    ImageIcon ii;
    JLabel jlw,jli;
    Welcome(){
        Container c= this.getContentPane();
        c.setLayout(new BorderLayout());
        jpb= new JProgressBar(0,100);
        jpb.setStringPainted(true);
        jpb.setForeground(Color.BLUE);
        ImageIcon original = new ImageIcon("ChatGPT Image Jun 2, 2026, 11_41_10 PM.png");
        Image scaled = original.getImage().getScaledInstance(560, 560, Image.SCALE_SMOOTH);
        ii = new ImageIcon(scaled);
        jlw= new JLabel("Welcome !");
        jlw.setForeground(Color.BLUE);
        jlw.setFont(new Font("nf",Font.BOLD,70));
        jlw.setHorizontalAlignment(SwingConstants.CENTER);
        jli= new JLabel(ii);
        c.add(BorderLayout.NORTH,jlw);
        c.add(BorderLayout.SOUTH,jpb);
        c.add(BorderLayout.CENTER,jli);
    }
    public static void main(String args[]){
        Welcome fw= new Welcome();
        fw.setTitle("Own Chat ");
        fw.setSize(1200,750);
        fw.setVisible(true);
        fw.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Load(fw);
    }
    public static void Load(JFrame f){
        for(int i=0;i<=100;i++){
            jpb.setValue(i);
            try{
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(i==100){
                App fa= new App();
                fa.setTitle("Own Chat");
                fa.setSize(1200,750);
                fa.setVisible(true);
                fa.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.dispose();
            }
        }
    }
}