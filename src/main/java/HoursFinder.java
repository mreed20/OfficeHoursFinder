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
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        Javalin app = Javalin.create(config ->
                // Needed for CSS files.
                config.addStaticFiles("/")
        ).start(7000);

        // Home page handler.
        app.get("/", ctx -> ctx.render("/index.mustache")); //This is a listener, when we enter the website, this is the first file that gets rendered

        // User login handler.
        app.post("/login", HoursFinder::handleLoginPost);

        // Class selection handler.
        app.post("/class_select", HoursFinder::handleSelectClassPost);

    }

    /**
     * Sort a list of classes by week, then by start time.
     */
    private static List<SchoolClass> sortClasses(List<SchoolClass> classes) {
        Comparator<SchoolClass> comp = Comparator.comparing(c -> c.days.get(0));
        comp = comp.thenComparing(Comparator.comparing(c -> c.startTime));
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
            //s += stringToHtmlRow(c.room);
            s += stringToHtmlRow(c.days.toString());
            s += stringToHtmlRow(c.startTime + " - " + c.endTime);
            s += "</tr>";
            rows.add(s);
        }
        return rows;
    }


    /**
     * Handles class selection.
     *
     * @param ctx   HTML context from the client request.
     */
    private static void handleSelectClassPost(Context ctx) {
        // TODO: handle not selecting anything... or just preselect something
        String selection = ctx.formParam("class");
        if (selection == null) {
            ctx.html("<b>SHOULD HAVE SELECTED SOMETHING WHEN U HAD THE CHANCE LOL</b>");
        } else {
            ctx.render("/generatehours.mustache",
                    Map.of("class", selection));
        }
    }


    /**
     * Handles user login.
     *
     * @param ctx   HTML context from the client request.
     */
    private static void handleLoginPost(Context ctx) {
        try {
            int gNumber = Integer.parseInt(Objects.requireNonNull(ctx.formParam("gnumber")));
            Teacher teacher = dc.getTeacher(gNumber);
            if (teacher != null) {

                // Build a model which mustache will parse.
                Map<String, Object> model = new HashMap<>();
                model.put("username", teacher.name);
                // TODO: this line of code gave me cancer, but it works
                model.put("classes", teacher.classes_taught.stream()
                                                    .map(c -> c.name)
                                                    .map(name -> Map.of("class", name))
                                                    .collect(Collectors.toList()));
                ctx.render("/selectclass.mustache", model);
            } else {
                ctx.render("/index.mustache",
                        Map.of("error", "User not found in the database")
                );
            }
        } catch (NumberFormatException e) {
            ctx.render("/index.mustache",
                    Map.of("error", "Invalid input")
            );
        }
    }
}
