import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatWindow extends JFrame implements ActionListener {
    JLabel jl1,jl2;
    JTextArea jtcb,jtth;
    JScrollPane jspcb,jspth;
    JButton send,back,contacts;
    JPanel jp;
    BufferedReader br;
    PrintWriter pw;
    Socket socket;
    String targetUser;

    ChatWindow(String othName) {
        Container c = this.getContentPane();
        c.setLayout(new BorderLayout(5,5));
        this.targetUser=othName;

        jl1 = new JLabel("Chat Box");
        jtcb= new JTextArea(10,10);
        jspcb= new JScrollPane(jtcb);

        c.add(BorderLayout.NORTH,jl1);
        c.add(BorderLayout.CENTER,jspcb);

        back= new JButton("Back");
        back.addActionListener(this);
        contacts= new JButton("Contacts");
        contacts.addActionListener(this);
        c.add(BorderLayout.EAST,back);
        c.add(BorderLayout.WEST,contacts);

        jp= new JPanel(new BorderLayout(3,3));
        jtth= new JTextArea(5,10);
        jspth= new JScrollPane(jtth);
        jl2= new JLabel("Type Here");
        send= new JButton("Send");
        send.addActionListener(this);

        jp.add(BorderLayout.NORTH,jl2);
        jp.add(BorderLayout.CENTER,jspth);
        jp.add(BorderLayout.SOUTH,send);

        c.add(BorderLayout.SOUTH,jp);



        connectToserver();

    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == back) {
            Contacts_List.onWindow=false;
            Contacts_List.online_fill(Contacts_List.onWindow);
            this.dispose();
        }
        if(ae.getSource() == send){
            String text=jtth.getText();
            jtcb.append(text+"\n");
            pw.println(text);
            jtth.setText("");
        }

    }

    public void connectToserver() {
        try {
            socket = new Socket(SetServerIP.ip, 4567);
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
                    if("The user has not opened the chat window".equals(msg)){
                        JOptionPane.showMessageDialog(this,"User has not opened the chat window");
                    }
                    else {
                        jtcb.append(msg+"\n");
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }
}