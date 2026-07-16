import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.net.*;
public  class Add_Contacts extends JFrame implements ActionListener {
    JLabel jl1,jl2;
    JTextField jt1,jt2;
    JButton js;
    Socket socket;
    PrintWriter pw;
    BufferedReader br;
    Add_Contacts(){
        Container c= this.getContentPane();
        c.setLayout(new FlowLayout());
        jl1= new JLabel("Enter name");
        jl2= new JLabel("Enter IP Address");
        jt1= new JTextField(20);
        jt2= new JTextField(20);
        js= new JButton("Save");

        c.add(jl1);
        c.add(jl2);
        c.add(jt1);
        c.add(jt2);
        c.add(js);

        js.addActionListener(this);

    }
    public void actionPerformed(ActionEvent ae){
        if(ae.getSource() == js){
            new Thread(() -> saveContact()).start();
        }
    }
    public void saveContact(){
        try {
            socket = new Socket("localhost", 4567);
            socket.setSoTimeout(10000);
            System.out.println("Connected");
            pw = new PrintWriter(socket.getOutputStream(), true);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw.println("Add Contacts");
            String cname=jt1.getText();
            String cip=jt2.getText();
            pw.println(clientSession.getUsername());
            pw.println(cname);
            pw.println(cip);
            String response = br.readLine();
            if("Added".equals(response)){
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this,"Contact added"));
            }
            else{
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this,"Contact was not added"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this,"Could not save contact"));
        }
    }
}
