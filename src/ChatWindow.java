import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.text.*;

public class ChatWindow extends JFrame implements ActionListener {
    JLabel jl1, jl2;
    JTextPane jtcb;
    JTextArea jtth;
    JScrollPane jspcb, jspth;
    JButton send, back, contacts;
    JPanel jp;
    BufferedReader br;
    PrintWriter pw;
    Socket socket;
    String targetUser;

    ChatWindow(String othName) {
        Container c = this.getContentPane();
        c.setLayout(new BorderLayout(5, 5));
        this.targetUser = othName;

        jl1 = new JLabel("Chat Box");
        jtcb = new JTextPane();
        jtcb.setEditable(false);
        jspcb = new JScrollPane(jtcb);

        c.add(BorderLayout.NORTH, jl1);
        c.add(BorderLayout.CENTER, jspcb);

        back = new JButton("Back");
        back.addActionListener(this);
        contacts = new JButton("Contacts");
        contacts.addActionListener(this);
        c.add(BorderLayout.EAST, back);
        c.add(BorderLayout.WEST, contacts);

        jp = new JPanel(new BorderLayout(3, 3));
        jtth = new JTextArea(5, 10);
        jspth = new JScrollPane(jtth);
        jl2 = new JLabel("Type Here");
        send = new JButton("Send");
        send.addActionListener(this);

        jp.add(BorderLayout.NORTH, jl2);
        jp.add(BorderLayout.CENTER, jspth);
        jp.add(BorderLayout.SOUTH, send);

        c.add(BorderLayout.SOUTH, jp);


        connectToserver();

    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == back) {
            Contacts_List.onWindow = false;
            Contacts_List.online_fill(Contacts_List.onWindow);
            this.dispose();
        }
        if (ae.getSource() == send) {
            String text = jtth.getText();
            appendColoredText(text, Color.green, true);
            pw.println(text);
            jtth.setText("");
        }

    }

    private void appendColoredText(String text, Color color, boolean bold) {
        StyledDocument doc = jtcb.getStyledDocument();
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, color);
        StyleConstants.setBold(style, bold);
        StyleConstants.setFontSize(style, 14);

        try {
            doc.insertString(doc.getLength(), text + "\n\n", style);
            jtcb.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void appendColoredDate(String Date, Color color, boolean bold) {
        StyledDocument doc = jtcb.getStyledDocument();
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, color);
        StyleConstants.setBold(style, bold);
        StyleConstants.setFontSize(style, 14);

        try {
            doc.insertString(doc.getLength(), Date + "\n", style);
            jtcb.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void connectToserver() {
        try {
            socket = new Socket(SetServerIP.ip, 4567);
            System.out.println("Connected");
            pw = new PrintWriter(socket.getOutputStream(), true);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            displayHistory();
            pw.println("chat_connect");
            pw.println(clientSession.getUsername());
            pw.println(targetUser);
            startRecieving();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void displayHistory(){
        try{
            Socket socket = new Socket(SetServerIP.ip, 4567);
            System.out.println("Connected");
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw.println("ChatHistory");
            pw.println(clientSession.getUsername());
            pw.println(targetUser);
            String line;
            while((line= br.readLine()) != null){
                if(line.equals("END")){
                    break;
                }
                String parts[]=line.split("\t",3);
                String sender=parts[0];
                String message=parts[1];
                String date=parts[2];
                if(sender.equals(clientSession.getUsername())){
                    appendColoredText("ME: "+message,Color.green,true);
                    appendColoredDate(date,Color.BLACK,true);
                }
                else{
                    appendColoredText(sender+": "+message,Color.CYAN,true);
                    appendColoredDate(date,Color.BLACK,true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void startRecieving() throws IOException {
        new Thread(() -> {
            try {
                String msg;
                while ((msg = br.readLine()) != null) {
                    if ("The user has not opened the chat window".equals(msg)) {
                        JOptionPane.showMessageDialog(this, "User has not opened the chat window");
                    } else {
                        appendColoredText(msg, Color.CYAN, true);
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }
}