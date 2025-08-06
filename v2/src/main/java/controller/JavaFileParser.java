package controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
// Later use 
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
// import java.util.concurrent.locks.ReentrantLock;

import com.github.javaparser.JavaParser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import model.ClassModel;
import model.FieldModel;
import model.MethodModel;
import model.ParameterModel;
import model.UMLModel;
import model.Visibility;

public class JavaFileParser {

    private final JavaParser parser = new JavaParser();
    private final UMLModel umlModel = new UMLModel();
    private final List<ClassOrInterfaceDeclaration> classDeclarations = new ArrayList<>();

    // private final ExecutorService executorService;
    // private final ReentrantLock reentrantLock = new ReentrantLock();

    // public JavaFileParser() {
    //     this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    //     System.out.println(this.executorService);
    // }

    public void parseFile(Path filePath) throws Exception {
        CompilationUnit cu = parser.parse(filePath).getResult().orElseThrow();

        for (ClassOrInterfaceDeclaration classInterface : cu.findAll(ClassOrInterfaceDeclaration.class)) {
            classDeclarations.add(classInterface);
            ClassModel classModel = parseClass(classInterface);
            umlModel.addClassToDiagram(classModel);
            System.out.println(classModel.toString());
        }
    }

    public List<ClassOrInterfaceDeclaration> getAllClassDeclarations() {
        return Collections.unmodifiableList(classDeclarations);
    }

    private ClassModel parseClass(ClassOrInterfaceDeclaration classInterface) {
        ClassModel classModel = new ClassModel(classInterface.getNameAsString());

        // Set class type flags
        classModel.setIsInterface(classInterface.isInterface());
        classModel.setIsAbstract(classInterface.isAbstract());

        // Parse and add fields
        Collection<FieldModel> fields = parseFields(classInterface);
        classModel.addFields(fields);

        // Parse and add methods
        Collection<MethodModel> methods = parseMethods(classInterface);
        classModel.addMethods(methods);

        return classModel;
    }

    private Collection<FieldModel> parseFields(ClassOrInterfaceDeclaration classInterface) {
        Collection<FieldModel> fieldModels = new ArrayList<>();

        for (FieldDeclaration field : classInterface.getFields()) {
            NodeList<Modifier> modList = field.getModifiers();
            Visibility visibility = getVisibility(modList);
            String returnType = field.getElementType().toString();
            boolean isStatic = field.isStatic();
            boolean isFinal = field.isFinal();

            // Handle multiple variables in one declaration (e.g., int x, y, z;)
            field.getVariables().forEach(variable -> {
                FieldModel fieldModel = createFieldModel(variable, visibility, isStatic, isFinal, returnType);
                fieldModels.add(fieldModel);
            });
        }

        return fieldModels;
    }

    private FieldModel createFieldModel(com.github.javaparser.ast.body.VariableDeclarator variable,
            Visibility visibility, boolean isStatic, boolean isFinal, String returnType) {
        String name = variable.getNameAsString();
        String declared = variable.getInitializer()
                .map(Object::toString)
                .orElse(null);

        return new FieldModel(visibility, isStatic, isFinal, returnType, name, declared);
    }

    private Collection<MethodModel> parseMethods(ClassOrInterfaceDeclaration classInterface) {
        Collection<MethodModel> methodModels = new ArrayList<>();

        for (MethodDeclaration method : classInterface.getMethods()) {
            MethodModel methodModel = parseMethod(method);
            methodModels.add(methodModel);
        }

        return methodModels;
    }

    private MethodModel parseMethod(MethodDeclaration method) {
        NodeList<Modifier> modList = method.getModifiers();
        Visibility visibility = getVisibility(modList);
        boolean isStatic = method.isStatic();
        boolean isFinal = method.isFinal();
        String returnType = method.getTypeAsString();
        String name = method.getNameAsString();

        MethodModel methodModel = new MethodModel(visibility, isStatic, isFinal, returnType, name);

        // Parse and add parameters
        Collection<ParameterModel> parameters = parseParameters(method);
        parameters.forEach(methodModel::addParameters);

        return methodModel;
    }

    private Collection<ParameterModel> parseParameters(MethodDeclaration method) {
        Collection<ParameterModel> parameters = new ArrayList<>();

        NodeList<com.github.javaparser.ast.body.Parameter> params = method.getParameters();
        for (com.github.javaparser.ast.body.Parameter parameter : params) {
            String paramName = parameter.getNameAsString();
            String dataType = parameter.getTypeAsString();
            ParameterModel param = new ParameterModel(dataType, paramName);
            parameters.add(param);
        }

        return parameters;
    }

    public UMLModel getUmlModel() {
        return umlModel;
    }

    public Visibility getVisibility(NodeList<Modifier> modList) {
        // Handle empty modifier list - default to package private
        if (modList.isEmpty()) {
            return Visibility.PACKAGE_PRIVATE;
        }

        // Look through all modifiers to find visibility modifier
        for (Modifier modifier : modList) {
            String modifierStr = modifier.toString().trim();
            switch (modifierStr) {
                case "public":
                    return Visibility.PUBLIC;
                case "private":
                    return Visibility.PRIVATE;
                case "protected":
                    return Visibility.PROTECTED;
            }
        }

        // If no visibility modifier found, default to package private
        return Visibility.PACKAGE_PRIVATE;
    }

    public static void main(String[] args) {
        JavaFileParser parser = new JavaFileParser();
        Path file2 = Paths.get("v2\\test\\resources\\data\\inputfiles\\classnotation\\Shape.java");
        // Path file2 =
        // Paths.get("v2\\test\\resources\\data\\inputfiles\\arrows\\realization\\PaymentMethod.java");
        System.out.println("");
        System.out.println("");
        try {
            parser.parseFile(file2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}