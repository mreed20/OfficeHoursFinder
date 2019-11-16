public class GeneratedHour {

    final private float availPercent;
    final private TimeSlot t;

    public GeneratedHour(float availPercent, TimeSlot t)
    {
        this.availPercent = availPercent;
        this.t = t;
    }

    public float getAvailPercent()
    {
        return this.availPercent;
    }

    public TimeSlot getTimeSlot()
    {
        return this.t;
    }
}
