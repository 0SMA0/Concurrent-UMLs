package controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

import model.ClassModel;
import model.DependencyModel;
import model.DependencyType;
import model.UMLModel;

public class RelationshipAnalyzer {
    
    private final UMLModel umlModel;
    private final Set<String> availableClasses;
    private final Map<String, ClassModel> classModelMap;
    private final Map<String, ClassOrInterfaceDeclaration> astNodeMap;
    
    public RelationshipAnalyzer(UMLModel umlModel) {
        this.umlModel = umlModel;
        this.availableClasses = new HashSet<>();
        this.classModelMap = new HashMap<>();
        this.astNodeMap = new HashMap<>();
        
        // Populate available classes from the UML model
        for (ClassModel classModel : umlModel.getClasses()) {
            String className = classModel.getClassName();
            availableClasses.add(className);
            classModelMap.put(className, classModel);
        }
    }
    
    /**
     * Main method to analyze all relationships between classes
     */
    public void analyzeAllRelationships(List<ClassOrInterfaceDeclaration> classDeclarations) {
        // First, build the AST node map
        buildASTNodeMap(classDeclarations);
        
        // Then analyze relationships for each class
        for (ClassOrInterfaceDeclaration classDecl : classDeclarations) {
            String className = classDecl.getNameAsString();
            ClassModel fromClass = classModelMap.get(className);
            
            if (fromClass != null) {
                System.out.println("Analyzing relationships for: " + className);
                analyzeClassRelationships(classDecl, fromClass);
            }
        }
        
        System.out.println("Relationship analysis complete. Found " + 
                         umlModel.getRelationships().size() + " relationships.");
    }
    
    private void buildASTNodeMap(List<ClassOrInterfaceDeclaration> classDeclarations) {
        for (ClassOrInterfaceDeclaration classDecl : classDeclarations) {
            astNodeMap.put(classDecl.getNameAsString(), classDecl);
        }
    }
    
    /**
     * Analyzes all types of relationships for a single class
     */
    private void analyzeClassRelationships(ClassOrInterfaceDeclaration classDecl, ClassModel fromClass) {
        // 1. Inheritance relationships (extends, implements)
        analyzeInheritanceRelationships(classDecl, fromClass);
        
        // 2. Field-based relationships (composition, aggregation, association)
        analyzeFieldRelationships(classDecl, fromClass);
        
        // 3. Method-based relationships (dependency)
        analyzeMethodRelationships(classDecl, fromClass);
        
        // 4. NEW: Constructor-based relationships (dependency)
        analyzeConstructorRelationships(classDecl, fromClass);
    }
    
    /**
     * Analyzes inheritance (extends) and realization (implements) relationships
     */
    private void analyzeInheritanceRelationships(ClassOrInterfaceDeclaration classDecl, ClassModel fromClass) {
        // Check extends relationships (inheritance)
        classDecl.getExtendedTypes().forEach(extendedType -> {
            String parentClassName = extractClassName(extendedType);
            if (availableClasses.contains(parentClassName)) {
                ClassModel toClass = classModelMap.get(parentClassName);
                if (toClass != null && !relationshipExists(fromClass, toClass, DependencyType.INHERITANCE)) {
                    DependencyModel dependency = new DependencyModel(fromClass, toClass, DependencyType.INHERITANCE);
                    umlModel.addRelationshipToDiagram(dependency);
                    System.out.println("  Found inheritance: " + fromClass.getClassName() + " --|> " + parentClassName);
                }
            }
        });
        
        // Check implements relationships (realization)
        classDecl.getImplementedTypes().forEach(implementedType -> {
            String interfaceName = extractClassName(implementedType);
            if (availableClasses.contains(interfaceName)) {
                ClassModel toClass = classModelMap.get(interfaceName);
                if (toClass != null && !relationshipExists(fromClass, toClass, DependencyType.REALIZATION)) {
                    DependencyModel dependency = new DependencyModel(fromClass, toClass, DependencyType.REALIZATION);
                    umlModel.addRelationshipToDiagram(dependency);
                    System.out.println("  Found realization: " + fromClass.getClassName() + " ..|> " + interfaceName);
                }
            }
        });
    }
    
    /**
     * Analyzes field-based relationships (composition, aggregation, association)
     */
    private void analyzeFieldRelationships(ClassOrInterfaceDeclaration classDecl, ClassModel fromClass) {
        for (FieldDeclaration field : classDecl.getFields()) {
            // Analyze each variable in the field declaration
            for (VariableDeclarator variable : field.getVariables()) {
                analyzeFieldVariable(field, variable, fromClass);
            }
        }
    }
    
