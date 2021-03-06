import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

class SchoolClass
{
    final String name;
    final List<DayOfWeek> days;
    final LocalTime startTime;
    final LocalTime endTime;

    SchoolClass(String name, List<DayOfWeek> days, LocalTime startTime, LocalTime endTime)
    {
        this.name = name;
        this.days = days;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchoolClass that = (SchoolClass) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(days, that.days) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, days, startTime, endTime);
    }
}

