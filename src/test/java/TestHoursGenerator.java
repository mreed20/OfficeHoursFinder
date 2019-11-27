import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestHoursGenerator extends HoursGenerator
{

    @Test
    void testDurationToIndex()
    {
        for (int i = 0; i < 50; i++) {
            assertEquals(
                    durationToIndex(Duration.ofMinutes(i * 30)),
                    i
            );
        }

        for (int i = 1; i <= 29; i++) {
            int finalI = i;
            assertThrows(IllegalArgumentException.class,
                    () -> durationToIndex(Duration.ofMinutes(finalI))
            );
        }
    }

    @Test
    void testLocalTimeToIndex()
    {
        assertEquals(localTimeToIndex(LocalTime.of(8, 0)), 16);
        assertEquals(localTimeToIndex(LocalTime.of(8, 30)), 17);
        assertEquals(localTimeToIndex(LocalTime.of(9, 0)), 18);

        assertEquals(localTimeToIndex(LocalTime.of(21, 30)), 43);
        assertEquals(localTimeToIndex(LocalTime.of(22, 0)), 44);
    }

    @Test
    void testIndexToLocalTime()
    {
        assertEquals(indexToLocalTime(16), LocalTime.of(8, 0));
        assertEquals(indexToLocalTime(17), LocalTime.of(8, 30));
        assertEquals(indexToLocalTime(18), LocalTime.of(9, 0));

        assertEquals(indexToLocalTime(43), LocalTime.of(21, 30));
        assertEquals(indexToLocalTime(44), LocalTime.of(22, 0));
    }

    @Test
    void testCreate()
    {
        DayOfWeek[] days = {
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY
        };
        for (int day = 1; day < 6; day++) {
            TimeSlot t = create(day, 0, Duration.ofMinutes(60));
            assertEquals(t.getDay(), days[day-1]);
            assertEquals(t.getStartTime().getHour(), 0);
        }
    }

    @Test
    void testIsValid()
    {
        for (int i = 0; i <= 43; i++) {
            assertTrue(isValid(Duration.ofMinutes(30), i));
        }
        for (int i = 44; i <= 100; i++) {
            assertFalse(isValid(Duration.ofMinutes(30), i));
        }
    }

    @Test
    void testGenHours()
    {
        //office hour length of one hour and on mondays and wednesdays
        Duration dur = Duration.ofMinutes(60);

        List<TimeSlot> officeHours = HoursGenerator.genHours(dur, List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY));
        for (TimeSlot hour : officeHours) {
            assertTrue(hour.getDay() == DayOfWeek.WEDNESDAY || hour.getDay() == DayOfWeek.MONDAY);
        }
        assertTrue(officeHours.size() == 36);

        //office hour length of 20 hours, should be no time slot available
        dur = Duration.ofMinutes(1200);
        officeHours = HoursGenerator.genHours(dur, List.of(DayOfWeek.MONDAY,DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY));
        assertTrue(officeHours.size() == 0);

        //office hour length 2 hours and on thursdays + fridays
        dur = Duration.ofMinutes(120);
        officeHours = HoursGenerator.genHours(dur, List.of(DayOfWeek.THURSDAY,DayOfWeek.FRIDAY));
        for (TimeSlot hour : officeHours) {
            assertTrue(hour.getDay() == DayOfWeek.THURSDAY || hour.getDay() == DayOfWeek.FRIDAY);
        }


    }

}
