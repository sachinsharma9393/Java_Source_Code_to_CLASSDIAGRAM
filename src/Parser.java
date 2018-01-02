import java.io.*;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Created by sachin on 10/14/2017.
 */
public class Parser {

    Vector<String> classes=new Vector<>();
    Vector<String>methods=new Vector<>();
    Vector<String>variables=new Vector<>();
    Vector<String>class_diff=new Vector<>();//used to diff. ,,explained down
    Vector<String>interface_diff=new Vector<>();
    BufferedReader br;
    FileInputStream fis= null;
    InputStreamReader is;
Parser(File file) throws IOException, SQLException {

    try {
        fis = new FileInputStream(file);
    } catch (FileNotFoundException e1) {
        e1.printStackTrace();
    }
    is = new InputStreamReader(fis);
    br = new BufferedReader(is);
    try {
        String s = br.readLine();
        //dealing with imports
        while (s.contains("import")) {

            s = br.readLine();
        }

        String class_name = null;
        Stack<Character> st = new Stack<>();//this stack will be used to diff. btw class var and fun var
        while(s!=null)
        {

                //first is preprocessing ...
            //removing comments
            if(s.contains("//"))//single line comments
            {
                System.out.println("here came single line comment ...REMOVING SINGLE COMMENTS");
                s=s.substring(0,s.indexOf("//"));
            }
            if(s.contains("/*"))//multiline
            {
                System.out.println("here came multiline line comment ...MULTILINE COMMENTS");
                if(s.contains("*/"))
                {
                    s+=" "+s.substring(s.indexOf("*/",s.lastIndexOf(s)));
                }
                else {
                    s=br.readLine();
                    while (!s.contains("*/"))
                    {
                        s=br.readLine();
                    }
                    s=s.substring(s.indexOf("*/",s.lastIndexOf(s)));
                }

            }
            //
           /* Pattern p2_stack=Pattern.compile("\\{");
            Matcher m2_stack=p2_stack.matcher(s);
            int count=m2_stack.groupCount();
            for (int i = 0; i <count ; i++) {
                st.push('{');
            }*/


            /*Pattern p_stack=Pattern.compile("\\}");
            Matcher m_stack=p_stack.matcher(s);
            int co=m_stack.groupCount();

            for (int i = 0; i < co; i++) {
                st.pop();
            }*/
            /*if(s.contains("{"))st.push('{');
            if(s.contains("}"))st.pop();*/
            System.out.println(st.size());
            Boolean nested=false;
            String temp="\"class \"";
            if(s.contains("class ")&&!s.contains(temp)&&!s.contains(";")&&!s.contains("="))//important
            //
            {
               class_name= find_Class(s);
               /* if(st.size()==2){
                   nested=true;
                }//means nested class*/

            }
String test="\"interface \"";
            if(s.contains("interface ")&&!s.contains(test)&&!s.contains(";")&&!s.contains("=")&&!s.contains(","))
            {
                class_name=find_interface(s);
                classes.add(class_name+" 1 ");
            }

            Pattern p=Pattern.compile("[A-Z]\\w*\\s\\w*[,|;]");
            Matcher m=p.matcher(s);
            /*above regular expressions captures
            1. JB b,b1;
            2. JB B;

            not capturing parameterized () bocz arguments can be of any number and type diffcult to do regular expression
            means for 3. capturing till "( " this*/

            if(m.find()&&st.size()==1&&!s.contains("(")&&!s.contains(")")&&!s.contains("String"))
            {
                System.out.println("say_hi.....obj ref. variables ..only initalized...."+s);
                find_ObjReferenceVariable(s,class_name);


            }
            // 3. JB b=new JB(); dealing separately with easy reges bcoz Of IDE PROBLEM
            Pattern p2=Pattern.compile("[A-Z]\\w*\\s\\w*");
            Matcher m2=p2.matcher(s);
            if(m2.find()&&s.contains("new")&&st.size()==1)
            {
                System.out.println("say_hi.....obj ref. variables ..so you used new ...."+s);
                find_ObjReferenceVariableNew(s,class_name);

            }
            if(s.contains("<")&&s.contains(">")&&s.contains(";")&&st.size()==1)
            {
            System.out.println("say_hi....to collections....."+s);

                    //should not be contained in a function/constructor ex Data_Store(Vector<>)
                find_Collections_Variable(s,class_name);

            }


            // System.out.println(s);
            //remove st.size==1 code ,,,nested functions will not come
            if( s.contains("(")&& s.contains(")")&&st.size()==1&&!s.contains(");")&&!s.contains("if")&&!s.contains("for")
                    &&!s.contains("while")&&!s.contains("catch")&&!s.contains(class_name+"(")&&!s.contains("@"))//removing annotation
            {
                //still there can be a possibility of wrong output ex. Data_Store quesry function
                //removing them
                if(!s.equalsIgnoreCase("insert")&&!s.equalsIgnoreCase("update")&&!s.contains("create")&&!s.equalsIgnoreCase("delete"))
                find_method(s,class_name);
            }
            //nested class functions through nested boolean variable
           /* if( s.contains("(")&& s.contains(")")&& nested&&!s.contains(");")&&!s.contains("if")&&!s.contains("for")
                    &&!s.contains("while")&&!s.contains("catch")&&!s.contains(class_name+"(")&&!s.contains("@"))//removing annotation
            {
                //still there can be a possibility of wrong output ex. Data_Store quesry function
                //removing them
                if(!s.equalsIgnoreCase("insert")&&!s.equalsIgnoreCase("update")&&!s.contains("create")&&!s.equalsIgnoreCase("delete"))
                    find_method(s,class_name);
                nested=false;
            }*/
            if((s.contains("byte")||s.contains("short")||s.contains("int")||s.contains("long")||
                    s.contains("float")||s.contains("char")||s.contains("double")||s.contains("String"))&&st.size()==1
                    &&!s.contains("(")&& !s.contains(")")&&!s.contains("interface"))
            {
                find_variable(s,class_name);
            }
            String unique="\"{\"";//this bcoz some project has { this   ....ex class diagram of our parser
            if(s.contains("{")&&!s.contains(unique))
                st.push('{');
            if(s.contains("}"))st.pop();
            System.out.println(st.size());
            s=br.readLine();
        }
//now after parsing we have to differentiate btw primitive and  user defined classes and interfaces
//in sample.java Thread is primitive and Sample is user defined
        //so we kept vector of user defined class and interfaces
        System.out.println("size of"+classes.size());
        //below commented code made is commented bcoz it will be used in future,,,logic is implemented
//        System.out.println(classes.elementAt(1));
        for (int i = 0; i <classes.size() ; i++) {
            String process=classes.elementAt(i);
            if(process==null)break;//don't know why extra null element comes
            //analyzing 4 case
            System.out.println(process);
            int j=process.indexOf(" ");
            System.out.println(j);
            String class_final=process.substring(0,j);
            int inter_or_class= Integer.parseInt(process.substring(j+1,j+2));//only one value
            System.out.println(inter_or_class);
            if (process.contains("extends")&&process.contains("implements"))
            {
                int index=process.indexOf("extends")+"extends".length()+1;//till space
                String temp=process.substring(index);
                System.out.println(temp);
                String temp_class=temp.substring(0,temp.indexOf("implements")).trim();
                System.out.println(temp_class);
                String final_extendclass="";
                if(class_diff.contains(temp_class))//this means class user defined
                final_extendclass+=temp_class;
                //now seeing the same for interface
                index=process.indexOf("implements")+"implements".length()+1;//till space
                String inter=process.substring(index);
                //now we got string of all interface
                //now seeing whether there are multiple interface
                StringTokenizer interface_tokens=new StringTokenizer(inter,",");
                String final_interface="";
                while(interface_tokens.hasMoreTokens())
                {
                    String cmp=interface_tokens.nextToken();
                    if (interface_diff.contains(cmp))
                    {
                        //means primitive interface
                        final_interface+=cmp;
                        final_interface+=",";

                    }
                }
                //now we finally got user defined class and interface
//now we replace existing vector[i] with new formed comprising userer defined class and interface
                classes.remove(i);
                //now again 4 cases of user defined found or not
                if(Objects.equals(final_extendclass, "") && Objects.equals(final_interface, ""))

                    classes.add(i,class_final+" "+inter_or_class);
                if(Objects.equals(final_extendclass, "") && !Objects.equals(final_interface, ""))
                {
                    classes.add(i,class_final+" "+inter_or_class+" implements "+final_interface);
                }
                if(!Objects.equals(final_extendclass, "") && Objects.equals(final_interface, ""))
                {
                    classes.add(i,class_final+" "+inter_or_class+" extends "+final_extendclass);
                }
                if(!Objects.equals(final_extendclass, "") && !Objects.equals(final_interface, ""))
                {
                    classes.add(i,class_final+" "+inter_or_class+" extends "+final_extendclass+" implements "+final_interface);
                }
            }
            if(process.contains("extends")&&!process.contains("implements"))
            {
                int index=process.indexOf("extends")+"extends".length()+1;//till space
                String temp=process.substring(index);
                System.out.println(temp);
                classes.remove(i);
                if(class_diff.contains(temp))
                {
                    classes.add(i,class_final+" "+inter_or_class+" extends "+temp);
                }
                else classes.add(i,class_final+" "+inter_or_class);


            }
            if(!process.contains("extends")&& process.contains("implements"))
            {
                int index=process.indexOf("implements")+"implements".length()+1;//till space
                String inter=process.substring(index);
                StringTokenizer interface_tokens=new StringTokenizer(inter,",");
                String final_interface="";
                while(interface_tokens.hasMoreTokens())
                {
                    String cmp=interface_tokens.nextToken();
                    if (interface_diff.contains(cmp))
                    {
                        //means primitive interface
                        final_interface+=cmp;
                        final_interface+=",";

                    }
                }
                classes.remove(i);
                if(!Objects.equals(final_interface, ""))
                classes.add(i,class_final+" "+inter_or_class+" implements "+final_interface);
else classes.add(i,class_final+" "+inter_or_class);
            }
            if(!process.contains("extends")&&!process.contains("implemets"))
            {
                //no processing required
            }
        }
        System.out.println("\tTHINGS GIVEN TO DATABASE FOR EVERY CLASS (NAME,METHODS,VARIABLES)\n");
        System.out.println("\t\tCLASSES....(USER DEFINED)..\n");
        for (String v:classes  //classes.forEach(System.out::println()) can
             ) {
            System.out.println("\t\t"+v);
        }
        System.out.println("\n\t\tMETHODS......\n");
        for (String v:methods
                ) {
            System.out.println("\t\t"+v);
        }
        System.out.println("\n\t\tVARIABLES......\n");
        for (String v:variables  //variables.forEach(System.out::println);
                ) {
            System.out.println("\t\t"+v);
        }
    }catch (IOException e1) {
        e1.printStackTrace();
    }

       new Data_Store(classes,methods,variables);
}

