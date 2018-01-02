import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Created by sachin on 10/30/2017.
 */
import java.sql.*;
public class Data_Store {
    ResultSet rst;
    Statement stmt;
    String driver  = "com.mysql.jdbc.Driver";
    String url  = "jdbc:mysql://127.0.0.1:3306/";
    String user="root";
    String password  = "sachin";
    Connection con  =null;
    public Data_Store() throws SQLException {//this will be called when application is started by the user
        try
        {
            Class.forName(driver);
        }
        catch(ClassNotFoundException ex)
        {
            ex.printStackTrace();
        }

        con  = DriverManager.getConnection(url,user,password);

        stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

        String sql="create schema if not exists co_to_cl;";
        stmt.execute(sql);
        stmt.execute("use co_to_cl");
        //used this query from mysql gui client
        //creating class table
        String sql_class="CREATE TABLE if not exists `class` (\n" + "  `id` int(11) NOT NULL AUTO_INCREMENT,\n"
                + "  `name` varchar(45) NOT NULL,\n" + "  `type` int(11) DEFAULT NULL,\n" +
                "  `extends` varchar(45) DEFAULT NULL,\n" + "  `implements` varchar(45) DEFAULT NULL,\n" +
                "  PRIMARY KEY (`id`),\n" + "  UNIQUE KEY `id_UNIQUE` (`id`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8";
        stmt.execute(sql_class);
            //creating functions table
        String sql_functions="CREATE TABLE if not exists`functions` (\n" + "  `fid` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `name` varchar(300) DEFAULT NULL,\n" + "  `id` int(11) DEFAULT NULL,\n" + "  PRIMARY KEY (`fid`),\n" +
                "  UNIQUE KEY `fid_UNIQUE` (`fid`)\n" + ") ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8";
        stmt.execute(sql_functions);
        //creating variables table
        String sql_variables="CREATE TABLE if not exists`variables` (\n" + "  `vid` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `name` varchar(300) NOT NULL,\n" + "  `id` int(11) DEFAULT NULL,\n" + "  PRIMARY KEY (`vid`),\n" +
                "  UNIQUE KEY `vid_UNIQUE` (`vid`)\n" + ") ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8";
        stmt.execute(sql_variables);

    }
    public Data_Store(Vector<String> classes, Vector<String> methods, Vector<String> variables) throws SQLException {

        try
        {
            Class.forName(driver);
        }
        catch(ClassNotFoundException ex)
        {
            ex.printStackTrace();
        }

        con  = DriverManager.getConnection(url,user,password);

        stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        //adding in class database

        Vector<String >class_name=new Vector<String>();
        Vector<String>interface_name=new Vector<>();

        for (int i=0;i<classes.size();i++)
        {
            String s=classes.elementAt(i);
            System.out.println(s);
            if(s==null)break;//don't know why extra null element comes
            StringTokenizer st=new StringTokenizer(s," ");
            String name=st.nextToken();
            class_name.add(name);
            int type= Integer.parseInt(st.nextToken());
            String extend=null;
            String implement=null;
           if(st.hasMoreTokens())
           {
               String cmp=st.nextToken();
                if(cmp.equals("extends"))
                {
                    extend=st.nextToken();
                    if(st.hasMoreTokens()&&st.nextToken().equals("implements"))
                    {
                        implement=st.nextToken();
                    }
                }
               if(cmp.equals("implements"))
                {
                    implement=st.nextToken();
                }
           }
            else{
             //  String sql="insert into class(name,type)values("+name+","+type+","+extend+","+implement+")";

           }
            stmt.execute("use co_to_cl");
            String sql="insert into co_to_cl.class(name,type,extends,implements)values('"+name+"',"+type+",'"+extend+"','"+implement+"')";
          stmt.executeUpdate(sql);
            System.out.println("name=" + name + " type=" + type + " extend=" + extend + " implements=" + implement);
        }


            //adding in methods table
        for (int i = 0; i <methods.size() ; i++) {

            String t=methods.elementAt(i);
            if(t==null)break;//don't know why extra null element comes
            StringTokenizer st=new StringTokenizer(t,"=");
            String fun=st.nextToken();
            String _class=st.nextToken();
            String name= (String) t.subSequence(0,t.indexOf("class=")-1);
            String sql_="select id from co_to_cl.class where name='"+_class+"'";
           rst=stmt.executeQuery(sql_);
            rst.next();
            int id= (rst.getInt(1));
           String sql="insert into co_to_cl.functions(name,id)values('"+name+"',"+id+")";
            stmt.executeUpdate(sql);
            System.out.println("fun is " + name + "  class is " + _class+"  id is "+id);
        }
        //adding in Variables tables
        for (int i = 0; i <variables.size() ; i++) {

            String t=variables.elementAt(i);
            if(t==null)break;//don't know why extra null element comes
            StringTokenizer st=new StringTokenizer(t,"=");
            String var=st.nextToken();
            System.out.println(var);
            String _class=st.nextToken();
            System.out.println(_class);
            String name= (String) t.subSequence(0,t.indexOf("class=")-1);
            String sql_="select id from co_to_cl.class where name='"+_class+"'";
            rst=stmt.executeQuery(sql_);
            rst.next();
            int id= (rst.getInt(1));
            String sql="insert into co_to_cl.variables(name,id)values('"+name+"',"+id+")";
            stmt.executeUpdate(sql);
            System.out.println("variables is " + name + "  class is " + _class+"  id is "+id);
        }
       // new Diagram_Generator();

    }


    public Data_Store(String s) throws SQLException {

        try
        {
            Class.forName(driver);
        }
        catch(ClassNotFoundException ex)
        {
            ex.printStackTrace();
        }

        con  = DriverManager.getConnection(url,user,password);

        stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);

        String sql="drop schema  co_to_cl;";
        stmt.execute(sql);
    }
}
