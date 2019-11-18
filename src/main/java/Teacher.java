import java.util.List;
import java.util.Objects;

public class Teacher
{

    final String name;
    final int Gnumber;
    final List<SchoolClass> classes_taught;

    Teacher(String name, int gnumber, List<SchoolClass> classes_taught)
    {

        this.name = name;
        this.Gnumber = gnumber;
        this.classes_taught = classes_taught;
    }

    public List<SchoolClass> getClasses_taught()
    {
        return this.classes_taught;
    }

    @Override
    public String toString()
    {
        return "Teacher{" +
                "name='" + name + '\'' +
                ", classes taught =" + classes_taught +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teacher teacher = (Teacher) o;
        return Objects.equals(name, teacher.name) &&
                Objects.equals(Gnumber, teacher.Gnumber) &&
                Objects.equals(classes_taught, teacher.classes_taught);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, Gnumber, classes_taught);
    }
}
