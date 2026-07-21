import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.Vector;

public class Contacts_List extends JFrame implements ActionListener{
    public static Socket socket;
    public static PrintWriter pw;
    public static BufferedReader br;
    public JButton jbc,back;
    public JTable jtc;
    public String contact;
    Object tableValue="";


    Contacts_List(){
        try{
            Container c= this.getContentPane();
            c.setLayout( new BorderLayout());
            jbc= new JButton("Connect");

            Vector<Vector> data= new Vector<>();
            Vector<String> cols= new Vector<>();
            cols.add("Name");
            cols.add("IP Address");
            socket=new Socket("localhost",4567);
            pw=new PrintWriter(socket.getOutputStream(),true);
            br= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw.println("Fetch contacts");
            pw.println(clientSession.getUsername());
            String name;
            String IP;

            while((name=br.readLine()) != null){
                if("END".equals(name)){
                    break;
                }
                Vector<String> row=new Vector<>();
                row.add(name);
                IP=br.readLine();
                row.add(IP);
                data.add(row);
            }
            jtc= new JTable(data,cols);
            c.add(new JScrollPane(jtc),BorderLayout.CENTER);
            jtc.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent me){
                    int r=jtc.rowAtPoint(me.getPoint());
                    int c=jtc.columnAtPoint(me.getPoint());
                    if(r != -1 && c != -1){
                        tableValue=jtc.getValueAt(r,c);
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void actionPerformed(ActionEvent ae){

    }

}
