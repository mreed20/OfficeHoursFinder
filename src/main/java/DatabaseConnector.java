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
        SchoolClass cs321 = new SchoolClass(
                "CS321",
                "Expl L003",
                Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.FRIDAY),
                LocalTime.of(12, 0),
                LocalTime.of(13, 15)
        );
        SchoolClass cs306 = new SchoolClass(
                "CS306",
                "ENGR 2901",
                Arrays.asList(DayOfWeek.TUESDAY),
                LocalTime.of(16, 0),
                LocalTime.of(18, 30)
        );
        SchoolClass cs471 = new SchoolClass("CS471",
                "Peterson 123",
                Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY),
                LocalTime.of(9, 0),
                LocalTime.of(10, 15)
        );
        SchoolClass engh301 = new SchoolClass("ENGH301",
                "Robinson B 113",
                Arrays.asList(DayOfWeek.FRIDAY),
                LocalTime.of(14, 0),
                LocalTime.of(16, 30)
        );
        classes = List.of(cs321, cs306, cs471, engh301);

        students = Map.of(
                1125901,
                new Student("Michael Reed",
                        List.of(cs321, cs306, cs471)),

                12345678,
                new Student("George Bush",
                        List.of(cs471, engh301))
        );
    }
}
