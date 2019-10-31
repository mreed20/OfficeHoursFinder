import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

class DatabaseConnector {

    // TODO: make this private
    List<SchoolClass> classes;
    Map<Integer, Student> students;

    DatabaseConnector() {
        classes = List.of(
                new SchoolClass("CS321",
                        "Expl L003",
                        Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.FRIDAY),
                        LocalTime.of(12, 0),
                        LocalTime.of(13, 15)),
                new SchoolClass("CS306",
                        "ENGR 2901",
                        Arrays.asList(DayOfWeek.TUESDAY),
                        LocalTime.of(16, 0),
                        LocalTime.of(18, 30))
        );

        students = Map.of(
                1125901,
                new Student("Michael Reed",
                        List.of(classes.get(0))),

                12345678,
                new Student("George Bush",
                        List.of(classes.get(1)))
        );
    }
}
