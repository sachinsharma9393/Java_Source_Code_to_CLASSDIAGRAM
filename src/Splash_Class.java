import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Created by sachin on 9/12/15.
 */
public class Splash_Class extends JFrame {
    JProgressBar jProgressBar;    //Class scope reference variables
    JLabel jLabel;

JButton bm=new JButton();
String ddf;
    Splash_Class() throws SQLException {            //Default Constructor

        jProgressBar = new JProgressBar(0, 300);
        jLabel = new JLabel("CODE TO CLASS DIAGRAM", SwingConstants.CENTER);
        jLabel.setSize(100,50);
        add(jLabel, BorderLayout.NORTH);//Layout Setting
        add(jProgressBar, BorderLayout.SOUTH);
        setUndecorated(true);//For not dispalying three window features of closed,minimize-maximize
        setVisible(true);
        // setBackground(Color.PINK);
        setBounds(700, 150, 600, 600);

        //setBackground(Color.PINK);//for checking

        //Progress Bar
        //creating database when user runs our application
new Data_Store();
        for (int i = 0; i <= 100; i++) {
            jProgressBar.setValue(i*3);
            jProgressBar.setForeground(Color.white);
            jProgressBar.setBorderPainted(true);
            jProgressBar.setBackground(new Color(i + 50, i + 100, i + 50));
            try {
                Thread.sleep(50);

            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (i == 100) { //redirecting at a new frame when progress bar value reaches 100
                dispose();//new Code_Submitter().setVisible(true);
               // new Code_Submitter();
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            new Code_Submitter().setVisible(true);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        }


    }

    public void paint(Graphics g) { //Applying image
        super.paint(g);
        Dimension d = this.getSize();
        // linux....  g.drawImage(new ImageIcon("/root/IdeaProjects/CLOUD/src/client/cloud.jpeg").getImage(), 0, 20, d.width, d.height, null);
        g.drawImage(new ImageIcon("G:\\Dropbox\\EASY_HOME\\src\\class_diagram.jpg").getImage(), 0, 20, d.width, d.height, null);

    }
}
