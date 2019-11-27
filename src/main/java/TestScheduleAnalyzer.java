import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestScheduleAnalyzer
{

    @Test
    void testBuildGeneratedHours() {
        List<SchoolClass> classes1 = new ArrayList<>();
        List<SchoolClass> classes2 = new ArrayList<>();
        List<SchoolClass> classes3 = new ArrayList<>();
        List<SchoolClass> classes4 = new ArrayList<>();
        List<DayOfWeek> List1 = new ArrayList<>();
        List1.add(DayOfWeek.TUESDAY);
        SchoolClass CS321 = new SchoolClass("CS321", List1, LocalTime.of(12, 0, 0), LocalTime.of(13, 15, 0));
        SchoolClass CS484 = new SchoolClass("CS484", List1, LocalTime.of(13, 0, 0), LocalTime.of(14, 15, 0));
        classes1.add(CS321);
        classes2.add(CS484);
        classes3.add(CS321);
        classes3.add(CS484);
        List<Student> students = new ArrayList<>();
        students.add(new Student("Kiwoong", classes1));
        students.add(new Student("Avi", classes1));
        students.add(new Student("Michael", classes1));
        students.add(new Student("Serral", classes1));
        students.add(new Student("Frank", classes2));
        students.add(new Student("Scott", classes2));
        students.add(new Student("Bob", classes3));
        students.add(new Student("Matt", classes3));
        students.add(new Student("Bob", classes4));
        students.add(new Student("Matt", classes4));

        List<TimeSlot> times = new ArrayList<>();
        times.add(new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(12, 0, 0), LocalTime.of(13, 30, 0)));
        times.add(new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(13, 0, 0), LocalTime.of(13, 30, 0)));
        times.add(new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(13, 0, 0), LocalTime.of(14, 30, 0)));

        ScheduleAnalyzer schedule = new ScheduleAnalyzer(students, times);
        List<GeneratedHour> hours = schedule.buildGeneratedHours();

        int[] expected = {40, 20, 60};

        for(int i = 0; i < expected.length; i++)
        {
            assertEquals(expected[i], (int)(hours.get(i).getAvailPercent()));
        }

        GeneratedHour bestTime = hours.get(0);
        for (GeneratedHour hour : hours) {
            if (hour.getAvailPercent() > bestTime.getAvailPercent())
                bestTime = hour;
        }
        schedule.setOfficeHour(bestTime);
        hours = schedule.buildGeneratedHours();
        int[] expected2 = {50, 0};
        for(int i = 0; i < expected2.length; i++)
        {
            assertEquals(expected2[i], (int)(hours.get(i).getAvailPercent()));
        }
        bestTime = hours.get(0);
        for (GeneratedHour hour : hours) {
            if (hour.getAvailPercent() > bestTime.getAvailPercent())
                bestTime = hour;
        }
        schedule.setOfficeHour(bestTime);
        hours = schedule.buildGeneratedHours();
        assertEquals(0, (int)(hours.get(0).getAvailPercent()));

    }

    @Test
    void testSetOfficeHour() {
        List<Student> students = new ArrayList<>();
        List<TimeSlot> times = new ArrayList<>();
        ScheduleAnalyzer schedule = new ScheduleAnalyzer(students, times);
        assertEquals(0, schedule.getChosenHours().size());
        GeneratedHour hour = new GeneratedHour((float)(0), new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(12, 0, 0), LocalTime.of(13, 30, 0)), students);
        schedule.setOfficeHour(hour);
        assertEquals(1, schedule.getChosenHours().size());
        schedule.setOfficeHour(hour);
        assertEquals(2, schedule.getChosenHours().size());
    }

    @Test
    void testGetTotalAvailPercent() {
        List<SchoolClass> classes1 = new ArrayList<>();
        List<SchoolClass> classes2 = new ArrayList<>();
        List<SchoolClass> classes3 = new ArrayList<>();
        List<SchoolClass> classes4 = new ArrayList<>();
        List<DayOfWeek> List1 = new ArrayList<>();
        List1.add(DayOfWeek.TUESDAY);
        SchoolClass CS321 = new SchoolClass("CS321", List1, LocalTime.of(12, 0, 0), LocalTime.of(13, 15, 0));
        SchoolClass CS484 = new SchoolClass("CS484", List1, LocalTime.of(13, 0, 0), LocalTime.of(14, 15, 0));
        classes1.add(CS321);
        classes2.add(CS484);
        classes3.add(CS321);
        classes3.add(CS484);
        List<Student> students = new ArrayList<>();
        students.add(new Student("Kiwoong", classes1));
        students.add(new Student("Avi", classes1));
        students.add(new Student("Michael", classes1));
        students.add(new Student("Serral", classes1));
        students.add(new Student("Frank", classes2));
        students.add(new Student("Scott", classes2));
        students.add(new Student("Bob", classes3));
        students.add(new Student("Matt", classes3));
        students.add(new Student("Bob", classes4));
        students.add(new Student("Matt", classes4));

        List<TimeSlot> times = new ArrayList<>();
        times.add(new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(12, 0, 0), LocalTime.of(13, 30, 0)));
        times.add(new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(13, 0, 0), LocalTime.of(13, 30, 0)));
        times.add(new TimeSlot(DayOfWeek.TUESDAY, LocalTime.of(13, 0, 0), LocalTime.of(14, 30, 0)));

        ScheduleAnalyzer schedule = new ScheduleAnalyzer(students, times);
        List<GeneratedHour> hours = schedule.buildGeneratedHours();
        GeneratedHour bestTime = hours.get(0);
        schedule.setOfficeHour(hours.get(2));
        hours = schedule.buildGeneratedHours();
        bestTime = hours.get(0);
        schedule.setOfficeHour(bestTime);
        float totalAvailable = schedule.getTotalAvailPercent();
        assertEquals((float)80, totalAvailable);
    }
}