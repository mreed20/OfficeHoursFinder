import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.*;

public class HoursFinder {

    // For getting all of our data
    private static DatabaseConnector dc = new DatabaseConnector();

    public static void main(String[] args) {

        Javalin app = Javalin.create(config ->
                config.addStaticFiles("/public")
        ).start(7000);

        // Home page handler.
        // TODO: unecessary?
        app.get("/", ctx -> ctx.render("/index.mustache"));

        // User login handler.
        app.post("/login", // TODO: store name in database
                HoursFinder::handleLogin);

    }

    private static void handleLogin(Context ctx) {
        try {
            int gNumber = Integer.parseInt(Objects.requireNonNull(ctx.formParam("gnumber")));
            Student student = dc.students.get(gNumber);
            if (student != null) {
                SchoolClass sc = student.classes.get(0);
                ctx.render("/loginMessage.mustache",
                        Map.of(
                                "username", student.name,
                                "classname", sc.name,
                                "room", sc.room,
                                "days", sc.days,
                                "time", sc.startTime + " - " + sc.endTime
                        )
                );
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
