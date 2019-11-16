import java.time.*;
import java.util.*;


public class ScheduleAnalyzer {
	final List<Student> students;
	final List<TimeSlot> hours;
	private List<Student>[][] availableStudents;
	private List<TimeSlot> chosenHours;
	private Map<TimeSlot, List<Student>> initialHours;
	private final int NUMDAYS = 5;
	private final int NUMINTERVALS = 4 * 24;

	/* public static void main(String[] args)
	{
		List<SchoolClass> classes1 = new ArrayList<SchoolClass>();
		List<SchoolClass> classes2 = new ArrayList<SchoolClass>();
		List<DayOfWeek> List1 = new ArrayList<DayOfWeek>();
		List1.add(DayOfWeek.TUESDAY);
		classes1.add(new SchoolClass("CS321", List1, LocalTime.of(12, 0, 0), LocalTime.of(13,15,0)));
		classes2.add(new SchoolClass("CS484", List1, LocalTime.of(13, 0, 0), LocalTime.of(14,15,0)));
		List<Student> students = new ArrayList<Student>();
		students.add(new Student("Kiwoong", classes1));
		students.add(new Student("Avi", classes1));
		students.add(new Student("Michael", classes1));
		students.add(new Student("Frank", classes2));
		students.add(new Student("Scott", classes2));

		List<TimeSlot> times = new ArrayList<TimeSlot>();
		times.add(new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(12, 0, 0), LocalTime.of(13,30,0)));
		times.add(new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(13, 0, 0), LocalTime.of(13,30,0)));
		times.add(new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(13, 0, 0), LocalTime.of(14,30,0)));

		ScheduleAnalyzer schedule = new ScheduleAnalyzer(students, times);
		List<GeneratedHour> hours = schedule.buildGeneratedHours();
		GeneratedHour bestTime = hours.get(0);
		for(GeneratedHour hour : hours)
		{
			System.out.printf("%.2f %s %s\n", hour.getAvailPercent(), hour.getTimeSlot().getStartTime(), hour.getTimeSlot().getEndTime());
			if(hour.getAvailPercent() > bestTime.getAvailPercent())
				bestTime = hour;
		}
		schedule.setOfficeHour(bestTime.getTimeSlot());
		hours = schedule.buildGeneratedHours();
		for(GeneratedHour hour : hours)
		{
			System.out.printf("%.2f %s %s\n", hour.getAvailPercent(), hour.getTimeSlot().getStartTime(), hour.getTimeSlot().getEndTime());
		}
	} */

	@SuppressWarnings("unchecked")
	public ScheduleAnalyzer(List<Student> students, List<TimeSlot> hours) {
		this.students = students;
		this.hours = hours;
		this.chosenHours = new ArrayList<>();
		this.availableStudents = new ArrayList[5][96];
		for(int i = 0; i < NUMDAYS; i++)
		{
			for(int j = 0; j < NUMINTERVALS; j++)
			{
				availableStudents[i][j] = new ArrayList<>();
			}
		}
		buildAvailableStudents();
		this.initialHours = calculateAvailabilities();
	}

	public List<GeneratedHour> buildGeneratedHours()
	{
		List<Student> students;
		if(chosenHours.size() == 0)
			students = this.students;
		else {
			students = new ArrayList<>(this.students);
			for(TimeSlot t : chosenHours) {
				students.removeAll(initialHours.get(t));
			}
		}

		List<Student> alreadyAvail = new ArrayList<>();
		if(this.chosenHours.size() != 0)
		{
			for(TimeSlot t : chosenHours) {
				alreadyAvail.addAll(initialHours.get(t));
			}
		}

		List<GeneratedHour> generatedHours = new ArrayList<>();

		if(chosenHours.size() != 0)
		{
			for(TimeSlot t : this.hours)
			{
				if(!chosenHours.contains(t)) {
					List<Student> studentsAvailable = new ArrayList<>(initialHours.get(t));
					studentsAvailable.removeAll(alreadyAvail);
					float availPercent = ((float)(studentsAvailable.size()))/students.size();
					generatedHours.add(new GeneratedHour(availPercent, t));
				}
			}
			return generatedHours;
		}
		else
		{
			for(TimeSlot t : this.hours)
			{
				float availPercent = ((float)(initialHours.get(t).size()))/students.size();
				generatedHours.add(new GeneratedHour(availPercent, t));
			}
			return generatedHours;
		}
	}

