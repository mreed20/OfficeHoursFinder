import org.jetbrains.annotations.Contract;

import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

class HoursGenerator {
    private static final int eightAM = localTimeToIndex(LocalTime.of(8,0));
    private static final int tenPM = localTimeToIndex(LocalTime.of(22,0));

    static List<TimeSlot> genHours(Duration length) {
        List<TimeSlot> hours = new ArrayList<>();
        //Go through each day 1:Mon ... 5:Fri
        for(int i = 1; i < 6; i++) {
            //go through each half hour in the day from 8am to 10pm
            for(int j = eightAM; j < tenPM; j++) {
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
    private static boolean isValid(Duration length, int day, int curTime) {
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
    private static TimeSlot create(int day, int curTime, Duration length) {
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
        TimeSlot class1 = new TimeSlot(DayOfWeek.WEDNESDAY,
                LocalTime.of(8,0),
                LocalTime.of(22,0));
        TimeSlot class2 = new TimeSlot(DayOfWeek.FRIDAY,
                LocalTime.of(8,0),
                LocalTime.of(22,0));

        List<TimeSlot> officeHours = hg.genHours(dur);

        for(TimeSlot hour: officeHours) {
            System.out.println(hour);
        }
    }
}
