import java.time.*;
import java.util.List;
import java.util.Objects;

class SchoolClass {
    final String name;
    final String room;
    final List<DayOfWeek> days;
    final LocalTime startTime;
    final LocalTime endTime;

    SchoolClass(String name, String room, List<DayOfWeek> days, LocalTime startTime, LocalTime endTime) {
        this.name = name;
        this.room = room;
        this.days = days;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "SchoolClass{" +
                "name='" + name + '\'' +
                ", room='" + room + '\'' +
                ", days=" + days +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchoolClass that = (SchoolClass) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(room, that.room) &&
                Objects.equals(days, that.days) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, room, days, startTime, endTime);
    }
}

