import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class ScheduleAnalyzer
{
    //students contains list of all students in this class
    private final List<Student> students;
    //hours contains list of all possible office hours
    private final List<TimeSlot> hours;
    //Contains list of students at every 15 minute interval who are available for next 30 minutes
    private final List<Student>[][] availableStudents;
    //chosenHours contains the TimeSlots that have been selected by the user for his/her office hours
    private final List<TimeSlot> chosenHours;
    //Pairings of TimeSlots and the corresponding list of available students for that office hour
    private final Map<TimeSlot, List<Student>> initialHours;
    private final int NUMDAYS = 5;
    private final int NUMINTERVALS = 4 * 24;

    @SuppressWarnings("unchecked")
    // should take as input a list of students gathered using the DatabaseConnector methods,
    // and a list of hours taken from the HoursGenerator methods
    public ScheduleAnalyzer(List<Student> students, List<TimeSlot> hours)
    {
        this.students = students;
        this.hours = hours;
        this.chosenHours = new ArrayList<>();
        this.availableStudents = new ArrayList[5][96];
        //Initialize each location in availableStudents with an arrayList
        for (int i = 0; i < NUMDAYS; i++) {
            for (int j = 0; j < NUMINTERVALS; j++) {
                availableStudents[i][j] = new ArrayList<>();
            }
        }
        //Builds the lists in availableStudents to hold who is available over next 30 minutes
        buildAvailableStudents();
        //For each time slot, calculates who can attend
        this.initialHours = calculateAvailabilities();
    }

//    public static void main(String[] args)
//    {
//        List<SchoolClass> classes1 = new ArrayList<>();
//        List<SchoolClass> classes2 = new ArrayList<>();
//        List<SchoolClass> classes3 = new ArrayList<>();
//        List<SchoolClass> classes4 = new ArrayList<>();
//        List<DayOfWeek> List1 = new ArrayList<>();
//        List1.add(DayOfWeek.TUESDAY);
//        SchoolClass CS321 = new SchoolClass("CS321", List1, LocalTime.of(12, 0, 0), LocalTime.of(13, 15, 0));
//        SchoolClass CS484 = new SchoolClass("CS484", List1, LocalTime.of(13, 0, 0), LocalTime.of(14, 15, 0));
//        classes1.add(CS321);
//        classes2.add(CS484);
//        classes3.add(CS321);
//        classes3.add(CS484);
//        List<Student> students = new ArrayList<>();
//        students.add(new Student("Kiwoong", classes1));
//        students.add(new Student("Avi", classes1));
//        students.add(new Student("Michael", classes1));
//        students.add(new Student("Serral", classes1));
//        students.add(new Student("Frank", classes2));
//        students.add(new Student("Scott", classes2));
//        students.add(new Student("Bob", classes3));
//        students.add(new Student("Matt", classes3));
//        students.add(new Student("Bob", classes4));
//        students.add(new Student("Matt", classes4));
//
//        List<TimeSlot> times = new ArrayList<>();
//        times.add(new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(12, 0, 0), LocalTime.of(13, 30, 0)));
//        times.add(new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(13, 0, 0), LocalTime.of(13, 30, 0)));
//        times.add(new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(13, 0, 0), LocalTime.of(14, 30, 0)));
//
//        ScheduleAnalyzer schedule = new ScheduleAnalyzer(students, times);
//        List<GeneratedHour> hours = schedule.buildGeneratedHours();
//        GeneratedHour bestTime = hours.get(0);
//        for (GeneratedHour hour : hours) {
//            System.out.printf("%.2f %s %s\n", hour.getAvailPercent(), hour.getTimeSlot().getStartTime(), hour.getTimeSlot().getEndTime());
//            if (hour.getAvailPercent() > bestTime.getAvailPercent())
//                bestTime = hour;
//        }
//        schedule.setOfficeHour(bestTime.getTimeSlot());
//        hours = schedule.buildGeneratedHours();
//        System.out.println();
//        for (GeneratedHour hour : hours) {
//           System.out.printf("%.2f %s %s\n", hour.getAvailPercent(), hour.getTimeSlot().getStartTime(), hour.getTimeSlot().getEndTime());
//       }
//   }

    /**
     * Returns integer representation of a DayOfTheWeek,
     * where Monday is 0 and Sunday is 6.
     */
    private static int dayToInt(DayOfWeek d)
    {
        return d.getValue() - 1;
    }

    /**
     * TODO: comment me
     *
     * @param t
     * @return
     */
    private static int localTimeToIndex(LocalTime t)
    {
        return t.getHour() * 4 + t.getMinute() / 15;
    }

    /*Primary method to be called after constructor. Returns a list of the GeneratedHour class.
      The availablityPercentages calculated are dependent on if chosenHours is empty or not.
      If other hours have been chosen, then the percentages calculated here ignore the students
      who can attend those office hours.
     */
    List<GeneratedHour> buildGeneratedHours()
    {
        //Create a list of students who are unable to go to an office hour, and the list of students who can attend
        List<Student> students;
        List<Student> alreadyAvail = new ArrayList<>();
        //If there haven't been any office hours chosen, then all students are used
        if (chosenHours.size() == 0)
            students = this.students;
		/*Otherwise, we remove the students who can attend any of the chosenHours from the overall list of students
		And add the students who can attend any of the chosenHours to alreadyAvail
		 */
        else {
            students = new ArrayList<>(this.students);
            for (TimeSlot t : chosenHours) {
                students.removeAll(initialHours.get(t));
                alreadyAvail.addAll(initialHours.get(t));
            }
        }

        List<GeneratedHour> generatedHours = new ArrayList<>();

        //If there are already office hours chosen
        if (chosenHours.size() != 0) {
            for (TimeSlot t : this.hours) {
                //Skip TimeSlots that are already chosen
                if (!chosenHours.contains(t)) {
                    //Gets the original list of students who could attend a given timeSlot and removes the students
                    //Who are already available to go to a chosen office hour
                    List<Student> studentsAvailable = new ArrayList<>(initialHours.get(t));
                    studentsAvailable.removeAll(alreadyAvail);
                    float availPercent;
                    //If all students can attend at least one office hour, calculate availPercent normally
                    if(students.size() == 0)
                        availPercent = ((float) (initialHours.get(t).size())) / this.students.size() * 100;
                    //If there are students who cannot attend one section, only consider them
                    else
                        availPercent = ((float) (studentsAvailable.size())) / students.size() * 100;

                    generatedHours.add(new GeneratedHour(availPercent, t));
                }
            }
            return generatedHours;
        }
        //If there have been no office hours chosen
        else {
            for (TimeSlot t : this.hours) {
                float availPercent = ((float) (initialHours.get(t).size())) / students.size() * 100;
                generatedHours.add(new GeneratedHour(availPercent, t));
            }
            return generatedHours;
        }
    }

    //For each TimeSlot in hours, this method calculates which students can attend an office hour during that TimeSlot
    private Map<TimeSlot, List<Student>> calculateAvailabilities()
    {

        List<Student> alreadyAvail = new ArrayList<>();
        if (this.chosenHours.size() != 0) {
            for (TimeSlot t : chosenHours) {
                alreadyAvail.addAll(initialHours.get(t));
            }
        }


        Map<TimeSlot, List<Student>> studentsPerTimeSlot = new HashMap<>();
        for (TimeSlot hour : hours) {
            List<Student> available = new ArrayList<>();
            int i = dayToInt(hour.getDay());
            int start = localTimeToIndex(hour.getStartTime());
            int end = localTimeToIndex(hour.getEndTime());
            // the end-1 is related to 30 minute intervals
            for (int j = start; j < end - 1; j++) {
                for (Student student : availableStudents[i][j]) {
                    if (!available.contains(student) && !alreadyAvail.contains(student)) {
                        available.add(student);
                    }
                }
            }
            studentsPerTimeSlot.put(hour, available);
        }
        if (chosenHours.size() != 0) {
            for (TimeSlot t : chosenHours) {
                studentsPerTimeSlot.remove(t);
            }
        }
        return studentsPerTimeSlot;
    }

    private void buildAvailableStudents()
    {
        for (Student student : students) {
            boolean[][] available = buildStudentArray(student);
            for (int i = 0; i < NUMDAYS; i++) {
                for (int j = 0; j < NUMINTERVALS - 1; j++) {

                    // Student only considered available if they are free for >30 minutes.
                    if (!available[i][j] && !available[i][j + 1]) {
                        this.availableStudents[i][j].add(student);
                    }
                }
            }
        }
    }

    /*
     * Adds a specified TimeSlot into the list of chosenHours. Adding a member to chosenHours will affect future calls
     * of buildGeneratedHours.
     */
    void setOfficeHour(TimeSlot t)
    {
        this.chosenHours.add(t);
    }

    public float getTotalAvailPercent()
    {
        if (chosenHours.size() == 0) throw new RuntimeException();

        List<Student> availableStudents = new ArrayList<>();
        for (TimeSlot t : chosenHours) {
            availableStudents.removeAll(initialHours.get(t));
            availableStudents.addAll(initialHours.get(t));
        }

        return ((float) (availableStudents.size())) / this.students.size();
    }

    /**
     * Generates a 2-d array representing student availability,
     * where 0 means not in class and 1 means in class.
     *
     * @param student The student whose schedule we look at.
     * @return
     */
    private boolean[][] buildStudentArray(Student student)
    {
        boolean[][] available = new boolean[NUMDAYS][NUMINTERVALS];
        //Arrays.fill(available, true);

        for (SchoolClass course : student.classes) {
            for (DayOfWeek day : course.days) {
                int i = dayToInt(day);
                // assumes that minutes are in 15 minute increments
                int start = localTimeToIndex(course.startTime);
                int end = localTimeToIndex(course.endTime);
                for (int j = start; j < end; j++) {
                    available[i][j] = true;
                }
            }
        }
        return available;
    }

}