    private void find_Collections_Variable(String s, String class_name) {
        System.out.println(class_name);
        String use=give_me_index(s);
        System.out.println(use);
        String symbol= String.valueOf(use.charAt(0));
        int i;
        i= Integer.parseInt(use.substring(2));
        System.out.println(i);
        String temp=s.trim();
        String temp_var= (String) temp.subSequence(temp.indexOf(">")+1,temp.indexOf("="));
        String type_a=(String) temp.subSequence(temp.indexOf("<"),temp.indexOf(">")+1);

        String type_b= (String) temp.subSequence(i, temp.indexOf("<"));
        System.out.println(type_a+"  "+type_b);
        String avar,btype,ctype;
        avar=temp_var.trim();
        btype=type_a.trim();
        ctype=type_b.trim();
        String complete_type=type_b+type_a;
        System.out.println(symbol+" "+avar+":"+complete_type+" class="+class_name);
        variables.add(symbol+" "+avar+":"+complete_type+" class="+class_name);

       /* String v=temp_var.trim();
        String type=temp_type.trim();
        System.out.println(type_);
        String complete_type=temp_type+type_;
        System.out.println(symbol+" "+v+":"+type+" class="+class_name);
        variables.add(symbol+" "+v+":"+type+" class="+class_name);*/
    }
    private String  give_me_index(String s)
    {
        String symbol;
        int i=0;
        if(s.contains("public"))
        {
            symbol="+";
            i+=7;

        }
        else if(s.contains("private"))
        {
            symbol="-";
            i+=8;
        }
        else if(s.contains("protected"))
        {
            symbol="#";
            i+=10;
        }
        else {
            symbol="~";
        }
        if(s.contains("static"))
            i+=7;
        if(s.contains("final"))
            i+=6;
        return symbol+" "+i;

    }
    private void find_ObjReferenceVariableNew(String s, String class_name) {
        String use=give_me_index(s);
        String symbol= String.valueOf(use.charAt(0));
        String ind=use.substring(2);
        // int i=Integer.parseInt(String.valueOf(use.charAt(2)));
        int i= Integer.parseInt(ind);
       // int i=Integer.parseInt(String.valueOf(use.charAt(2)));
        System.out.println(i);
       //beacuse of removing unneccessary spaces with a single spaces
        Pattern p=Pattern.compile("\\s+");
        Matcher m=p.matcher(s);
        String str=m.replaceAll(" ");
        String for_new= ((String) str.subSequence(i,str.indexOf("=")+1)).trim();
        System.out.println(for_new);
        Pattern p_= Pattern.compile(" ");
        Matcher m_=p_.matcher(for_new);
        if(m_.find())
        {
            String ty= (String) for_new.subSequence(0, m_.start());
            String variable= (String) for_new.subSequence(m_.start() + 1, for_new.indexOf("="));
            System.out.println(variable);
            System.out.println(ty);

            if(ty.contains("[]"))//1st  array syntax
            {
                ty=ty.substring(0,ty.indexOf('['))+" Array";
            }
            if(variable.contains("[]"))//2nd array syntax
            {
                variable=variable.substring(0,variable.indexOf('['));
            }
            String v=variable.trim();
            String type=ty.trim();
            System.out.println(symbol+" "+v+":"+type+" class="+class_name);
            variables.add(symbol+" "+v+":"+type+" class="+class_name);
        }



    }

