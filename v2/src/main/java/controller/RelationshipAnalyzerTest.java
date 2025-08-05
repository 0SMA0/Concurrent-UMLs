package controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import model.UMLModel;
import view.PlantUMLGenerator;

/**
 * Test class demonstrating the RelationshipAnalyzer in action
 */
public class RelationshipAnalyzerTest {
    
    public static void main(String[] args) {
        try {
            // Test with sample Java files
            testRelationshipAnalysis();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void testRelationshipAnalysis() throws Exception {
        System.out.println("=== Testing RelationshipAnalyzer ===\n");
        
        // Create the parser
        JavaFileParser parser = new JavaFileParser();
        
        // Sample files to parse (adjust paths as needed)
        List<Path> testFiles = Arrays.asList(
            Paths.get("v2/test/resources/data/inputfiles/classnotation/Shape.java"),
            Paths.get("v2/test/resources/data/inputfiles/arrows/inheritance/Animal.java"),
            Paths.get("v2/test/resources/data/inputfiles/arrows/inheritance/Dog.java"),
            Paths.get("v2/test/resources/data/inputfiles/arrows/inheritance/Cat.java")
            // Add more test files as needed
        );
        
        // Phase 1: Parse all files
        System.out.println("Phase 1: Parsing Java files...");
        for (Path file : testFiles) {
            if (file.toFile().exists()) {
                System.out.println("Parsing: " + file.getFileName());
                parser.parseFile(file);
            } else {
                System.out.println("File not found: " + file + " (skipping)");
            }
        }
        
        UMLModel umlModel = parser.getUmlModel();
        System.out.println("Parsed " + umlModel.getClasses().size() + " classes\n");
        
        // Phase 2: Analyze relationships
        System.out.println("Phase 2: Analyzing relationships...");
        RelationshipAnalyzer analyzer = new RelationshipAnalyzer(umlModel);
        analyzer.analyzeAllRelationships(parser.getAllClassDeclarations());
        
        // Phase 3: Print results
        System.out.println("\nPhase 3: Results");
        analyzer.printRelationshipSummary();
        
        // Phase 4: Generate PlantUML
        System.out.println("\n=== Generated PlantUML ===");
        PlantUMLGenerator generator = new PlantUMLGenerator();
        String plantUML = generator.generatePlantUML(umlModel);
        System.out.println(plantUML);
        
        System.out.println("\n=== Test Complete ===");
        System.out.println("Copy the PlantUML code above to: http://www.plantuml.com/plantuml/uml");
    }
}