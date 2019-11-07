import java.time.*;
import java.util.*;

public class ScheduleAnalyzer<availableStudents> {
	final List<Student> students;
	final List<TimeSlot> hours;
	private List<Student>[][] availableStudents;

	private final int NUMDAYS = 5;
	private final int NUMINTERVALS = 4 * 24;
	
	public ScheduleAnalyzer(List<Student> students, List<TimeSlot> hours) {
		this.students = students;
		this.hours = hours;
		availableStudents = null;
	}

	public Map<TimeSlot, List<Student>> calculateAvailabilities() {
		buildAvailableStudents();
		Map<TimeSlot, List<Student>> availableStudents = new HashMap<>();
		for(TimeSlot hour : hours)
		{
			ArrayList<Student> available = new ArrayList<>();
			int i = dayToInt(hour.getDay());
			int start = localTimeToIndex(hour.getStartTime());
			int end = localTimeToIndex(hour.getEndTime());
			// the end-1 is related to 30 minute intervals
			for(int j = start; j<end-1; j++) {
				for(Student student : availableStudents[i][j]) {
					if(!available.contains(student)) {
						available.add(student);
					}
				}
			}
			availableStudents.put(hour, available);			
		}
		return availableStudents;
	}


	private void buildAvailableStudents() {
		for(Student student : students) {
			boolean[][] available = buildStudentArray(student);
			for(int i = 0; i<NUMDAYS; i++) {
				for(int j = 0; j<NUMINTERVALS-1; j++) {

				    // Student only considered available if they are free for >30 minutes.
					if(available[i][j] && available[i][j+1]) {
						this.availableStudents[i][j].add(student);
					}
				}
			}
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
		Arrays.fill(available, true);

		for(SchoolClass course : student.getClasses()) {
			for(DayOfWeek day : course.getDays()) {
				int i = dayToInt(day);
				// assumes that minutes are in 15 minute increments
				int start = localTimeToIndex(course.getStart());
				int end = localTimeToIndex(course.getEnd());
				for(int j = start; j<end; j++) {
					available[i][j] = false;
				}
			}
		}
		return available;
	}
	
}
