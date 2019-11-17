import java.time.*;
import java.util.Objects;

public class TimeSlot {
    final private DayOfWeek day;
    final private LocalTime startTime;
    final private LocalTime endTime;

    // TODO: natural ordering
    TimeSlot(DayOfWeek day, LocalTime start, LocalTime end) {
        this.day = day;
        this.startTime = start;
        this.endTime = end;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return day == timeSlot.day &&
                Objects.equals(startTime, timeSlot.startTime) &&
                Objects.equals(endTime, timeSlot.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, startTime, endTime);
    }
}
