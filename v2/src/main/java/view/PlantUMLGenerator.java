package view;

import model.*;
import java.util.List;

public class PlantUMLGenerator {

    public String generatePlantUML(UMLModel umlModel) {
        StringBuilder plantUML = new StringBuilder();

        // PlantUML header
        plantUML.append("@startuml\n");
        plantUML.append("skinparam classAttributeIconSize 0\n");
        plantUML.append("hide circle\n\n");

        // Generate classes
        for (ClassModel classModel : umlModel.getClasses()) {
            generateClass(plantUML, classModel);
            plantUML.append("\n");
        }

        // Generate relationships
        for (DependencyModel relationship : umlModel.getRelationships()) {
            generateRelationship(plantUML, relationship);
        }

        plantUML.append("@enduml");
        return plantUML.toString();
    }

    private void generateClass(StringBuilder plantUML, ClassModel classModel) {
        // Class declaration
        if (classModel.isInterface()) {
            plantUML.append("interface ");
        } else if (classModel.isAbstract()) {
            plantUML.append("abstract class ");
        } else {
            plantUML.append("class ");
        }

        plantUML.append(classModel.getClassName()).append(" {\n");

        // Fields
        for (FieldModel field : classModel.getFields()) {
            plantUML.append("  ");
            plantUML.append(field.getVisibility().getVisibility());

            if (field.isStatic()) {
                plantUML.append("{static} ");
            }

            plantUML.append(field.getName())
                    .append(": ")
                    .append(field.getReturnType());

            if (field.getDeclaredValues() != null) {
                plantUML.append(" = ").append(field.getDeclaredValues());
            }

            plantUML.append("\n");
        }

        // Add separator between fields and methods if both exist
        if (!classModel.getFields().isEmpty() && !classModel.getMethods().isEmpty()) {
            plantUML.append("  --\n");
        }

        // Methods (including constructors)
        for (MethodModel method : classModel.getMethods()) {
            plantUML.append("  ");
            plantUML.append(method.getVisibility().getVisibility());

            if (method.isStatic()) {
                plantUML.append("{static} ");
            }

            // NEW: Check if this is a constructor (no return type or return type is empty)
            boolean isConstructor = method.getReturnType() == null || method.getReturnType().isEmpty();
            
            plantUML.append(method.getName()).append("(");

            // Parameters
            List<ParameterModel> params = method.getParameters();
            for (int i = 0; i < params.size(); i++) {
                ParameterModel param = params.get(i);
                plantUML.append(param.getName())
                        .append(": ")
                        .append(param.getDataType());
                if (i < params.size() - 1) {
                    plantUML.append(", ");
                }
            }

            plantUML.append(")");
            
            // NEW: Only add return type for methods, not constructors
            if (!isConstructor) {
                plantUML.append(": ").append(method.getReturnType());
            }
            
            plantUML.append("\n");
        }

        plantUML.append("}\n");
    }

    private void generateRelationship(StringBuilder plantUML, DependencyModel relationship) {
        plantUML.append(relationship.getFromClass().getClassName())
                .append(" ")
                .append(relationship.getType().getArrow())
                .append(" ")
                .append(relationship.getToClass().getClassName())
                .append("\n");
    }

    public static void main(String[] args) {
        // Create sample UML model for testing
        UMLModel umlModel = new UMLModel();

        // Create Animal class
        ClassModel animal = new ClassModel("Animal");
        animal.addField(new FieldModel(Visibility.PROTECTED, false, false, "String", "name", null));
        animal.addMethod(new MethodModel(Visibility.PUBLIC, false, false, "void", "speak"));

        // Create Dog class with constructor
        ClassModel dog = new ClassModel("Dog");
        
        // Add constructor (empty return type indicates constructor)
        MethodModel dogConstructor = new MethodModel(Visibility.PUBLIC, false, false, "", "Dog");
        dogConstructor.addParameters(new ParameterModel("String", "name"));
        dog.addMethod(dogConstructor);
        
        MethodModel dogSpeak = new MethodModel(Visibility.PUBLIC, false, false, "void", "speak");
        dog.addMethod(dogSpeak);

        // Create Student class with multiple constructors
        ClassModel student = new ClassModel("Student");
        student.addField(new FieldModel(Visibility.PRIVATE, false, false, "String", "name", null));
        student.addField(new FieldModel(Visibility.PRIVATE, false, false, "int", "age", null));
        student.addField(new FieldModel(Visibility.PRIVATE, true, true, "int", "MAX_AGE", "120"));

        // Add default constructor
        MethodModel defaultConstructor = new MethodModel(Visibility.PUBLIC, false, false, "", "Student");
        student.addMethod(defaultConstructor);
        
        // Add parameterized constructor
        MethodModel paramConstructor = new MethodModel(Visibility.PUBLIC, false, false, "", "Student");
        paramConstructor.addParameters(new ParameterModel("String", "name"));
        paramConstructor.addParameters(new ParameterModel("int", "age"));
        student.addMethod(paramConstructor);

        // Add regular methods
        MethodModel getName = new MethodModel(Visibility.PUBLIC, false, false, "String", "getName");
        MethodModel setName = new MethodModel(Visibility.PUBLIC, false, false, "void", "setName");
        setName.addParameters(new ParameterModel("String", "name"));

        MethodModel staticMethod = new MethodModel(Visibility.PUBLIC, true, false, "int", "getMaxAge");

        student.addMethod(getName);
        student.addMethod(setName);
        student.addMethod(staticMethod);

        // Add classes to model
        umlModel.addClassToDiagram(animal);
        umlModel.addClassToDiagram(dog);
        umlModel.addClassToDiagram(student);

        // Add relationships
        umlModel.addRelationshipToDiagram(new DependencyModel(dog, animal, DependencyType.INHERITANCE));

        // Generate and print PlantUML
        PlantUMLGenerator generator = new PlantUMLGenerator();
        String plantUML = generator.generatePlantUML(umlModel);

        System.out.println("Generated PlantUML:");
        System.out.println("==================");
        System.out.println(plantUML);
        System.out.println("==================");
        System.out.println();
        System.out.println("Copy the above PlantUML code to: http://www.plantuml.com/plantuml/uml");
        System.out.println("Or save it to a .puml file and use a PlantUML plugin in your IDE");
    }

}