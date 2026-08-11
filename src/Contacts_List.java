import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Vector;

public class Contacts_List extends JFrame implements ActionListener {
    public JButton jbc, back;
    public JTable jtc;
    public DefaultTableModel model;
    public Object tableValue;
    public Object Oname;
    Socket socket;
    PrintWriter pw;
    BufferedReader br;
     static boolean onWindow=false;

    public Contacts_List() {
        Container c = this.getContentPane();
        c.setLayout(new BorderLayout());

        jbc = new JButton("Connect");
        jbc.addActionListener(this);
        back = new JButton("Back");
        back.addActionListener(this);

        // Setup clean JTable Model
        Vector<String> cols = new Vector<>();
        cols.add("Name");

        model = new DefaultTableModel(cols, 0);
        jtc = new JTable(model);

        c.add(new JScrollPane(jtc), BorderLayout.CENTER);
        c.add(back, BorderLayout.WEST);
        c.add(jbc, BorderLayout.EAST);


        new Thread(this::fetchContactsData).start();
        jtc.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me){
                int r=jtc.rowAtPoint(me.getPoint());
                if(r != -1 ){
                    Oname=jtc.getValueAt(r,0);
                }
            }
        });
    }
    public void startChat(Object tableValue,Object Oname,boolean onWindow){
        if(Oname!= null){
            String targetName=toString().valueOf(Oname);
            checkUser(targetName);
        }
        else{
            JOptionPane.showMessageDialog(this,"Please Select a contact first");
        }
    }
    public void checkUser(String tn){
        try{
            socket = new Socket(SetServerIP.ip, 4567);
            System.out.println("Connected");
            pw = new PrintWriter(socket.getOutputStream(), true);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw.println("checkUser");
            pw.println(tn);
            String res= br.readLine();
            if("online".equals(res)){
                ChatWindow ch= new ChatWindow(tn);
                ch.setSize(1200,750);
                ch.setTitle(clientSession.getUsername()+" Chatting with "+tn);
                onWindow=true;
                online_fill(onWindow);
                ch.setVisible(true);
            }
            else {
                JOptionPane.showMessageDialog(this,"user is offline. Try another time");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchContactsData() {
        try (Socket socket = new Socket(SetServerIP.ip, 4567);
             PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            pw.println("Fetch contacts");
            pw.println(clientSession.getUsername());

            String name;
            while ((name = br.readLine()) != null) {
                if ("END".equals(name)) break;

                String finalName = name;
                SwingUtilities.invokeLater(() -> model.addRow(new Object[]{finalName}));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == back) {
            this.dispose();
        }
        if(ae.getSource() == jbc){
            new Thread(()->{
                startChat(tableValue,Oname,onWindow);
            }).start();
        }
    }
    public static void online_fill(Boolean onFlag){
        try{
            Socket socket = new Socket(SetServerIP.ip, 4567);
            PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw.println("Change online status");
            if(onFlag == true){
                pw.println("ONLINE");
                pw.println(clientSession.getUsername());
            }
            else{
                pw.println("OFFLINE");
                pw.println(clientSession.getUsername());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}