import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * An intermediate class used to generate office hour slots of a desired length
 * Note:
 * Only schedules office hours on every 30 minute intervals
 * Times are represented in 24hr time
 */
class HoursGenerator
{
    private static final int eightAM = localTimeToIndex(LocalTime.of(9, 0));
    private static final int tenPM = localTimeToIndex(LocalTime.of(18, 0));

    /**
     * The main function
     */
    static List<TimeSlot> genHours(Duration length, List<DayOfWeek> days)
    {
        List<TimeSlot> hours = new ArrayList<>();
        //Generate office hours for each day in the list of days desired
        for (DayOfWeek day : days) {
            for (int j = eightAM; j < tenPM; j++) {
                int i = day.getValue();
                if (isValid(length, i, j)) {
                    hours.add(create(i, j, length));
                }
            }
        }
        return hours;
    }

    /**
     * Helper function to check if that office hour slot is available
     */
    private static boolean isValid(Duration length, int day, int curTime)
    {
        //end time is out of bounds situation
        return curTime + durationToIndex(length) <= 44;
    }

    /**
     * Helper function to create
     */
    private static TimeSlot create(int day, int curTime, Duration length)
    {
        DayOfWeek d = DayOfWeek.of(day);
        LocalTime start = indexToLocalTime(curTime);
        LocalTime end = start.plusMinutes(length.toMinutes());
        return new TimeSlot(DayOfWeek.of(day), start, end);
    }

    /**
     * Returns an integer index of a localtime in half-hour.
     */
    private static int localTimeToIndex(LocalTime t)
    {
        return t.getHour() * 2 + t.getMinute() / 30;
    }

    private static LocalTime indexToLocalTime(int index)
    {
        int min = (index % 2) * 30;
        int hour = (index - (index % 2)) / 2;
        return LocalTime.of(hour, min);
    }

    /**
     * Returns an index equivalent integer to represent a duration
     * ex: 60 minutes --> 2; 90 minutes --> 3; 1 hour 30 minutes --> 3
     */
    private static int durationToIndex(Duration dur)
    {
        return (int) dur.toMinutes() / 30;
    }

    public static void main(String[] args)
    {
        Duration dur = Duration.ofMinutes(60);
        TimeSlot class1 = new TimeSlot(DayOfWeek.WEDNESDAY,
                LocalTime.of(8, 0),
                LocalTime.of(22, 0));
        TimeSlot class2 = new TimeSlot(DayOfWeek.FRIDAY,
                LocalTime.of(8, 0),
                LocalTime.of(22, 0));

        List<TimeSlot> officeHours = genHours(dur, List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY));

        for (TimeSlot hour : officeHours) {
            System.out.println(hour);
        }
    }
}
