package controller;

import java.lang.module.ModuleDescriptor.Exports.Modifier;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import model.ClassModel;
import model.FieldModel;
import model.UMLModel;
import model.Visibility;

public class JavaFileParser {
    
    private final JavaParser parser = new JavaParser();
    private final UMLModel umlModel = new UMLModel();
    private final ExecutorService executorService;
    private final ReentrantLock reentrantLock = new ReentrantLock();


    public JavaFileParser(){
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
                FieldModel fieldModel = new FieldModel(null, false, false, null, null, null);
                // modifers are in a list and at the first ele, but if empty it will not be taken
                field.getModifiers();

                // System.out.println(" Field: " + field.getElementType() + " " + field.getVariables());
            }

            for (MethodDeclaration method : classInterface.getMethods()) {
                System.out.println(" Method: " + method.getType() + " " + method.getName() + method.getParameters());
            } 
        }
    }

    public static void main(String[] args) {
        JavaFileParser parser = new JavaFileParser();
        Path file2 = Paths.get("v2\\test\\resources\\data\\inputfiles\\classnotation\\Shape.java");
        // Path file2 = Paths.get("v2\\test\\resources\\data\\inputfiles\\arrows\\realization\\PaymentMethod.java");
        System.out.println("");
        System.out.println("");
        try {
            parser.parseFile(file2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}