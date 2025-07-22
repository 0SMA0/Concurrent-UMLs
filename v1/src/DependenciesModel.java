import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DependenciesModel {
    private final Map<String, Set<String>> dependencies = new HashMap<>();
    private final Map<String, Set<String>> implementations = new HashMap<>();
    private final Map<String, String> inheritance = new HashMap<>();
    private final Map<String, Map<String, AssociationType>> associations = new HashMap<>();

    // Patterns for inheritance and implementation
    private static final Pattern EXTENDS_PATTERN = Pattern.compile("extends\\s+([a-zA-Z_][a-zA-Z0-9_]*)");
    private static final Pattern IMPLEMENTS_PATTERN = Pattern
            .compile("implements\\s+([a-zA-Z_][a-zA-Z0-9_]*)(?:\\s*,\\s*([a-zA-Z_][a-zA-Z0-9_]*))*");
    // Updated type checking
    private static final Set<String> JAVA_LANG_TYPES = Set.of(
            "void", "boolean", "byte", "char", "short", "int", "long", "float", "double",
            "String", "Object", "Number", "Boolean", "Character", "Byte", "Short",
            "Integer", "Long", "Float", "Double");

    private static final Set<String> COLLECTION_TYPES = Set.of(
            "Collection", "List", "Set", "Map", "Queue", "Deque", "ArrayList",
            "LinkedList", "HashSet", "TreeSet", "HashMap", "TreeMap");

    private boolean isCustomClass(String type) {
        if (type == null || type.isEmpty())
            return false;

        // Extract base type (remove generics and arrays)
        String baseType = type.replaceAll("<.*>|\\[\\]", "").trim();

        // Skip if it's a Java/lang type or collection
        return !JAVA_LANG_TYPES.contains(baseType) &&
                !COLLECTION_TYPES.contains(baseType);
    }

    public void analyzeClassDeclaration(String className, String classDeclaration) {
        // First check for implements (interfaces)
        Matcher implementsMatcher = IMPLEMENTS_PATTERN.matcher(classDeclaration);
        if (implementsMatcher.find()) {
            // Handle all implemented interfaces
            for (int i = 1; i <= implementsMatcher.groupCount(); i++) {
                String interfaceName = implementsMatcher.group(i);
                if (interfaceName != null && !interfaceName.trim().isEmpty()) {
                    addImplementation(className, interfaceName.trim());
                }
            }
        }

        // Then check for extends (superclass)
        Matcher extendsMatcher = EXTENDS_PATTERN.matcher(classDeclaration);
        if (extendsMatcher.find()) {
            String parentClass = extendsMatcher.group(1);
            if (!parentClass.trim().isEmpty()) {
                addInheritance(className, parentClass.trim());
            }
        }
    }

    public void analyzeFieldDependencies(UMLModel umlModel) {
        for (Map.Entry<String, List<String>> entry : umlModel.classAttributes.entrySet()) {
            String className = entry.getKey();
            Map<String, String> returnTypes = umlModel.attributeReturnTypes.get(className);

            if (returnTypes != null) {
                for (Map.Entry<String, String> attrEntry : returnTypes.entrySet()) {
                    String fieldType = attrEntry.getValue();
                    if (fieldType.contains("<") && isCustomClass(fieldType)) {
                        processGenericTypes(className, fieldType);
                    }

                    // Only process if it's a custom class (not collection/java.lang)
                    if (isCustomClass(fieldType)) {
                        boolean isFinal = umlModel.attributeFinal.getOrDefault(className, Map.of())
                                .getOrDefault(attrEntry.getKey(), false);

                        AssociationType type = isFinal ? AssociationType.COMPOSITION : AssociationType.AGGREGATION;
                        addAssociation(className, fieldType, type);
                    }
                }
            }
        }
    }

    public void analyzeMethodDependencies(UMLModel umlModel) {
        for (Map.Entry<String, Map<String, String>> classEntry : umlModel.methodParams.entrySet()) {
            String className = classEntry.getKey();

            for (Map.Entry<String, String> methodEntry : classEntry.getValue().entrySet()) {
                String params = methodEntry.getValue();
                if (params != null) {
                    Arrays.stream(params.split(","))
                            .map(String::trim)
                            .filter(p -> !p.isEmpty())
                            .map(p -> p.split("\\s+")[0]) // Get type only
                            .filter(this::isCustomClass)
                            .forEach(paramType -> addDependency(className, paramType));
                }
            }
        }
    }

    private void processGenericTypes(String className, String genericType) {
        // Extract content between angle brackets
        Pattern pattern = Pattern.compile("<([^<>]+)>");
        Matcher matcher = pattern.matcher(genericType);

        while (matcher.find()) {
            String typeParams = matcher.group(1);
            // Split multiple parameters (like Map<K,V>)
            Arrays.stream(typeParams.split("\\s*,\\s*"))
                    .filter(this::isCustomClass)
                    .forEach(customType -> addDependency(className, customType));
        }
    }

    public void addDependency(String sourceClass, String targetClass) {
        dependencies.computeIfAbsent(sourceClass, k -> new HashSet<>()).add(targetClass);
    }

    public void addImplementation(String className, String interfaceName) {
        implementations.computeIfAbsent(className, k -> new HashSet<>()).add(interfaceName);
    }

    public void addInheritance(String childClass, String parentClass) {
        inheritance.put(childClass, parentClass);
    }

    public void addAssociation(String sourceClass, String targetClass, AssociationType type) {
        associations.computeIfAbsent(sourceClass, k -> new HashMap<>())
                .put(targetClass, type);
    }

    public String generatePlantUmlRelationships() {
        StringBuilder sb = new StringBuilder();

        // Generate inheritance relationships
        for (Map.Entry<String, String> entry : inheritance.entrySet()) {
            sb.append(String.format("%s --|> %s\n", entry.getKey(), entry.getValue()));
        }

        // Generate implementation relationships
        for (Map.Entry<String, Set<String>> entry : implementations.entrySet()) {
            String className = entry.getKey();
            for (String interfaceName : entry.getValue()) {
                sb.append(String.format("%s ..|> %s\n", className, interfaceName));
            }
        }

        // Generate association relationships later
        for (Map.Entry<String, Map<String, AssociationType>> sourceEntry : associations.entrySet()) {
            String source = sourceEntry.getKey();
            for (Map.Entry<String, AssociationType> targetEntry : sourceEntry.getValue().entrySet()) {
                String target = targetEntry.getKey();

                sb.append(String.format("%s --> %s\n", source, target));

            }
        }


        return sb.toString();
    }
}