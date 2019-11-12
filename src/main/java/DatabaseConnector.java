import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.Date;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

class DatabaseConnector {

    // TODO: make this private
    private List<SchoolClass> classes = new ArrayList<>();
    Map<Integer, Student> students = new HashMap<>();

    private final String url = "jdbc:postgresql://localhost:5432/postgres";
    private final String username = "postgres";
    private final String password = "postgresqlmasterpassword";

    DatabaseConnector() throws SQLException {

        Connection conn = null; //used to create a connection
        ResultSet rs = null; //used to get the results after executing the query
        ResultSet rs1 = null; //same
        Statement statement = null; //used to make an sql statement
        Statement statement1 = null;
        Date date = null; //temporary date variable

        conn = DriverManager.getConnection(url, username, password); //makes the connection
        System.out.println("Successful connection");

        statement = conn.createStatement(); //sets up to make an sql statement
        statement1 = conn.createStatement();

        rs = statement.executeQuery("select * from public.classes"); //gets the list of all the classes from the classes table

        while (rs.next()) { //loops through the result set and makes each a class a new SchoolClass object


            String days = rs.getString("days"); //gets the days from the database
            days = days.substring(1, days.length() - 1); //since it comes with braces, we take only the string within the braces
            String[] temp = days.split(","); //if multiple days exist, there will be a comma separating them, so we remove the comma
            List<DayOfWeek> test = new ArrayList<>(); //This is a list that will store the string to DaysOfWeek conversion

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE");
            TemporalAccessor accessor; //These two are used to make the conversions


            for (int i = 0; i < temp.length; i++) {

                test.add(DayOfWeek.from(formatter.parse(temp[i].replaceAll(" ", "")))); //removes white space and converts
                //string to DayOfWeek

            }



            String class_name = rs.getString("class_name");
            LocalTime startTime = rs.getTime("starttime").toLocalTime();
            LocalTime endTime = rs.getTime("endtime").toLocalTime();


            classes.add(new SchoolClass(class_name, test, startTime, endTime));


        }

        rs = statement.executeQuery("select distinct gnumber from public.students"); //gets all the distinct students
        while (rs.next()) {

            int gnum = rs.getInt("gnumber"); //gets the gnumber
            System.out.println(gnum);

            rs1 = statement1.executeQuery("select classes from public.students where gnumber = " + gnum);//gets the classes of a student,
            List<SchoolClass> student_classes = new ArrayList<>(); //a list of all classes belonging to a student

            while (rs1.next()) {

                String classname = rs1.getString("classes");//gets the class name

                for (int i = 0; i < classes.size(); i++) {

                    //finds that class in the classes list and adds it to the student_classes list
                    if (classes.get(i).name.equals(classname)) {
                        student_classes.add(classes.get(i));
                    }
                }
            }

            students.put(gnum, new Student(Integer.toString(gnum), student_classes)); //adds a new student object to the s list
        }
    }
}
