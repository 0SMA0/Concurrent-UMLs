package controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

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
    private final ExecutorService executorService;
    private final ReentrantLock reentrantLock = new ReentrantLock();

    public JavaFileParser() {
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        System.out.println(this.executorService);
    }

    public void parseFile(Path filePath) throws Exception {
        CompilationUnit cu = parser.parse(filePath).getResult().orElseThrow();
        // System.out.println(d.getNameAsString());
        ClassModel current;
        for (ClassOrInterfaceDeclaration classInterface : cu.findAll(ClassOrInterfaceDeclaration.class)) {
            current = new ClassModel(classInterface.getNameAsString());

            Collection<FieldModel> fieldModels = new ArrayList<>();

            for (FieldDeclaration field : classInterface.getFields()) {

                NodeList<Modifier> modList = field.getModifiers();
                Visibility visibility = getVisibility(modList);
                String returnType = field.getElementType().toString();
                boolean isStatic = field.isStatic();
                boolean isFinal = field.isFinal();
                String declared = field.getVariable(0).getInitializer().toString();
                String name = field.getVariable(0).getNameAsString();
                FieldModel fieldModel = new FieldModel(visibility, isStatic, isFinal, returnType, name, declared);
                fieldModels.add(fieldModel);
            }
            current.addFields(fieldModels);
            Collection<MethodModel> methodModels = new ArrayList<>();
            for (MethodDeclaration method : classInterface.getMethods()) {
                NodeList<Modifier> modList = method.getModifiers();
                Visibility visibility = getVisibility(modList);
                boolean isStatic = method.isStatic();
                boolean isFinal = method.isFinal();
                String returnType = method.getTypeAsString();
                String name = method.getNameAsString();
                MethodModel model = new MethodModel(visibility, isStatic, isFinal, returnType, name);
                NodeList<com.github.javaparser.ast.body.Parameter> params = method.getParameters();
                for (com.github.javaparser.ast.body.Parameter parameter : params) {
                    String paramName = parameter.getNameAsString();
                    String dataType = parameter.getTypeAsString();
                    ParameterModel param = new ParameterModel(dataType, paramName);
                    model.addParameters(param);
                }
                methodModels.add(model);
            }
            current.addMethods(methodModels);
            System.out.println(current.toString());
        }
    }

    public Visibility getVisibility(NodeList<Modifier> modList) {
        // Handle empty modifier list - default to package private
        if (modList.isEmpty()) {
            return Visibility.PACAKAGE_PRIVATE;
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
        return Visibility.PACAKAGE_PRIVATE;
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