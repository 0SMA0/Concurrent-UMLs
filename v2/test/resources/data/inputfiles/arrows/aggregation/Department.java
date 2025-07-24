package v2.test.resources.data.inputfiles.arrows.aggregation;

import java.util.List;

public class Department {
    private String deptName;
    private List<Professor> professors; // Aggregation: Professors can exist independently

    public Department(String deptName, List<Professor> professors) {
        this.deptName = deptName;
        this.professors = professors;
    }

    public String getDeptName() {
        return deptName;
    }

    public List<Professor> getProfessors() {
        return professors;
    }
}
