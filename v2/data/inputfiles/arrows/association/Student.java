package v2.data.inputfiles.arrows.association;

public class Student {
    private String name;
    private Course enrolledCourse; // Association: knows about Course

    public Student(String name, Course enrolledCourse) {
        this.name = name;
        this.enrolledCourse = enrolledCourse;
    }

    public String getName() {
        return name;
    }

    public Course getEnrolledCourse() {
        return enrolledCourse;
    }
}
