import java.util.List;
import java.util.Objects;

class Student {

    final String name;
    final List<SchoolClass> classes;

    Student(String name, List<SchoolClass> classes) {
        this.name = name;
        this.classes = classes;
    }

    public List<SchoolClass> getClasses(){
    	return this.classes;
    }
    
    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", classes=" + classes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(name, student.name) &&
                Objects.equals(classes, student.classes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, classes);
    }
}
