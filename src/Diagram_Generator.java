import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Vector;

/**
 * Created by sachin on 11/2/2017.
 */
public class Diagram_Generator extends JFrame implements ActionListener {

    public  int CANVAS_WIDTH ;
    public  int CANVAS_HEIGHT ;
    public  Color CANVAS_BACKGROUND;
    private DrawCanvas canvas; // The custom drawing canvas (an inner class extends JPanel)
    JButton save,close;

    ResultSet rst,rst1,rst2;
    Statement stmt;
    String driver  = "com.mysql.jdbc.Driver";
    String url  = "jdbc:mysql://127.0.0.1:3306/";
    String user="root";
    String password  = "sachin";
    Connection con  =null;
        class DrawCanvas extends JPanel {

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(CANVAS_BACKGROUND);
            //g.setColor(LINE_COLOR);
           // g.drawLine(30,50,80,80);
            Font m=new Font("Courier New",1,14);
            g.setFont(m);
            //no of classes=no. of rectangles
//g.setColor(Color.LIGHT_GRAY);
            try
            {
                Class.forName(driver);
            }
            catch(ClassNotFoundException ex)
            {
                ex.printStackTrace();
            }

            try {
                con  = DriverManager.getConnection(url,user,password);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            } catch (SQLException e) {
                e.printStackTrace();
            }
    int x_corr=30;//start position x
            int y_corr=30;//start position y
            String sql_ex="select extends,implements from co_to_cl.class where type=0";
            try {
                rst1=stmt.executeQuery(sql_ex);
            } catch (SQLException e) {
                e.printStackTrace();
            }