    private void find_ObjReferenceVariable(String s, String class_name) {
        String use=give_me_index(s);
        String symbol= String.valueOf(use.charAt(0));
        String ind=use.substring(2);
       // int i=Integer.parseInt(String.valueOf(use.charAt(2)));
        int i= Integer.parseInt(ind);
        System.out.println(i);
        //beacuse of removing unneccessary spaces with a single spaces
        Pattern p=Pattern.compile("\\s+");
        Matcher m=p.matcher(s);
        String str=m.replaceAll(" ");
        String main= (String) str.subSequence(i,str.indexOf(";")+1);
       // System.out.println(main);
        String type= (String) main.subSequence(0, main.lastIndexOf(" "));
        String var= (String) main.subSequence(type.indexOf(type)+type.length(),main.indexOf(";"));//main.indexOf(" ")+1
       // System.out.println(type);
        //System.out.println(var);
        if(type.contains("[]"))
        {
            type=type.substring(0,type.indexOf("["))+" Array";
        }
        if(var.contains("[]"))
        {
            var=var.substring(0,var.indexOf("["));
        }
        String new_var=var.trim();
        String new_type=type.trim();
        System.out.println(symbol+" "+new_var+":"+new_type);
        variables.add(symbol+" "+new_var+":"+new_type+" class="+class_name);

    }
    private String find_interface(String s) {
       // System.out.println(s.length())
        System.out.println("-------coming to interface------" + s);
        int index_start=s.lastIndexOf("interface");
        int index_end=index_start+"interface".length()+1;
       // System.out.println(index_start+"..."+index_end);
        String interf= (String) s.substring(index_end,s.length());
       // System.out.println(interf);
        String name=null;
        if(interf.contains("{"))
        {
            name=interf.substring(0,interf.indexOf('{'));
         //   System.out.println(name.trim());
            name=name.trim();
        }
        else name=interf.trim();
        interface_diff.add(name);
        return name;

    }

