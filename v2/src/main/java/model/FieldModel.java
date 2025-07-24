package v2.src.main.java.model;

public class FieldModel {
    // visiblity
    private Visibility visibility;
    // isStatic
    private boolean isStatic;
    // isFinal
    private boolean isFinal;
    // return type
    private String returnType;
    // field name
    private String name;
    // declared value
    private String declaredValues;

    public FieldModel(Visibility visibility, boolean isStatic, boolean isFinal, String returnType, String name,
            String declaredValues) {
        this.visibility = visibility;
        this.isStatic = isStatic;
        this.isFinal = isFinal;
        this.returnType = returnType;
        this.name = name;
        this.declaredValues = declaredValues;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getName() {
        return name;
    }

    public String getDeclaredValues() {
        return declaredValues;
    }

    @Override
    public String toString() {
        return String.format("FieldModel{name='%s', type='%s', visibility=%s, static=%s, final=%s, default='%s'}",
                name, returnType, visibility, isStatic, isFinal, declaredValues);
    }

}
