import javax.swing.*;
import java.awt.*;

public class App extends JFrame {
    JTabbedPane jtb;

    App(){
        Container c= this.getContentPane();
        jtb= new JTabbedPane();
        jtb.addTab("Home",new Home());
        jtb.addTab("Settings",new Settings());
        jtb.setSelectedIndex(0);
        c.add(jtb);
    }
}