import java.util.Objects;
import java.util.List;

class GeneratedHour
{
    final float availPercent;
    final TimeSlot timeSlot;
    final List<Student> availStudents;

    GeneratedHour(float availPercent, TimeSlot timeSlot, List<Student> availStudents)
    {
        this.availPercent = availPercent;
        this.timeSlot = timeSlot;
        this.availStudents = availStudents;
    }

    public float getAvailPercent()
    {
        return availPercent;
    }

    public TimeSlot getTimeSlot()
    {
        return timeSlot;
    }

    public List<Student> getAvailStudents()
    {
        return availStudents;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneratedHour that = (GeneratedHour) o;
        return Float.compare(that.availPercent, availPercent) == 0 &&
                Objects.equals(timeSlot, that.timeSlot);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(availPercent, timeSlot);
    }
}
