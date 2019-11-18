class GeneratedHour
{

    final private float availPercent;
    final private TimeSlot t;

    GeneratedHour(float availPercent, TimeSlot t)
    {
        this.availPercent = availPercent;
        this.t = t;
    }

    float getAvailPercent()
    {
        return this.availPercent;
    }

    TimeSlot getTimeSlot()
    {
        return this.t;
    }

    @Override
    public String toString()
    {
        return "GeneratedHour{" +
                "availPercent=" + availPercent +
                ", t=" + t +
                '}';
    }
}