    private void find_variable(String s, String class_name) {
        String with_var;
        String symbol;
        if(s.contains("public"))
        {
            symbol="+";
        }
        else if(s.contains("private"))
        {
            symbol="-";
        }
        else if(s.contains("protected"))
        {
            symbol="#";
        }
        else {
            symbol="~";
        }
        if(s.contains("byte"))
        {
            if(s.contains("[]")&&s.contains("="))  //array type of variable  array object creation
            {
                if(s.indexOf(']')+1==s.indexOf('=')||s.indexOf(']')+1==s.indexOf('='))//int m[]=new int[9]  1st declaration
                {

                    String temp= (String) s.subSequence(s.indexOf("byte")+5,s.indexOf('['));
                    System.out.println(symbol+" "+temp+":Byte Array");
                    variables.add(symbol+" "+temp.trim()+":Byte Array class="+class_name);

                }
                else {  //int []m=new int[9]       2nd declaration

                    String temp= (String) s.subSequence(0,s.indexOf('='));
                    int index=temp.indexOf("]");
                    String var=temp.substring(index+1).trim();
                    System.out.println(symbol+" "+var+":Byte Array");
                    variables.add(symbol+" "+var+":Byte Array class="+class_name);
                }
            }

            else
            {
                if(s.contains("[]"))//still byte []b,c;
                {
                    int index=s.indexOf("]")+1;
                    String temp=s.substring(index,s.indexOf(";")-1);
                    String var=temp.trim();
                    System.out.println(symbol+" "+var+":Byte Array");
                    variables.add(symbol+" "+var+":Byte Array class="+class_name);

                }
                else
                {
                    int start=s.indexOf("byte");

                  if(s.contains("="))//ex byte b=9;
                {
                    with_var=(String) s.subSequence(start+5,s.indexOf("="));
                }
                else with_var= (String) s.subSequence(start+5,s.indexOf(";"));
                System.out.println(symbol+" "+with_var+":Byte");
                variables.add(symbol+" "+with_var+":Byte class="+class_name);
            }
            }
        }
        else if(s.contains("short"))
        {


            if(s.contains("[]")&&s.contains("="))  //array type of variable
            {
                if(s.indexOf(']')+1==s.indexOf('=')||s.indexOf(']')+1==s.indexOf('='))//int m[]=new int[9]  1st declaration
                {

                    String temp= (String) s.subSequence(s.indexOf("short")+6,s.indexOf('['));
                    System.out.println(symbol+" "+temp+":Short Array");
                    variables.add(symbol+" "+temp.trim()+":Short Array class="+class_name);
                }
                else {  //short []m=new short[9]       2nd declaration

                    String temp= (String) s.subSequence(0,s.indexOf('='));
                    int index=temp.indexOf("]");
                    String var=temp.substring(index+1).trim();
                    System.out.println(symbol+" "+var+":Short Array");
                    variables.add(symbol+" "+var+":Short Array class="+class_name);

                }
            }
            else
            {
                int start=s.indexOf("short");
                if(s.contains("[]"))//still short []b,c;
                {
                    int index=s.indexOf("]")+1;
                    String temp=s.substring(index,s.indexOf(";")-1);
                    String var=temp.trim();
                    System.out.println(symbol+" "+var+":Short Array");
                    variables.add(symbol+" "+var+":Short Array class="+class_name);
                }
                else {
                    if(s.contains("="))
                    {
                        with_var= (String) s.subSequence(start+6,s.indexOf("="));
                    }
                    else with_var= (String) s.subSequence(start+6,s.indexOf(";"));

                    System.out.println(symbol+" "+with_var+":Short");
                    variables.add(symbol+" "+with_var+":Short class="+class_name);
                }


            }
        }
        else if(s.contains("int"))
        {


            if(s.contains("[]")&&s.contains("="))  //array type of variable
            {
                if(s.indexOf(']')+1==s.indexOf('=')||s.indexOf(']')+1==s.indexOf('='))//int m[]=new int[9]  1st declaration
                {

                    String temp= (String) s.subSequence(s.indexOf("int")+4,s.indexOf('['));
                    System.out.println(symbol+" "+temp+":Integer Array");
                    variables.add(symbol+" "+temp.trim()+":Integer Array class="+class_name);
                }
                else {  //int []m=new int[9]       2nd declaration
                    String temp= (String) s.subSequence(0,s.indexOf('='));
                    int index=temp.indexOf("]");
                    String var=temp.substring(index+1).trim();
                    System.out.println(symbol+" "+var+":Integer Array");
                    variables.add(symbol+" "+var+":Integer Array class="+class_name);
                }
            }
            else
            {
                if(s.contains("[]")){
                    int index=s.indexOf("]")+1;
                    String temp=s.substring(index,s.indexOf(";")-1);
                    String var=temp.trim();
                    System.out.println(symbol+" "+var+":Integer Array");
                    variables.add(symbol+" "+var+":Integer Array class="+class_name);
                }
                else{
                int start=s.indexOf("int");
                if(s.contains("="))
                {
                    with_var= (String) s.subSequence(start+4,s.indexOf("="));
                }
                else with_var= (String) s.subSequence(start+4,s.indexOf(";"));
                System.out.println(symbol+" "+with_var+":Integer");
                variables.add(symbol+" "+with_var+":Integer class="+class_name);
            }
            }

        }
        else if(s.contains("long"))
        {
            if(s.contains("[]")&&s.contains("="))  //array type of variable
            {
                if(s.indexOf(']')+1==s.indexOf('=')||s.indexOf(']')+1==s.indexOf('='))//int m[]=new int[9]  1st declaration
                {

                    String temp= (String) s.subSequence(s.indexOf("long")+5,s.indexOf('['));
                    System.out.println(symbol+" "+temp+":Long Array");
                    variables.add(symbol+" "+temp.trim()+":Long Array class="+class_name);
                }
                else {  //int []m=new int[9]       2nd declaration
                    String temp= (String) s.subSequence(0,s.indexOf('='));
                    int index=temp.indexOf("]");
                    String var=temp.substring(index+1).trim();
                    System.out.println(symbol+" "+var+":Long Array");
                    variables.add(symbol+" "+var+":Long Array class="+class_name);
                }
            }
            else
            {
                if(s.contains("[]")){
                    int index=s.indexOf("]")+1;
                    String temp=s.substring(index,s.indexOf(";")-1);
                    String var=temp.trim();
                    System.out.println(symbol+" "+var+":Long Array");
                    variables.add(symbol+" "+var+":Long Array class="+class_name);
                }
                    else
                {
                    int start=s.indexOf("long");
                    if(s.contains("="))
                    {
                        with_var= (String) s.subSequence(start+5,s.indexOf("="));
                    }
                    else with_var= (String) s.subSequence(start+5,s.indexOf(";"));
                    System.out.println(symbol+" "+with_var+":Long");
                    variables.add(symbol+" "+with_var+":Long class="+class_name);
                }

            }
        }
        else if(s.contains("float"))
        {


            if(s.contains("[]")&&s.contains("="))  //array type of variable
            {
                if(s.indexOf(']')+1==s.indexOf('=')||s.indexOf(']')+1==s.indexOf('='))//int m[]=new int[9]  1st declaration
                {

                    String temp= (String) s.subSequence(s.indexOf("float")+6,s.indexOf('['));
                    System.out.println(symbol+" "+temp+":Float Array");
                    variables.add(symbol+" "+temp.trim()+":Float Array class="+class_name);
                }
                else {  //int []m=new int[9]       2nd declaration
                    String temp= (String) s.subSequence(0,s.indexOf('='));
                    int index=temp.indexOf("]");
                    String var=temp.substring(index+1).trim();
                    System.out.println(symbol+" "+var+":Float Array");
                    variables.add(symbol+" "+var+":Float Array class="+class_name);
                }
            }
            else
            {
                if(s.contains("[]"))
                {
                    int index=s.indexOf("]")+1;
                    String temp=s.substring(index,s.indexOf(";")-1);
                    String var=temp.trim();
                    System.out.println(symbol+" "+var+":Float Array");
                    variables.add(symbol+" "+var+":Float Array class="+class_name);
                }
                else {
                    int start=s.indexOf("float");
                    if(s.contains("="))
                    {
                        with_var= (String) s.subSequence(start+6,s.indexOf("="));
                    }
                    else with_var= (String) s.subSequence(start+6,s.indexOf(";"));
                    System.out.println(symbol+" "+with_var+":Float");
                    variables.add(symbol+" "+with_var+":Float class="+class_name);}


            }
        }
        else if(s.contains("char"))
        {

            if(s.contains("[]")&&s.contains("="))  //array type of variable
            {
                if(s.indexOf(']')+1==s.indexOf('=')||s.indexOf(']')+1==s.indexOf('='))//int m[]=new int[9]  1st declaration
                {

                    String temp= (String) s.subSequence(s.indexOf("char")+5,s.indexOf('['));
                    System.out.println(symbol+" "+temp+":Character Array");
                    variables.add(symbol+" "+temp.trim()+":Character Array class="+class_name);
                }
                else {  //int []m=new int[9]       2nd declaration
                    String temp= (String) s.subSequence(0,s.indexOf('='));
                    int index=temp.indexOf("]");
                    String var=temp.substring(index+1).trim();
                    System.out.println(symbol+" "+var+":Character Array");
                    variables.add(symbol+" "+var+":Character Array class="+class_name);
                }
            }
            else
            {
                if(s.contains("[]"))
                {
                    int index=s.indexOf("]")+1;
                    String temp=s.substring(index,s.indexOf(";")-1);
                    String var=temp.trim();
                    System.out.println(symbol+" "+var+":Character Array");
                    variables.add(symbol+" "+var.trim()+":Character Array class="+class_name);
                }

                else {
                    int start=s.indexOf("char");
                    if(s.contains("="))
                    {
                        with_var= (String) s.subSequence(start+5,s.indexOf("="));
                    }
                    with_var= (String) s.subSequence(start+5,s.indexOf(";"));
                    System.out.println(symbol+" "+with_var+":Character");
                    variables.add(symbol+" "+with_var+":Character class="+class_name);
                }

            }
        }
        else if(s.contains("double"))
        {
            if(s.contains("[]")&&s.contains("="))  //array type of variable
            {
                if(s.indexOf(']')+1==s.indexOf('=')||s.indexOf(']')+1==s.indexOf('='))//int m[]=new int[9]  1st declaration
                {

                    String temp= (String) s.subSequence(s.indexOf("double")+7,s.indexOf('['));
                    System.out.println(symbol+" "+temp+":Double Array");
                    variables.add(symbol+" "+temp.trim()+":Double Array class="+class_name);
                }
                else {  //int []m=new int[9]       2nd declaration
                    String temp= (String) s.subSequence(0,s.indexOf('='));
                    int index=temp.indexOf("]");
                    String var=temp.substring(index+1).trim();
                    System.out.println(symbol+" "+var+":Double Array");
                    variables.add(symbol+" "+var+":Double Array class="+class_name);
                }
            }
            else
            {
                if(s.contains("[]")){
                    int index=s.indexOf("]")+1;
                    String temp=s.substring(index,s.indexOf(";")-1);
                    String var=temp.trim();
                    System.out.println(symbol+" "+var+":Double Array");
                    variables.add(symbol+" "+var+":Double Array class="+class_name);
                }
                    else
                {
                    int start=s.indexOf("double");
                    if(s.contains("="))
                    {
                        with_var= (String) s.subSequence(start+7,s.indexOf("="));
                    }
                    with_var= (String) s.subSequence(start+7,s.indexOf(";"));
                    System.out.println(symbol+" "+with_var+":Double");
                    variables.add(symbol+" "+with_var+":Double class="+class_name);
                }

            }


        }
        else if(s.contains("String"))
        {


            if(s.contains("[]")&&s.contains("="))  //array type of variable
            {
                if(s.indexOf(']')+1==s.indexOf('=')||s.indexOf(']')+1==s.indexOf('='))//int m[]=new int[9]  1st declaration
                {

                    String temp= (String) s.subSequence(s.indexOf("String")+7,s.indexOf('['));
                    System.out.println(symbol+" "+temp+":String Array");
                    variables.add(symbol+" "+temp.trim()+":String Array class="+class_name);
                }
                else {  //int []m=new int[9]       2nd declaration
                    String temp= (String) s.subSequence(0,s.indexOf('='));
                    int index=temp.indexOf("]");
                    String var=temp.substring(index+1).trim();
                    System.out.println(symbol+" "+var+":String Array");
                    variables.add(symbol+" "+var+":String Array class="+class_name);
                }
            }
            else
            {
                if(s.contains("[]")){
                    int index=s.indexOf("]")+1;
                    String temp=s.substring(index,s.indexOf(";")-1);
                    String var=temp.trim();
                    System.out.println(symbol+" "+var+":String Array");
                    variables.add(symbol+" "+var+":String Array class="+class_name);
                }

                else {
                    int start=s.indexOf("String");
                    if(s.contains("="))
                    {
                        with_var= (String) s.subSequence(start+7,s.indexOf("="));
                    }
                    else with_var= (String) s.subSequence(start+7,s.indexOf(";"));
                    System.out.println(symbol+" "+with_var+":String");
                    variables.add(symbol+" "+with_var+":String class="+class_name);
                }

            }
        }

    }


