package v2.src.main.java.model;

import java.util.ArrayList;
import java.util.List;

public class MethodModel {
    // visiblity
    private Visibility visibility;
    // isStatic
    private boolean isStatic;
    // isFinal
    private boolean isFinal;
    // return type
    private String returnType;
    // method name
    private String name;
    // params (k: name: v:type)
    private final List<ParameterModel> parameters = new ArrayList<>();

    public MethodModel(Visibility visibility, boolean isStatic, boolean isFinal, String returnType, String name) {
        this.visibility = visibility;
        this.isStatic = isStatic;
        this.isFinal = isFinal;
        this.returnType = returnType;
        this.name = name;
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

    public List<ParameterModel> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return String.format("MethodModel{name='%s', returnType='%s', visibility=%s, static=%s, final=%s, params=%s}",
                name, returnType, visibility, isStatic, isFinal, parameters);
    }

}