    private void analyzeFieldVariable(FieldDeclaration field, VariableDeclarator variable, ClassModel fromClass) {
        Type fieldType = field.getElementType();
        String fieldName = variable.getNameAsString();
        
        // Handle direct class references
        String directClassName = extractClassName(fieldType);
        if (availableClasses.contains(directClassName)) {
            DependencyType relationshipType = determineFieldRelationshipType(field, fieldName, directClassName);
            addFieldRelationship(fromClass, directClassName, relationshipType);
        }
        
        // Handle generic types (List<Class>, Set<Class>, Map<Key, Value>, etc.)
        analyzeGenericTypes(fieldType, fromClass, fieldName);
        
        // Handle array types (Class[], Class[][])
        analyzeArrayTypes(fieldType, fromClass);
    }
    
    /**
     * Analyzes generic types like List<Person>, Map<String, Order>
     */
    private void analyzeGenericTypes(Type fieldType, ClassModel fromClass, String fieldName) {
        if (fieldType.isClassOrInterfaceType()) {
            ClassOrInterfaceType classType = fieldType.asClassOrInterfaceType();
            
            if (classType.getTypeArguments().isPresent()) {
                classType.getTypeArguments().get().forEach(typeArg -> {
                    String genericClassName = extractClassName(typeArg);
                    if (availableClasses.contains(genericClassName)) {
                        // Collections typically indicate aggregation
                        addFieldRelationship(fromClass, genericClassName, DependencyType.AGGREGATION);
                        System.out.println("  Found generic aggregation: " + fromClass.getClassName() + 
                                         " o-- " + genericClassName + " (via " + fieldName + ")");
                    }
                });
            }
        }
    }
    
    /**
     * Analyzes array types like Person[], Order[][]
     */
    private void analyzeArrayTypes(Type fieldType, ClassModel fromClass) {
        if (fieldType.isArrayType()) {
            Type componentType = fieldType.asArrayType().getComponentType();
            String arrayClassName = extractClassName(componentType);
            if (availableClasses.contains(arrayClassName)) {
                // Arrays typically indicate aggregation
                addFieldRelationship(fromClass, arrayClassName, DependencyType.AGGREGATION);
                System.out.println("  Found array aggregation: " + fromClass.getClassName() + 
                                 " o-- " + arrayClassName + " (array)");
            }
        }
    }
    
    /**
     * Determines the type of field relationship based on various heuristics
     */
    private DependencyType determineFieldRelationshipType(FieldDeclaration field, String fieldName, String targetClassName) {
        String fieldNameLower = fieldName.toLowerCase();
        
        // Composition indicators (stronger ownership)
        if (field.isFinal() || 
            fieldNameLower.contains("part") || 
            fieldNameLower.contains("component") ||
            fieldNameLower.contains("detail") ||
            (field.getModifiers().size() > 0 && field.isPrivate())) {
            return DependencyType.COMPOSITION;
        }
        
        // Aggregation indicators (weaker ownership, collections)
        if (isCollectionType(field.getElementType()) ||
            fieldNameLower.contains("list") ||
            fieldNameLower.contains("set") ||
            fieldNameLower.contains("collection") ||
            fieldNameLower.contains("items") ||
            fieldNameLower.endsWith("s")) { // plural names often indicate collections
            return DependencyType.AGGREGATION;
        }
        
        // Default to association for simple references
        return DependencyType.ASSOCIATION;
    }
    
    /**
     * Checks if a type represents a collection (List, Set, Map, etc.)
     */
    private boolean isCollectionType(Type type) {
        if (!type.isClassOrInterfaceType()) {
            return false;
        }
        
        String typeName = type.asClassOrInterfaceType().getNameAsString();
        Set<String> collectionTypes = Set.of(
            "List", "ArrayList", "LinkedList",
            "Set", "HashSet", "TreeSet", "LinkedHashSet",
            "Map", "HashMap", "TreeMap", "LinkedHashMap",
            "Collection", "Queue", "Deque"
        );
        
        return collectionTypes.contains(typeName);
    }
    
    /**
     * Analyzes method-based relationships (dependencies)
     */
    private void analyzeMethodRelationships(ClassOrInterfaceDeclaration classDecl, ClassModel fromClass) {
        for (MethodDeclaration method : classDecl.getMethods()) {
            // Analyze method parameters
            for (Parameter param : method.getParameters()) {
                String paramClassName = extractClassName(param.getType());
                if (availableClasses.contains(paramClassName) && 
                    !hasStrongerRelationship(fromClass, paramClassName)) {
                    
                    addMethodRelationship(fromClass, paramClassName, "parameter in " + method.getNameAsString());
                }
            }
            
            // Analyze return type
            String returnClassName = extractClassName(method.getType());
            if (availableClasses.contains(returnClassName) && 
                !hasStrongerRelationship(fromClass, returnClassName)) {
                
                addMethodRelationship(fromClass, returnClassName, "return type of " + method.getNameAsString());
            }
        }
    }
    