    private void find_method(String s, String class_name) {
        StringTokenizer tokenizer=new StringTokenizer(s," ");
        while(tokenizer.hasMoreTokens())
        {
            String fun=tokenizer.nextToken();
                    /*
                        public---> + ,private ---> -,default --> `~ protected --> #
                     */
            String symbol,fun_return = null;
            switch (fun) {
                case "public":{symbol="+";fun=tokenizer.nextToken();
                    break;}
                case "private":
                {symbol="-";fun=tokenizer.nextToken();break;}
                case "protected":{symbol="#";fun=tokenizer.nextToken();break;}
                default:
                {
                    symbol="~";
                }
            }
            if(fun.equals("static")||fun.equals("final"))
            {
                fun= tokenizer.nextToken();
            }

            //fun_return=fun;fun=tokenizer.nextToken();
            switch(fun)
            {
                case "void":{
                    fun_return="void";fun=tokenizer.nextToken();break;
                }
                case "byte":{
                    fun_return="byte";fun=tokenizer.nextToken();break;
                }
                case "short":{
                    fun_return= "short";fun=tokenizer.nextToken();break;
                }
                case "int":{
                    fun_return="int"; fun=tokenizer.nextToken();break;
                }
                case "long":{
                    fun_return="long";fun=tokenizer.nextToken();break;
                }
                case "float":{
                    fun_return= "float";fun=tokenizer.nextToken();break;
                }
                case "double":{
                    fun_return="double";fun=tokenizer.nextToken();break;
                }
                case "boolean":{
                    fun_return="boolean";fun= tokenizer.nextToken();break;
                }
                case "char":{
                    fun_return="char";fun=tokenizer.nextToken();break;
                }
                case "String":{
                    fun_return="String"; fun=tokenizer.nextToken();break;
                }
                default:fun_return=fun;
                    //bcoz giving null exception so checking
                    if(tokenizer.hasMoreTokens()){fun=tokenizer.nextToken();}//returning Class type
            }
            if(fun.contains("(")&&s.contains(")"))
            {
                // String temp= (String) fun.subSequence(0,fun.indexOf(")")+1);   work well for 0 argument
                System.out.println(s);
                String temp= (String) s.subSequence(s.indexOf(fun),s.indexOf(")")+1);//using s.subSequence for multiple arguments
                methods.add(symbol+" "+temp+":"+fun_return+" class="+class_name);
                //checking class_name=function_name  so as to remove constructor possibility
                //String constructor=temp.substring(0,temp.charAt('('));
                System.out.println("\t"+symbol + " " + temp + " : " + fun_return);

            }


        }

    }

