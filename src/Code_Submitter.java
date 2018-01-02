import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by sachin on 10/15/2017.
 */
public class Code_Submitter extends JFrame implements ActionListener {
    JButton single,multiple,close;
    JPanel p1,p3;
    Code_Submitter() throws SQLException {
       // new Data_Store();

        setVisible(true);
        Dimension d= Toolkit.getDefaultToolkit().getScreenSize();
        //setMaximumSize( d.getWidth(),d.getHeight());
        setExtendedState(0);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);//not closing through window close option
        // setBackground(Color.PINK);
        setBounds(600, 150, 1000, 800);
        setLayout(new GridLayout(3,2));
        //getContentPane().setBackground(Color.lightGray);
        p1=new JPanel();

        p3=new JPanel();
        single=new JButton("SINGLE FILE");
       // Rectangle e = single.getBounds();
       // single.setBounds((int)e.getX(),(int)e.getY()+100,(int)e.getWidth(),(int)e.getHeight());
        multiple=new JButton("MULTIPLE FILES");
        single.addActionListener(this);
        multiple.addActionListener(this);
        p1.add(single);p1.add(multiple);
        add(p1);


        close=new JButton("CLOSE");
        // save=new JButton("SAVE");
        close.addActionListener(this);

        p3.add(close);

        add(p3);
    }

    public void paint(Graphics g) { //Applying image
        super.paint(g);
        Dimension d = this.getSize();
        // linux....  g.drawImage(new ImageIcon("/root/IdeaProjects/CLOUD/src/client/cloud.jpeg").getImage(), 0, 20, d.width, d.height, null);
        g.drawImage(new ImageIcon("G:\\Dropbox\\EASY_HOME\\src\\back.jpg").getImage(), 0, 20, d.width, d.height, null);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==single)
        {
            JFileChooser fileChooser=new JFileChooser();
            FileFilter filter = new FileNameExtensionFilter("JAVA FILES","java");
            fileChooser.setFileFilter(filter);

            fileChooser.showOpenDialog(this);

            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file=fileChooser.getSelectedFile();
                String path=file.getAbsolutePath();
                System.out.println(file.getParent());
                System.out.println(path);
                try {
                    new Parser(file);
                    //new Diagram_Generator();
                } catch (IOException | SQLException e1) {
                    e1.printStackTrace();
                }
                int k=JOptionPane.showConfirmDialog(this,"DO YOU WANT TO SEE DIAGRAM","FILE UPLOAD SUCCESSFUL",JOptionPane.YES_NO_OPTION);
                if(k==JOptionPane.YES_OPTION)
                {
                    new Diagram_Generator();
                    dispose();

                }

            }
            else {
                System.out.println("No Selection ");
            }

        }
        if(e.getSource()==multiple)
        {
            JFileChooser fileChooser=new JFileChooser();
            FileFilter filter = new FileNameExtensionFilter("JAVA FILES","java");
            fileChooser.setFileFilter(filter);
            fileChooser.setMultiSelectionEnabled(true);
           // fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(true);
            fileChooser.showOpenDialog(this);

            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                System.out.println("getCurrentDirectory(): "
                        +  fileChooser.getCurrentDirectory());
                for (File  f:fileChooser.getSelectedFiles()
                     ) {
                    System.out.println("getSelectedFile() : "
                            +  f.getAbsolutePath());
                    try {
                        new Parser(f);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }

                int k=JOptionPane.showConfirmDialog(this,"DO YOU WANT TO SEE DIAGRAM","FILES ARE UPLOADED SUCCESSFULLY",JOptionPane.YES_NO_OPTION);
                if(k==JOptionPane.YES_OPTION)
                {
                    new Diagram_Generator();
                    dispose();

                }
            }
            else {
                System.out.println("No Selection ");
            }
        }


        if(e.getSource()==close)
        {
            int n=JOptionPane.showConfirmDialog(this,"DO YOU WANT TO CLOSE","CLOSE",JOptionPane.YES_NO_OPTION);
            if(n==JOptionPane.YES_OPTION)
            {
                try {
                    new Data_Store("Delete it");
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
                this.dispose();
            }
        }
    }

    public static void main(String[] args) {
        //new Code_Submitter();
    }
}
