import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.UIManager.*;
public class ChatWindow extends JFrame implements ActionListener {
    JPanel jps, jpr;
    JLabel jls, jlr;
    JTextArea jtas, jtar;
    JButton jbs, jbr, back, cont;
    BufferedReader br;
    PrintWriter pw;
    Socket socket;
    String targetUser;

    ChatWindow(String othName) {
        Container c = this.getContentPane();
        c.setLayout(new GridLayout(1, 2));
        this.targetUser=othName;
        jps = new JPanel(new BorderLayout());
        jpr = new JPanel(new BorderLayout());

        jls = new JLabel("You");
        jls.setBorder(BorderFactory.createLineBorder(Color.GRAY, 4));
        jlr = new JLabel(othName);//add real name later
        jlr.setBorder(BorderFactory.createLineBorder(Color.GRAY, 4));

        jtas = new JTextArea(10, 10);
        jtas.setBorder(BorderFactory.createLineBorder(Color.GRAY, 5));
        jtar = new JTextArea(10, 10);
        jtar.setBorder(BorderFactory.createLineBorder(Color.GRAY, 5));

        jbs = new JButton("Send");
        jbs.setBorder(BorderFactory.createLineBorder(Color.GRAY, 4));
        jbs.addActionListener(this);
        jbr = new JButton("Recieve");
        jbr.setBorder(BorderFactory.createLineBorder(Color.GRAY, 4));
        jbr.addActionListener(this);
        back = new JButton("Back");
        back.setBorder(BorderFactory.createLineBorder(Color.GRAY, 4));
        back.addActionListener(this);
        cont = new JButton("Contacts");
        cont.setBorder(BorderFactory.createLineBorder(Color.GRAY, 4));
        cont.addActionListener(this);


        jps.add(BorderLayout.NORTH, jls);
        jps.add(BorderLayout.CENTER, jtas);
        jps.add(BorderLayout.SOUTH, jbs);
        jps.add(BorderLayout.WEST, back);

        jpr.add(BorderLayout.NORTH, jlr);
        jpr.add(BorderLayout.CENTER, jtar);
        jpr.add(BorderLayout.SOUTH, jbr);
        jpr.add(BorderLayout.EAST, cont);

        c.add(jps);
        c.add(jpr);

        connectToserver();

    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == back) {
            this.dispose();
        }
        if (ae.getSource() == cont) {
            Home.openClist(true);
            this.dispose();
        }
        if (ae.getSource() == jbs) {
            String msg = jtas.getText();
            pw.println(msg);
        }
        if(ae.getSource() == jbr){

        }

    }

    public void connectToserver() {
        try {
            socket = new Socket("localhost", 4567);
            System.out.println("Connected");
            pw = new PrintWriter(socket.getOutputStream(), true);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw.println("chat_connect");
            pw.println(clientSession.getUsername());
            pw.println(targetUser);
            startRecieving();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void startRecieving() throws IOException{
        new Thread(()->{
            try{
                String msg;
                while((msg=br.readLine()) != null) {
                    jtar.setText(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }
}