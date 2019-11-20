import java.util.Objects;

class GeneratedHour
{
    final float availPercent;
    final TimeSlot timeSlot;

    GeneratedHour(float availPercent, TimeSlot timeSlot)
    {
        this.availPercent = availPercent;
        this.timeSlot = timeSlot;
    }

    public float getAvailPercent()
    {
        return availPercent;
    }

    public TimeSlot getTimeSlot()
    {
        return timeSlot;
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
