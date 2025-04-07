public enum Visibility {
    PUBLIC("public"),
    PRIVATE("private"),
    PROTECTED("protected");

    private final String visibility;

    private Visibility(String visibility) {
        this.visibility = visibility;
    }

    public String getVisibility() {
        return visibility;
    }
}