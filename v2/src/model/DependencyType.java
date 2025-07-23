package v2.src.model;

public enum DependencyType {
    AGGREGATION("o--"),
    ASSOCIATION("-->"),
    COMPOSITION("*--"),
    DEPENDENCY("..>"),
    INHERITANCE("--|>"),
    REALIZATION("..|>");

    private final String arrow;

    private DependencyType(String arrow) {
        this.arrow = arrow;
    }

    public String getArrow() {
        return arrow;
    }

}
