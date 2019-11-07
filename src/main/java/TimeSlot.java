import java.time.*;

public class TimeSlot {
    final private DayOfWeek day;
    final private LocalTime startTime;
    final private LocalTime endTime;

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
}
