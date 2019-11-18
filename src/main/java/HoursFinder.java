import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class HoursFinder {

    // For getting all of our data
    private static DatabaseConnector dc;

    public static void main(String[] args) {

        try {
            final String url = "jdbc:postgresql://localhost:5432/postgres";
            final String username = "postgres";
            final String password = "postgresqlmasterpassword";
            dc = new DatabaseConnector(url, username, password);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        Javalin app = Javalin.create(config ->
                // Tells server about our CSS files.
                config.addStaticFiles("/")
        ).start(7000);

        // Home page handler, which gets executed when we first visit the website
        // since we will be at the root directory ("/").
        app.get("/", ctx -> ctx.render(Paths.MUSTACHE_INDEX));


        // User login handler.
        app.post("/login", ctx -> {
                    try {
                        int gNumber = Integer.parseInt(ctx.formParam("gnumber"));
                        if (gNumber < 0 || gNumber > 99999999) {
                            throw new NumberFormatException();
                        }

                        Teacher teacher = dc.getTeacher(gNumber);
                        if (teacher != null) {
                            // Set a cookie on the client so that other pages know
                            // who is logged in, and thus what data to generate.
                            ctx.cookieStore("current_user", gNumber);
                            renderSelectClass(ctx, teacher);
                        } else {
                            ctx.render(Paths.MUSTACHE_INDEX, ErrorMaps.USER_NOT_FOUND);
                        }
                    } catch (NumberFormatException e) {
                        ctx.render(Paths.MUSTACHE_INDEX, ErrorMaps.INVALID_INPUT);
                    }
                }
        );

        // Class selection handler.
        app.post("/select_class", ctx -> {
                    String selection = ctx.formParam("class");
                    // This assertion should never trigger because the radio buttons in `select_class.mustache`
                    // have the `required` attribute, meaning that at least one radio button must be selected
                    // before the browser submits the form.
                    assert selection != null;
                    ctx.cookieStore("current_class", selection);
                    ctx.render(Paths.MUSTACHE_SELECT_AVAILABILITY);
                }
        );

        // Availability selection handler.
        app.post("/select_availability", ctx -> {
                    // Get provided schedule from the HTML POST, which contains information about which
                    // boxes the user checked (the form parameter will be non-null if the box is checked).
                    List<TimeSlot> schedule = new ArrayList<>();
                    for (String key : new String[]{"m", "tu", "w", "tr", "f"}) {
                        if (ctx.formParam(key) != null) {
                            // The user said they are free on this day, so make them available
                            // from 8:00 am to 10:00 pm.
                            TimeSlot t = new TimeSlot(
                                    strToDayOfWeek(key),
                                    LocalTime.of(8, 0),
                                    LocalTime.of(22, 0)
                            );
                            schedule.add(t);
                        }
                    }

                    // Get office hour length.
                    int minutes = Integer.parseInt(Objects.requireNonNull(ctx.formParam("length"))
                            .split(" ")[0]);
                    assert minutes >= 30 && minutes <= 120;
                    Duration d = Duration.of(minutes, ChronoUnit.MINUTES);


                    // Finally generate the requisite time slots needed by ScheduleAnalyzer.
                    List<TimeSlot> timeSlots = HoursGenerator.genHours(d);

                    // Get the name of the currently selected class, which we stored
                    // in a cookie previously.
                    String currentClassName = ctx.cookieStore("current_class");
                    assert currentClassName != null;

                    // Get the list of students in the class.
                    List<Student> students = new ArrayList<>(dc.getStudents(currentClassName));
                    // Generate the office hours.
                    ScheduleAnalyzer analyzer = new ScheduleAnalyzer(students, timeSlots);
                    List<GeneratedHour> hours = analyzer.buildGeneratedHours();
                    renderDisplayGeneratedHours(ctx, hours);
                }
        );

        app.post("/generate_again", ctx -> {
                    String selection = ctx.formParam("selection");
                    assert selection != null;
                    // Clear cookies, which effectively logs the user out
                    ctx.clearCookieStore();
                    // Redirect to home page for now.
                    ctx.redirect("/");
                }
        );

    }


    private static void renderDisplayGeneratedHours(Context ctx, List<GeneratedHour> hours) {
        // This gets rid of duplicates and sorts the set by largest availability
        // percentage first.
        TreeSet<GeneratedHour> sorted = new TreeSet<GeneratedHour>(
                Comparator.comparing(GeneratedHour::getAvailPercent).reversed()
        );
        sorted.addAll(hours);
        // We need at least 5 hours to populate our table.
        assert hours.size() >= 5;

        Iterator<GeneratedHour> it = sorted.iterator();
        Map<String, Object> model = new HashMap<>();
        for (int i = 0; i < 5 && it.hasNext(); i++) {
            GeneratedHour h = it.next();
            model.put("a" + i, String.format("%.2f%%", h.getAvailPercent()));
            model.put("t" + i, h.getTimeSlot().toString());
        }
        ctx.render(Paths.MUSTACHE_DISPLAY_GENERATED_HOURS, model);
    }


    private static void renderSelectClass(Context ctx, Teacher teacher) {
        // Build a model which mustache will parse.
        Map<String, Object> model = new HashMap<>();
        model.put("username", teacher.name);
        model.put("classes", classesToMap(teacher.classes_taught));

        // Render the mustache file with the given model.
        ctx.render(Paths.MUSTACHE_SELECT_CLASS, model);
    }

    private static List<Map<String, String>> classesToMap(List<SchoolClass> classes) {
        // TODO: this line of code gave me cancer, but it works
        return classes.stream()
                .map(c -> c.name)
                .map(name -> Map.of("class", name))
                .collect(Collectors.toList());
    }

    private static DayOfWeek strToDayOfWeek(String s)
    {
        switch (s) {
            case "m":
                return DayOfWeek.MONDAY;
            case "tu":
                return DayOfWeek.TUESDAY;
            case "w":
                return DayOfWeek.WEDNESDAY;
            case "tr":
                return DayOfWeek.THURSDAY;
            case "f":
                return DayOfWeek.FRIDAY;
            default:
                throw new IllegalArgumentException("Failed to convert '" + s + "' to DayOfWeek");
        }
    }

}
