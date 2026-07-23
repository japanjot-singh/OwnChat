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

    public Contacts_List() {
        Container c = this.getContentPane();
        c.setLayout(new BorderLayout());

        jbc = new JButton("Connect");
        back = new JButton("Back");
        back.addActionListener(this);

        // Setup clean JTable Model
        Vector<String> cols = new Vector<>();
        cols.add("Name");
        cols.add("IP Address");

        model = new DefaultTableModel(cols, 0);
        jtc = new JTable(model);

        c.add(new JScrollPane(jtc), BorderLayout.CENTER);
        c.add(back, BorderLayout.WEST);
        c.add(jbc, BorderLayout.EAST);

        new Thread(this::fetchContactsData).start();
    }

    private void fetchContactsData() {
        try (Socket socket = new Socket("localhost", 4567);
             PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            pw.println("Fetch contacts");
            pw.println(clientSession.getUsername());

            String name;
            while ((name = br.readLine()) != null) {
                if ("END".equals(name)) break;
                String ip = br.readLine();
                
                String finalName = name;
                SwingUtilities.invokeLater(() -> model.addRow(new Object[]{finalName, ip}));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == back) {
            this.dispose();
        }
    }
}