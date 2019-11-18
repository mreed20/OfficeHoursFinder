import io.javalin.Javalin;
import io.javalin.http.Context;

import java.sql.SQLException;
import java.util.*;
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
                    ctx.render(Paths.MUSTACHE_SELECT_AVAILABILITY);
                }
        );

        // Availability selection handler.
        app.post("/select_availability", ctx -> {
                    String selection = ctx.formParam("length");
                    assert selection != null;
                    // TODO: do some stuff
                    renderDisplayGeneratedHours(ctx);
                }
        );

        app.post("/generate_again", ctx -> {
                    String selection = ctx.formParam("selection");
                    if (selection == null) {
                        ctx.html("it's null yo");
                    } else {
                        ctx.html("selection:" + selection);
                    }
                }
        );

    }

    // TODO: take String `selection` as parameter
    private static void renderDisplayGeneratedHours(Context ctx) {
        Map<String, String> model = Map.of(
                "a0", "95",
                "t0", "M 3:00 pm - 5:00 pm",
                "a1", "87",
                "t1", "F 3:30 pm - 5:30 pm",
                "a2", "84",
                "t2", "W 9:15 am - 11:15 am",
                "a3", "71",
                "t3", "Tr 11:45 am - 1:45 pm",
                "a4", "67",
                "t4", "M 8:00 pm - 10:00 pm"
        );
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

    /**
     * Sort a list of classes by week, then by start time.
     */
    private static List<SchoolClass> sortClasses(List<SchoolClass> classes) {
        Comparator<SchoolClass> comp = Comparator.comparing(c -> c.days.get(0));
        comp = comp.thenComparing(c -> c.startTime);
        return classes.stream()
                .sorted(comp)
                .collect(Collectors.toList());
    }


    /**
     * Convert the string s to an HTML row.
     */
    private static String stringToHtmlRow(String s) {
        return "<td>" + s + "</td>";
    }


    /**
     * Convert a list of classes to a row in an HTML table.
     * @param classes  A list of classes.
     * @return An HTML String made of the concatenation of each class
     *         represented as an HTML row.
     */
    private static List<String> classesToHtmlTableRows(List<SchoolClass> classes) {
        List<String> rows = new ArrayList<>();
        for (SchoolClass c : sortClasses(classes)) {
            String s = "<tr>";
            s += stringToHtmlRow(c.name);
            s += stringToHtmlRow(c.days.toString());
            s += stringToHtmlRow(c.startTime + " - " + c.endTime);
            s += "</tr>";
            rows.add(s);
        }
        return rows;
    }


}
