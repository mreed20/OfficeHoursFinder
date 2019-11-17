import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.time.format.DateTimeFormatter;

class DatabaseConnector {

    private final List<SchoolClass> classes = new ArrayList<>();
    private Map<Integer, Student> students = new HashMap<>();
    private Map<Integer, Teacher> teachers = new HashMap<>();


    DatabaseConnector(String url, String username, String password) throws SQLException {
        Connection conn = DriverManager.getConnection(url, username, password); //makes the connection

        Statement statement = conn.createStatement(); //sets up to make an sql statement
        Statement statement1 = conn.createStatement();

        ResultSet rs = statement.executeQuery("select * from public.classes"); //gets the list of all the classes from the classes table

        // loops through the result set and makes each a class a new SchoolClass object
        while (rs.next()) {

            String days = rs.getString("days"); //gets the days from the database
            days = days.substring(1, days.length() - 1); //since it comes with braces, we take only the string within the braces
            String[] temp = days.split(","); //if multiple days exist, there will be a comma separating them, so we remove the comma
            List<DayOfWeek> test = new ArrayList<>(); //This is a list that will store the string to DaysOfWeek conversion

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE");

            for (String s : temp) {
                //removes white space and converts string to DayOfWeek
                test.add(DayOfWeek.from(formatter.parse(s.replaceAll(" ", ""))));
            }

            String class_name = rs.getString("class_name");
            LocalTime startTime = rs.getTime("starttime").toLocalTime();
            LocalTime endTime = rs.getTime("endtime").toLocalTime();

            classes.add(new SchoolClass(class_name, test, startTime, endTime));
        }

        rs = statement.executeQuery("select distinct gnumber from public.students"); //gets all the distinct students
        while (rs.next()) {

            int gnum = rs.getInt("gnumber"); //gets the gnumber

            ResultSet rs1 = statement1.executeQuery("select classes from public.students where gnumber = " + gnum);//gets the classes of a student,
            List<SchoolClass> student_classes = new ArrayList<>(); //a list of all classes belonging to a student

            while (rs1.next()) {

                String classname = rs1.getString("classes");//gets the class name

                for (SchoolClass aClass : classes) {

                    //finds that class in the classes list and adds it to the student_classes list
                    if (aClass.name.equals(classname)) {
                        student_classes.add(aClass);
                    }
                }
            }

            students.put(gnum, new Student(Integer.toString(gnum), student_classes)); //adds a new student object to the s list
        }

        rs = statement.executeQuery("select distinct gnumber, name from public.teachers"); //gets all the distinct teachers
        while (rs.next()) {

            int gnum = rs.getInt("gnumber"); //gets the gnumber
            String teach_name = rs.getString("name");

            ResultSet rs1 = statement1.executeQuery("select classes_teaching from public.teachers where gnumber = " + gnum);//gets the classes of a student,
            List<SchoolClass> teacher_classes = new ArrayList<>(); //a list of all classes belonging to a student

            while (rs1.next()) {

                String classname = rs1.getString("classes_teaching");//gets the class name

                for (SchoolClass aClass : classes) {

                    //finds that class in the classes list and adds it to the student_classes list
                    if (aClass.name.equals(classname)) {
                        teacher_classes.add(aClass);
                    }
                }
            }

            teachers.put(gnum, new Teacher(teach_name, gnum, teacher_classes)); //adds a new student object to the s list
        }
    }

    Teacher getTeacher(int gNumber) {
        return this.teachers.get(gNumber);
    }
}
