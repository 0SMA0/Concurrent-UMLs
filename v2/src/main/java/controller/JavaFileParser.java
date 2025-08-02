package controller;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

public class JavaFileParser {
    
    private final JavaParser parser = new JavaParser();

    public void parseFile(Path filePath) throws Exception {
        CompilationUnit cu = parser.parse(filePath).getResult().orElseThrow();

        for (ClassOrInterfaceDeclaration type : cu.findAll(ClassOrInterfaceDeclaration.class)) {
            System.out.println(type.getNameAsString());
            for (FieldDeclaration field : type.getFields()) {
                System.out.println(" Field: " + field.getElementType() + " " + field.getVariables());
            }

            for (MethodDeclaration method : type.getMethods()) {
                System.out.println(" Method: " + method.getType() + " " + method.getName() + method.getParameters());
            } 
        }



    }

    public static void main(String[] args) {
        JavaFileParser parser = new JavaFileParser();
        // Path file = Paths.get("v2\\test\\resources\\data\\inputfiles\\classnotation\\Shape.java");
        Path file2 = Paths.get("v2\\test\\resources\\data\\inputfiles\\arrows\\realization\\PaymentMethod.java");

        try {
            parser.parseFile(file2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}