    private String find_Class(String s) {
       /* A .java file can only have one public class and it should be named as same as the .java file.
          But .java file can have several non public classes.*/
        System.out.println("coming to class -----------------" + s);//testing some cases of null coming
        StringTokenizer stringTokenizer = new StringTokenizer(s, " ");
//                    stack.push('{');
            //this condition bcoz class may or may not extend or implements
            //if class name and file name same ,that class is public in java...so for that modifier needs to be checked
            String access = stringTokenizer.nextToken();
            int temp_v;
            String class_name = null;
            switch (access) {
                case "public": {
                    temp_v=1;
                    break;
                }
                case "final": {
                    temp_v=1;
                    break;
                }
                case "abstract": {
                    temp_v=1;
                    break;
                }
                default:temp_v=0;

            }
            if(temp_v==1)access=stringTokenizer.nextToken();
            String add_class = null;
            if (access.equals("class")) {
                System.out.print("here is class .....\t");
                add_class = stringTokenizer.nextToken();
                System.out.println(add_class);
                class_name=add_class;
                class_diff.add(class_name);
                add_class=add_class+" 0";

            }

            if (stringTokenizer.hasMoreTokens()) {
                String temp = stringTokenizer.nextToken();
                if (temp.compareTo("extends") == 0) {
                    String c = stringTokenizer.nextToken();
                    //classes.add(c + " parent");
                    System.out.println("\t its parent is: " + c);
                    add_class+=" extends "+c;
                    //EXTENDS AND THEN IMPLEMENTS IN THAT ORDER
                    if (stringTokenizer.hasMoreTokens()) {
                        String temp_implement = stringTokenizer.nextToken();
                        if (temp_implement.compareTo("implements") == 0) {
                            //class can implement multiple interface so interface array
                            String interfaces = stringTokenizer.nextToken();
                            System.out.println("\t and interfaces implemented are :  " + interfaces);
                            add_class+=" implements "+interfaces;
                        }
                    }
                } else if (temp.compareTo("implements") == 0) {
                    { //if not extends then it should be interface if there is tokens
                        String interfaces = stringTokenizer.nextToken();
                        System.out.println("\t only interfaces implemented are :  " + interfaces);
                        add_class+=" implements "+interfaces;
                    }
                }
            }
            classes.add(add_class);

        return class_name;
    }
    }

















