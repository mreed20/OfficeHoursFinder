import io.javalin.Javalin;

import java.time.*;
import java.util.*;

public class AwesomeWebApp {

    static Map<Integer, SchoolClass> users = new Hashtable<>();

    public static void main(String[] args) {

        users.put(1125901,
                new SchoolClass("CS321",
                        "Expl L003",
                        Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.FRIDAY),
                        LocalTime.of(12, 0),
                        LocalTime.of(13, 15)));

        users.put(1234567,
                new SchoolClass("CS306",
                        "ENGR 2901",
                        Arrays.asList(DayOfWeek.TUESDAY),
                        LocalTime.of(16, 0),
                        LocalTime.of(18, 30)));

        Javalin app = Javalin.create(config ->
                config.addStaticFiles("/public")
        ).start(7000);


        app.post("/login", ctx -> {
            String gNumber = ctx.formParam("gnumber");
            try {
                int userId = Integer.parseInt(gNumber);
                if (users.containsKey(userId)) {
                    ctx.html("Logged in<br>Your schedules are: " + users.get(userId).toString());
                } else {
                    ctx.html(linkToIndex("No user found, click here to go back to login page"));
                }
            } catch (NumberFormatException e) {
                ctx.html(linkToIndex("Number format exception: " + e.getMessage()));
            }
        });
    }

    private static String linkToIndex(String html) {
        return "<a href='index.html'>" + html + "</a>";
    }

}
