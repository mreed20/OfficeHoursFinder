import java.time.*;
import java.util.List;
import java.util.Objects;

class SchoolClass {
    public final String name;
    public final String roomNumber;
    public final List<DayOfWeek> days;
    public final LocalTime startTime;
    public final LocalTime endTime;

    public SchoolClass(String name, String roomNumber, List<DayOfWeek> days, LocalTime startTime, LocalTime endTime) {
        this.name = name;
        this.roomNumber = roomNumber;
        this.days = days;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "SchoolClass{" +
                "name='" + name + '\'' +
                ", roomNumber='" + roomNumber + '\'' +
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
                Objects.equals(roomNumber, that.roomNumber) &&
                Objects.equals(days, that.days) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, roomNumber, days, startTime, endTime);
    }
}

