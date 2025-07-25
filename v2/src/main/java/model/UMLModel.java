package model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class UMLModel {

    private final List<ClassModel> classes = new CopyOnWriteArrayList<>();
    private final List<DependencyModel> relationships = new CopyOnWriteArrayList<>();

    public boolean addClassToDiagram(ClassModel cModel) {
        return classes.add(cModel);
    }

    public boolean addRelationshipToDiagram(DependencyModel dModel) {
        return relationships.add(dModel);
    }

    public List<ClassModel> getClasses() {
        return List.copyOf(classes); // defensive copy
    }

    public List<DependencyModel> getRelationships() {
        return List.copyOf(relationships); // defensive copy
    }

    public void replaceClass(String className, ClassModel newModel) {
        if (className == null || newModel == null || newModel.getClassName() == null)
            return;

        classes.removeIf(c -> c.getClassName().equals(className));
        classes.add(newModel);
    }
}