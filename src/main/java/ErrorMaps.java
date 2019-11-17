import java.util.Map;

// Set of convenience maps for use in Javalin's render() methods.
class ErrorMaps
{
    static final Map<String, String> USER_NOT_FOUND = Map.of("error", "User not found in the database");
    static final Map<String, String> INVALID_INPUT = Map.of("error", "Invalid input");
}