	public Map<TimeSlot, List<Student>> calculateAvailabilities() {

		List<Student> alreadyAvail = new ArrayList<>();
		if(this.chosenHours.size() != 0)
		{
			for(TimeSlot t : chosenHours) {
				alreadyAvail.addAll(initialHours.get(t));
			}
		}


		Map<TimeSlot, List<Student>> studentsPerTimeSlot = new HashMap<>();
		for(TimeSlot hour : hours)
		{
			List<Student> available = new ArrayList<>();
			int i = dayToInt(hour.getDay());
			int start = localTimeToIndex(hour.getStartTime());
			int end = localTimeToIndex(hour.getEndTime());
			// the end-1 is related to 30 minute intervals
			for(int j = start; j<end-1; j++) {
				for(Student student : availableStudents[i][j]) {
					if(!available.contains(student) && !alreadyAvail.contains(student)) {
						available.add(student);
					}
				}
			}
			studentsPerTimeSlot.put(hour, available);
		}
		if(chosenHours.size() != 0)
		{
			for(TimeSlot t : chosenHours)
			{
				studentsPerTimeSlot.remove(t);
			}
		}
		return studentsPerTimeSlot;
	}


	private void buildAvailableStudents() {
		for(Student student : students) {
			boolean[][] available = buildStudentArray(student);
			for(int i = 0; i<NUMDAYS; i++) {
				for(int j = 0; j<NUMINTERVALS-1; j++) {

					// Student only considered available if they are free for >30 minutes.
					if(!available[i][j] && !available[i][j+1]) {
						this.availableStudents[i][j].add(student);
					}
				}
			}
		}
	}

	public void setOfficeHour(TimeSlot t)
	{
		this.chosenHours.add(t);
	}

	public float getTotalAvailPercent()
	{
		List<Student> availableStudents = new ArrayList<>();
		if(chosenHours.size() == 0) {return -1;}
		else{
			for(TimeSlot t : chosenHours)
			{
				availableStudents.removeAll(initialHours.get(t));
				availableStudents.addAll(initialHours.get(t));
			}

			return (((float)(availableStudents.size()))/this.students.size());
		}

	}

	/**
	 * Returns integer representation of a DayOfTheWeek,
	 * where Monday is 0 and Sunday is 6.
	 */
	private static int dayToInt(DayOfWeek d) {
		return d.getValue() - 1;
	}


	/**
	 * TODO: comment me
	 * @param t
	 * @return
	 */
	private static int localTimeToIndex(LocalTime t) {
		return t.getHour()*4 + t.getMinute()/15;
	}


	/**
	 * Generates a 2-d array representing student availability,
	 * where 0 means not in class and 1 means in class.
	 *
	 * @param student  The student whose schedule we look at.
	 * @return
	 */
	private boolean[][] buildStudentArray(Student student) {
		boolean[][] available = new boolean[NUMDAYS][NUMINTERVALS];
		//Arrays.fill(available, true);

		for(SchoolClass course : student.getClasses()) {
			for(DayOfWeek day : course.getDays()) {
				int i = dayToInt(day);
				// assumes that minutes are in 15 minute increments
				int start = localTimeToIndex(course.getStart());
				int end = localTimeToIndex(course.getEnd());
				for(int j = start; j<end; j++) {
					available[i][j] = true;
				}
			}
		}
		return available;
	}

}
