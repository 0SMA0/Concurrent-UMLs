package v2.src.main.java.model;

public enum Visibility {
    PUBLIC("+"),
    PRIVATE("-"),
    PROTECTED("#"),
    PACAKAGE_PRIVATE("~");

    private final String visibility;

    private Visibility(String visibility) {
        this.visibility = visibility;
    }

    public String getVisibility() {
        return visibility;
    }
}