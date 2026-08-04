import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.io.PrintWriter;
import java.net.Socket;
public class Welcome extends JFrame {
    static JProgressBar jpb;
    ImageIcon ii;
    JLabel jlw,jli;
    Welcome(){
        Container c= this.getContentPane();
        c.setLayout(new BorderLayout());
        jpb= new JProgressBar(0,100);
        jpb.setStringPainted(true);
        jpb.setForeground(Color.BLUE);
        ImageIcon original = new ImageIcon("logo.png");
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
    public static void autoLog(){
        try{
            Socket socket = new Socket("localhost", 4567);
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

            pw.println("SLogOut");
            pw.println(clientSession.getUsername());

            pw.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}