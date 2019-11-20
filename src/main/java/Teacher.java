import java.util.List;
import java.util.Objects;

public class Teacher
{

    final String name;
    final int gNumber;
    final List<SchoolClass> classesTaught;

    Teacher(String name, int gNumber, List<SchoolClass> classesTaught)
    {
        this.name = name;
        this.gNumber = gNumber;
        this.classesTaught = classesTaught;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teacher teacher = (Teacher) o;
        return gNumber == teacher.gNumber &&
                Objects.equals(name, teacher.name) &&
                Objects.equals(classesTaught, teacher.classesTaught);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, gNumber, classesTaught);
    }
}