            //getting all class tables attributes
            String sql="select * from co_to_cl.class ";
            try {
                 rst2=stmt.executeQuery(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
int count=0;
            try {
                while(rst2.next()){
                    //new stmt everytime
                    count++;
                    Statement stm= con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    int id= Integer.parseInt(rst2.getString(1));
                    String name=rst2.getString(2);
                    int type=Integer.parseInt(rst2.getString(3));
                    String extend=rst2.getString(4);
                    String implement=rst2.getString(5);

                  //variables
                    Vector<String>variables=new Vector<>();//method vector for a particular class
                    String sql_variable="select name from co_to_cl.variables where id="+id;//for getting variables of a particular class
                    rst1=stm.executeQuery(sql_variable);
                    while(rst1.next())
                    {
                        variables.add(rst1.getString(1));
                    }
                    System.out.println(variables.size());
                    //methods
                    Vector<String>methods=new Vector<>();//method vector for a particular class
                    String sql_method="select name from co_to_cl.functions where id="+id;//for getting methods of a particular class
                    rst1=stm.executeQuery(sql_method);
                    while(rst1.next())
                    {
                        methods.add(rst1.getString(1));
                    }
                    System.out.println(methods.size());

                    //   System.out.println(methods+"--"+methods.size());
                    //now we got methods and variable of particular class
                    //lets draw
                    int old_y=y_corr;
                    int old_x=x_corr;
                    int max_width=0;
                    y_corr+=10;
                    //making Class name at somewhat middle so add spaces
                    String class_n="    "+name+"    ";
                    if(class_n.length()>max_width)
                        max_width=class_n.length();
                    //drawing interface and class
                    if(type==1)//means interface
                    {
                        String represent="      <<interface>>    ";
                        if(represent.length()>max_width)
                            max_width=represent.length();
                        g.drawString("      <<interface>>    ",x_corr+4,y_corr);
                        y_corr+=20;
                        g.drawString(class_n,x_corr+4,y_corr);
                    }
                    else
                    g.drawString(class_n,x_corr+4,y_corr);
                    int class_y=y_corr;
                    y_corr+=20;
                    //now placing variables
                    if(variables.size()>0)
                    {
                        for (int i = 0; i <variables.size() ; i++) {
                            String temp=variables.elementAt(i);
                            g.drawString(temp,x_corr+4,y_corr);
                            y_corr+=14;
                            if(temp.length()>max_width)
                            {
                                max_width=temp.length();
                            }

                        }
                    }
                    y_corr+=10;
                    int variable_y=y_corr;

                    //now placing methods
                    y_corr+=20;
                    if(methods.size()>0)
                    {
                        for (int i = 0; i <methods.size() ; i++) {
                            String temp=methods.elementAt(i);
                            g.drawString(temp,x_corr+4,y_corr);
                            y_corr+=14;
                            if(temp.length()>max_width)
                            {
                                max_width=temp.length();
                            }
                            //System.out.println(temp);
                        }
                    }
                    int height=y_corr-old_y;
                    int width=(int) (max_width*8.3);
                   /* if(width>CANVAS_WIDTH)//problem but concept is this
                    {
                        CANVAS_WIDTH+=1700;//bcoz screen width is around 1600-1700
                        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
                    }*/
                        g.drawRect(old_x,old_y, (int) (max_width*8.3),height);
                        //g.fillRect(old_x,old_y, (int) (max_width*8.3),height);
                        g.drawLine(old_x,class_y+4, (int) (old_x+max_width*8.3),class_y+4);
                    g.drawLine(old_x,variable_y, (int) (old_x+max_width*8.3),variable_y);//line separating variable



                    //now moving x_cor,old_y
                    x_corr+=max_width+(int) (max_width*8.3);y_corr=old_y;
                    System.out.println("---------------");
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
    Diagram_Generator()
    {
        super("CLASS DIAGRAM");
        CANVAS_WIDTH = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth()+6500;//till now its manual,in future we will make it dynamics
        CANVAS_HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight()-60;
        CANVAS_BACKGROUND = Color.lightGray;
        canvas = new DrawCanvas();
        setMaximumSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);//not closing through window close option
        JPanel btnPanel = new JPanel(new FlowLayout());
        close = new JButton("CLOSE");
        close.addActionListener(this);
        btnPanel.add(close,FlowLayout.LEFT);
        save = new JButton("SAVE");
        save.addActionListener(this);
        btnPanel.add(save,FlowLayout.LEFT);
        setSize(CANVAS_WIDTH,CANVAS_HEIGHT);

        // Add both panels to this JFrame's content-pane
        //Container cp = getContentPane();
        JPanel cp=new JPanel();
        cp.setLayout(new BorderLayout());
        cp.add(canvas, BorderLayout.CENTER);
        cp.add(btnPanel, BorderLayout.SOUTH);


        JScrollPane jsp=new JScrollPane(cp,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(jsp);
      //  setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Handle the CLOSE button
        setTitle("CLASS DIAGRAM");
       // pack();           // pack all the components in the JFrame
        setVisible(true); // show it
        requestFocus();   // set the focus to JFrame to receive KeyEvent

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==save)
        {
            BufferedImage b1=null ;
            System.out.println("hii");


            Dimension  d1=Toolkit.getDefaultToolkit().getScreenSize();
            Robot r1= null;
            try {
                r1 = new Robot();
            } catch (AWTException e1) {
                e1.printStackTrace();
            }
            if (r1 != null) {
                b1=r1.createScreenCapture(new Rectangle(d1)) ;
            }
            JFileChooser jjjj=new JFileChooser();
                FileFilter filter = new FileNameExtensionFilter("image file","jpg");
                jjjj.setFileFilter(filter);
                jjjj.showSaveDialog(new JTable());
                File f1=jjjj.getSelectedFile();
                String ssss=f1.getAbsolutePath();
            System.out.println(ssss);
            try {

                if (b1 != null) {
                    ImageIO.write(b1,"jpg",new File(ssss+".jpg"));
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
        else {
            int n=JOptionPane.showConfirmDialog(this,"DO YOU WANT TO CLOSE","CLOSE",JOptionPane.YES_NO_OPTION);
            try {
                new Data_Store("delete it");
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            if(n==JOptionPane.YES_OPTION)
            {
                int more=JOptionPane.showConfirmDialog(this,"MORE CLASS DIAGRAM","MORE",JOptionPane.YES_NO_OPTION);
                if(more==JOptionPane.YES_OPTION)
                {
                    try {
                        new Splash_Class();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }

                this.dispose();
            }
            //delete database now of no use ...as when application starts it is already created

        }

    }



   /* public void paint(Graphics g)
    {
        g.setColor(Color.red);
        g.drawString("Matrix",20,150);
    }
*/
    public static void main(String[] args) {
        new Diagram_Generator();
    }


}
