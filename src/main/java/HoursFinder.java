import com.github.mustachejava.util.DecoratedCollection;
import io.javalin.Javalin;
import io.javalin.http.Context;

import javax.annotation.processing.Generated;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

class HoursFinder
{
    // For getting all of our data
    private static DatabaseConnector dc = null;

    // Used for parsing in days of the week.
    private static final DateTimeFormatter dayOfWeekFormatter =
            DateTimeFormatter.ofPattern("E");

    // Ugly hack to make list of time slots accessible across pages.
    // Should probably use sessions instead.
    private static final AtomicReference<List<TimeSlot>> selectedTimes =
            new AtomicReference<>();
    private static AtomicReference<List<GeneratedHour>> top10 =
            new AtomicReference<>();

    public static void main(String[] args)
    {
        // THIS IS A HACK
        selectedTimes.set(new ArrayList<>());

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
        app.get("/", ctx -> ctx.render(Constants.MUSTACHE_INDEX));


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
                            ctx.cookieStore(Constants.COOKIE_CURRENT_USER, gNumber);
                            // Used for later after the user selects office hours.
                            renderSelectClass(ctx, teacher);
                        } else {
                            ctx.render(Constants.MUSTACHE_INDEX, ErrorMaps.USER_NOT_FOUND);
                        }
                    } catch (NumberFormatException e) {
                        ctx.render(Constants.MUSTACHE_INDEX, ErrorMaps.INVALID_INPUT);
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
                    ctx.cookieStore(Constants.COOKIE_CURRENT_CLASS, selection);
                    ctx.render(Constants.MUSTACHE_SELECT_AVAILABILITY);
                }
        );

        // Availability selection handler.
        app.post("/select_availability", ctx -> {
                    // Get provided schedule from the HTTP POST, which contains information about which
                    // boxes the user checked (the form parameter will be non-null if the box is checked).
                    List<DayOfWeek> daysAvailable = new ArrayList<>();
                    // These are the names of checkbox elements in select_availability.mustache.
                    String[] dayCheckboxes = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri"};
                    for (String key : dayCheckboxes) {
                        if (ctx.formParam(key) != null) {
                            // The user said they are free on this day.
                            TemporalAccessor t = dayOfWeekFormatter.parse(key);
                            daysAvailable.add(DayOfWeek.from(t));
                        }
                    }

                    // Get office hour length.
                    int minutes = Integer.parseInt(Objects.requireNonNull(ctx.formParam("length"))
                            .split(" ")[0]);
                    assert minutes >= 30 && minutes <= 120;
                    Duration d = Duration.of(minutes, ChronoUnit.MINUTES);

                    // Finally generate the requisite time slots needed by ScheduleAnalyzer.
                    List<TimeSlot> timeSlots = HoursGenerator.genHours(d, daysAvailable);

                    // Get the name of the currently selected class, which we stored
                    // in a cookie previously.
                    String currentClassName = ctx.cookieStore(Constants.COOKIE_CURRENT_CLASS);
                    assert currentClassName != null;

                    // Get the list of students in the class.
                    List<Student> students = new ArrayList<>(dc.getStudents(currentClassName));
                    // Generate the office hours.
                    ScheduleAnalyzer analyzer = new ScheduleAnalyzer(students, timeSlots);

                    // Tell the ScheduleAnalyzer to account for previously selected office hours when scheduling.
                    for (TimeSlot t : selectedTimes.get()) {
                        analyzer.setOfficeHour(t);
                    }

                    // Finally generate the office hours.
                    List<GeneratedHour> hours = analyzer.buildGeneratedHours();
                    renderDisplayGeneratedHours(ctx, hours);
                }
        );

        app.post("/generate_again", ctx -> {
                    String nextPage = ctx.formParam("next_page");
                    switch (nextPage) {
                        case "same_class":
                            // Store the selected time slot.
                            int i = Integer.parseInt(ctx.formParam("timeslot_selection"));
                            TimeSlot t = top10.get().get(i).getTimeSlot();
                            selectedTimes.get().add(t);

                            // Bring the user back to the availability selection screen
                            ctx.render(Constants.MUSTACHE_SELECT_AVAILABILITY);
                            break;
                        case "new_class":
                            // Clear, but do not delete, the selected hours list.
                            // TODO: move this stuff inside main so we dont need to use an AtomicReference
                            selectedTimes.get().clear();

                            // Bring the user back to the class selection page.
                            int gNumber = ctx.cookieStore(Constants.COOKIE_CURRENT_USER);
                            Teacher teacher = dc.getTeacher(gNumber);
                            renderSelectClass(ctx, teacher);
                            break;
                        default:
                            throw new RuntimeException();
                    }
                }
        );

        app.before("/logout", ctx -> {
                    // Clear cookies.
                    ctx.clearCookieStore();
                    // Redirect to home page.
                    ctx.redirect("/logged_out.html");
                }
        );
    }

    private static void renderDisplayGeneratedHours(Context ctx, List<GeneratedHour> hours)
    {
        Map<String, Object> model = new HashMap<>();
        // Get the current class from the cookie store.
        // We use it as a caption for the table of generated hours.
        model.put("classname", ctx.cookieStore(Constants.COOKIE_CURRENT_CLASS));

        // TODO: test that no hours coincide with the class being selected
        hours.sort(Comparator.comparing(GeneratedHour::getAvailPercent).reversed());

        // Get the top 5 time slots by availability percentage...
        top10.set(hours.stream().limit(10).collect(Collectors.toList()));
        // ... and wrap said time slots in a DecoratedCollection, which will
        // expose {{index}} tags (via the iterator) to the underlying mustache
        // template.
        model.put("hours", new DecoratedCollection<>(top10.get()));

        ctx.render(Constants.MUSTACHE_DISPLAY_GENERATED_HOURS, model);
    }


    private static void renderSelectClass(Context ctx, Teacher teacher)
    {
        // Build a model which mustache will parse.
        Map<String, Object> model = new HashMap<>();
        model.put("username", teacher.name);
        model.put("classes", teacher.classesTaught);

        // Render the mustache file with the given model.
        ctx.render(Constants.MUSTACHE_SELECT_CLASS, model);
    }
}
