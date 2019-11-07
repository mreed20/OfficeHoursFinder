import java.util.ArrayList;
import java.util.List;

public class HoursGenerator {
    List<TimeSlot> timeSlots;

    HoursGenerator() {
        this.timeSlots = new ArrayList<>();
    }

    public List<TimeSlot> genHours(int length, List<TimeSlot> schedule) {
        //Go through each day
        for(int i = 0; i < 6; i++) {
            //go through each half hour in the day from 7am to 10pm
            for(int j = 7; j < 20; j++) {
                if(isAvailable(length, schedule)) {
                    timeSlots.add(create(i, j));
                }
            }
        }


        return timeSlots;
    }

    private boolean isAvailable(int length, List<TimeSlot> schedule) {
        /**
         * to implement
         */
        return true;
    }

    private TimeSlot create(int day, int time) {
        /**
         * to implement
         */
        return null;
    }
}
