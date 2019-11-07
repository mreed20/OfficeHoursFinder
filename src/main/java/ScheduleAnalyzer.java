import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

public class ScheduleAnalyzer<availableStudents> {
	final List<Student> students;
	final List<TimeSlot> hours;
	private List<Student>[][] availableStudents;
	
	public ScheduleAnalyzer(List<Student> students, List<TimeSlot> hours) {
		this.students = students;
		this.hours = hours;
		// TODO
		availableStudents = null;
	}
	
	public Map<TimeSlot, List<Student>> calculateAvailabilities(){
	    /*
	    TODO
		buildAvailableStudents();
		Map<OfficeHour, ArrayList<Student>> availableStudents = new HashMap<>();
		for(OfficeHour hour : hours)
		{
			ArrayList<Student> available = new ArrayList<>();
			int i = hour.getDay().getValue()-1;
			int start = hour.getStartTime().getHour()*4 + hour.getStartTime().getMinute()/15;
			int end = hour.getEndTime().getHour()*4 + hour.getEndTime().getMinute()/15;
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
	     */
	    return null;
	}
	
	private void buildAvailableStudents() {
		for(Student student : students) {
			int[][] schedule = buildStudentArray(student);
			for(int i = 0; i<5; i++) {
				for(int j = 0; j<95; j++) {
					if(schedule[i][j] == 0 && schedule[i][j+1] == 0) {
						this.availableStudents[i][j].add(student);
					}
				}
			}
		}
	}
	
	private int[][] buildStudentArray(Student student) {
		int[][] schedule = new int[5][96];
		for(SchoolClass course : student.getClasses()) {
			for(DayOfWeek day : course.getDays()) {
				int i = day.getValue() - 1;
				int start = course.getStart().getHour()*4 + course.getStart().getMinute()/15;
				int end = course.getEnd().getHour()*4 + course.getEnd().getMinute()/15;
				for(int j = start; j<end; j++) {
					schedule[i][j] = 1;
				}
			}
		}
		return schedule;
	}
	
}