    /**
     * NEW: Analyzes constructor-based relationships (dependencies)
     */
    private void analyzeConstructorRelationships(ClassOrInterfaceDeclaration classDecl, ClassModel fromClass) {
        for (ConstructorDeclaration constructor : classDecl.getConstructors()) {
            // Analyze constructor parameters
            for (Parameter param : constructor.getParameters()) {
                String paramClassName = extractClassName(param.getType());
                if (availableClasses.contains(paramClassName) && 
                    !hasStrongerRelationship(fromClass, paramClassName)) {
                    
                    addMethodRelationship(fromClass, paramClassName, "constructor parameter");
                }
            }
        }
    }
    
    /**
     * Adds a field-based relationship if it doesn't already exist
     */
    private void addFieldRelationship(ClassModel fromClass, String toClassName, DependencyType relationshipType) {
        ClassModel toClass = classModelMap.get(toClassName);
        if (toClass != null && !relationshipExists(fromClass, toClass, relationshipType)) {
            DependencyModel dependency = new DependencyModel(fromClass, toClass, relationshipType);
            umlModel.addRelationshipToDiagram(dependency);
            System.out.println("  Found " + relationshipType.name().toLowerCase() + ": " + 
                             fromClass.getClassName() + " " + relationshipType.getArrow() + " " + toClassName);
        }
    }
    
    /**
     * Adds a method-based dependency relationship
     */
    private void addMethodRelationship(ClassModel fromClass, String toClassName, String context) {
        ClassModel toClass = classModelMap.get(toClassName);
        if (toClass != null && !relationshipExists(fromClass, toClass, DependencyType.DEPENDENCY)) {
            DependencyModel dependency = new DependencyModel(fromClass, toClass, DependencyType.DEPENDENCY);
            umlModel.addRelationshipToDiagram(dependency);
            System.out.println("  Found dependency: " + fromClass.getClassName() + " ..> " + toClassName + 
                             " (" + context + ")");
        }
    }
    
    /**
     * Checks if a stronger relationship already exists between two classes
     * Hierarchy: Inheritance > Realization > Composition > Aggregation > Association > Dependency
     */
    private boolean hasStrongerRelationship(ClassModel fromClass, String toClassName) {
        ClassModel toClass = classModelMap.get(toClassName);
        if (toClass == null) return false;
        
        // Check if any stronger relationship already exists
        return relationshipExists(fromClass, toClass, DependencyType.INHERITANCE) ||
               relationshipExists(fromClass, toClass, DependencyType.REALIZATION) ||
               relationshipExists(fromClass, toClass, DependencyType.COMPOSITION) ||
               relationshipExists(fromClass, toClass, DependencyType.AGGREGATION) ||
               relationshipExists(fromClass, toClass, DependencyType.ASSOCIATION);
    }
    
    /**
     * Checks if a specific relationship already exists between two classes
     */
    private boolean relationshipExists(ClassModel fromClass, ClassModel toClass, DependencyType type) {
        return umlModel.getRelationships().stream()
            .anyMatch(rel -> rel.getFromClass().equals(fromClass) && 
                           rel.getToClass().equals(toClass) && 
                           rel.getType() == type);
    }
    
    /**
     * Extracts the simple class name from a Type, handling generics and arrays
     */
    private String extractClassName(Type type) {
        if (type.isClassOrInterfaceType()) {
            return type.asClassOrInterfaceType().getNameAsString();
        } else if (type.isArrayType()) {
            return extractClassName(type.asArrayType().getComponentType());
        } else if (type.isPrimitiveType()) {
            return type.asPrimitiveType().getType().asString();
        }
        return type.toString();
    }
    
    /**
     * Gets relationship statistics for debugging/reporting
     */
    public void printRelationshipSummary() {
        Map<DependencyType, Integer> counts = new HashMap<>();
        
        for (DependencyModel rel : umlModel.getRelationships()) {
            counts.merge(rel.getType(), 1, Integer::sum);
        }
        
        System.out.println("\n=== Relationship Analysis Summary ===");
        System.out.println("Total relationships found: " + umlModel.getRelationships().size());
        for (Map.Entry<DependencyType, Integer> entry : counts.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        
        System.out.println("\n=== All Relationships ===");
        for (DependencyModel rel : umlModel.getRelationships()) {
            System.out.println(rel.toString());
        }
    }
}