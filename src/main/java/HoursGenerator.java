

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * An intermediate class used to generate office hour slots of a desired length
 * Note:
 *      Only schedules office hours on every 30 minute intervals
 *      Times are represented in 24hr time
 *
 */
class HoursGenerator {
    private List<TimeSlot> hours;
    private final int eightAM = localTimeToIndex(LocalTime.of(8,0));
    private final int tenPM = localTimeToIndex(LocalTime.of(22,0));

    HoursGenerator() {
        this.hours = new ArrayList<>();
    }

    /**
     * The main function
     */
    public List<TimeSlot> genHours(Duration length, List<DayOfWeek> days) {
        //Generate office hours for each day in the list of days desired
        for(DayOfWeek day: days) {
            for(int j = eightAM; j < tenPM; j++) {
                int i = day.getValue();
                if(isValid(length, i, j)) {
                    hours.add(create(i, j, length));
                }
            }
        }
        return hours;
    }

    /**
     * Helper function to check if that office hour slot is available
     */
    private boolean isValid(Duration length, int day, int curTime) {
        int d = durationToIndex(length);
        LocalTime start = indexToLocalTime(curTime);
        //end time is out of bounds situation
        if(curTime + durationToIndex(length) > 44) {
            return false;
        }
        return true;
    }

    /**
     * Helper function to create
     */
    private TimeSlot create(int day, int curTime, Duration length) {
        DayOfWeek d = DayOfWeek.of(day);
        LocalTime start = indexToLocalTime(curTime);
        LocalTime end = start.plusMinutes(length.toMinutes());
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

        Duration dur = Duration.ofMinutes(60);
        List<DayOfWeek> days = new ArrayList<>();
        days.add(DayOfWeek.MONDAY);
        days.add(DayOfWeek.WEDNESDAY);

        List<TimeSlot> officeHours = hg.genHours(dur, days);

        for(TimeSlot hour: officeHours) {
            System.out.println(hour);
        }
    }
}
