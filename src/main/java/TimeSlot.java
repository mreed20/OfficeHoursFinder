import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

class TimeSlot
{
    final private DayOfWeek day;
    final private LocalTime startTime;
    final private LocalTime endTime;

    TimeSlot(DayOfWeek day, LocalTime start, LocalTime end)
    {
        this.day = day;
        this.startTime = start;
        this.endTime = end;
    }

    private static String dayOfWeekToString(DayOfWeek d)
    {
        if (d.equals(DayOfWeek.MONDAY)) {
            return "M";
        } else if (d.equals(DayOfWeek.TUESDAY)) {
            return "Tu";
        } else if (d.equals(DayOfWeek.WEDNESDAY)) {
            return "W";
        } else if (d.equals(DayOfWeek.THURSDAY)) {
            return "Th";
        } else if (d.equals(DayOfWeek.FRIDAY)) {
            return "F";
        } else {
            throw new IllegalArgumentException();
        }
    }

    public DayOfWeek getDay()
    {
        return day;
    }

    public LocalTime getStartTime()
    {
        return startTime;
    }

    public LocalTime getEndTime()
    {
        return endTime;
    }

    @Override
    public String toString()
    {
        return String.format("%s %s - %s",
                dayOfWeekToString(day),
                startTime.toString(),
                endTime.toString()
        );
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return day == timeSlot.day &&
                Objects.equals(startTime, timeSlot.startTime) &&
                Objects.equals(endTime, timeSlot.endTime);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(day, startTime, endTime);
    }

}
