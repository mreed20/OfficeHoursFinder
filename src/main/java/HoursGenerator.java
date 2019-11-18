import org.jetbrains.annotations.Contract;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

class HoursGenerator {
    private static final int eightAM = localTimeToIndex(LocalTime.of(8,0));
    private static final int tenPM = localTimeToIndex(LocalTime.of(22,0));

    public static List<TimeSlot> genHours(Duration length, List<TimeSlot> schedule) {
        List<TimeSlot> hours = new ArrayList<>();
        //Go through each day 1:Mon ... 5:Fri
        for(int i = 1; i < 6; i++) {
            //go through each half hour in the day from 8am to 10pm
            for(int j = eightAM; j < tenPM; j++) {
                if(isAvailable(length, schedule, i, j)) {
                    hours.add(create(i, j, length));
                }
            }
        }
        return hours;
    }

    /**
     * Helper function to check if that office hour slot is available
     */
    private static boolean isAvailable(Duration length, List<TimeSlot> schedule, int day, int curTime) {
        int d = durationToIndex(length);
        LocalTime start = indexToLocalTime(curTime);

        //end time is out of bounds situation
        if(curTime + durationToIndex(length) < 48) { return false; }

        LocalTime end = indexToLocalTime(durationToIndex(length) + curTime);

        for(TimeSlot teacher_class: schedule) {
            if(teacher_class.getDay().getValue() == day){
                if(isTimeConflict(start, end, teacher_class)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * internal helper method to see if there is conflict in time
     */
    private static boolean isTimeConflict(LocalTime startOffice, LocalTime endOffice, TimeSlot teacher_class){
        //class is after the end of the office hour
        if (teacher_class.getStartTime().isAfter(endOffice)) {
            return true;
        }
        //class ends before the office hour starts
        if (teacher_class.getEndTime().isBefore(startOffice)) {
            return true;
        }
        //cases where they could potentially intersect
        //if class starts before the office hour
        if(teacher_class.getStartTime().isBefore(startOffice)){
            //check to see if the office hour starts before class is over
            if(startOffice.isBefore(teacher_class.getEndTime())){
                return false;
            }
        } else { //class starts on or after the office hour beins
            if(endOffice.isAfter(teacher_class.getStartTime())) {
                return false;
            }
        }
        return true;
    }

    private static TimeSlot create(int day, int curTime, Duration length) {
        DayOfWeek d = DayOfWeek.of(day);
        LocalTime start = indexToLocalTime(curTime);
        LocalTime end = start.plusHours(length.toMinutes());
        TimeSlot temp = new TimeSlot(DayOfWeek.of(day), start,end);
        return temp;
    }

    /**
     * Returns an integer index of a localtime in half-hour.
     */
    private static int localTimeToIndex(LocalTime t) {
        return t.getHour()*2 + t.getMinute()/30;
    }

    private static LocalTime indexToLocalTime(int index) {
        int min = (index % 2) * 30;
        int hour = (index - (index % 2)) / 2;
        LocalTime temp = LocalTime.of(hour, min);
        return temp;
    }

    /**
     * Returns an index equivalent integer to represent a duration
     *  ex: 60 minutes --> 2; 90 minutes --> 3; 1 hour 30 minutes --> 3
     */
    private static int durationToIndex(Duration dur) {
        return (int)dur.toMinutes() / 30;
    }


    public static void main(String[] args) {
        HoursGenerator hg = new HoursGenerator();
        //hg.genHours(Duration.ofMinutes(30), null);
        LocalTime lt = LocalTime.of(2,30);

        System.out.println(lt.plus(Duration.ofMinutes(120)));
    }

}
