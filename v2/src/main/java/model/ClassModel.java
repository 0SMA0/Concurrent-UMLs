package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ClassModel {
    // class name
    private final String className;
    // class fields
    private final List<FieldModel> fields = new ArrayList<>();
    // class methods
    private final List<MethodModel> methods = new ArrayList<>();

    private boolean isInterface;
    private boolean isAbstract;

    public ClassModel(String className) {
        this.className = className;
    }

    public boolean addField(FieldModel field) {
        if (field == null)
            return false;
        return fields.add(field);
    }

    public boolean addMethod(MethodModel method) {
        if (method == null)
            return false;
        return methods.add(method);
    }

    public boolean addFields(Collection<FieldModel> fieldsList) {
        if (fieldsList != null) {
            boolean success = false;
            for (FieldModel field : fieldsList) {
                if (addField(field)) {
                    success = true;
                }
            }
            return success;
        }
        return false;
    }

    public boolean addMethods(Collection<MethodModel> methodsList) {
        if (methodsList != null) {
            boolean success = false;
            for (MethodModel method : methodsList) {
                if (addMethod(method)) {
                    success = true;
                }
            }
            return success;
        }
        return false;
    }

    public String getClassName() {
        return className;
    }

    public List<FieldModel> getFields() {
        return Collections.unmodifiableList(fields);
    }

    public List<MethodModel> getMethods() {
        return Collections.unmodifiableList(methods);
    }

    @Override
    public String toString() {
        return "ClassModel{" +
                "className='" + className + '\'' +
                ", fields=" + fields +
                ", methods=" + methods +
                '}';
    }

    public boolean isInterface() {
        return isInterface;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setIsInterface(boolean set) {
        this.isInterface = set;
    }

    public void setIsAbstract(boolean set) {
        this.isAbstract = set;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ClassModel that = (ClassModel) obj;
        return className.equals(that.className);
    }

    @Override
    public int hashCode() {
        return className.hashCode();
    }

    public static void main(String[] args) {
        FieldModel field = new FieldModel(Visibility.PRIVATE, false, false, null, "e", null);
        ClassModel d = new ClassModel("d");
        d.addField(field);
        System.out.println(d.toString());
    }
}
