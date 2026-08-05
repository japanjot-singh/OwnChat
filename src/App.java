import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class App extends JFrame{
    JTabbedPane jtb;

    App(){
        Container c= this.getContentPane();
        jtb= new JTabbedPane();
        jtb.addTab("Home",new Home());
        jtb.addTab("Settings",new Settings());
        jtb.addTab("Set Server",new SetServerIP());

        jtb.setSelectedIndex(0);
        c.add(jtb);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Welcome.autoLog();
            }
        });

    }
}