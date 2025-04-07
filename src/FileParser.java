import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileParser implements Runnable {

    private final String filePath;
    private final UMLModel umlModel;
    private String className;

    // Patterns
    private static final Pattern CLASS_PATTERN = Pattern
            .compile("^\\s*(?:public\\s+)?class\\s+([a-zA-Z_][a-zA-Z0-9_]*)", Pattern.MULTILINE);
    private static final Pattern INTERFACE_PATTERN = Pattern
            .compile("^\\s*(?:public\\s+)?interface\\s+([a-zA-Z_][a-zA-Z0-9_]*)", Pattern.MULTILINE);
    private static final Pattern ABSTRACT_CLASS_PATTERN = Pattern
            .compile("^\\s*(?:public\\s+)?abstract\\s+class\\s+([a-zA-Z_][a-zA-Z0-9_]*)", Pattern.MULTILINE);

    private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile(
            "^\\s*(public|private|protected)?\\s*" +
                    "(static\\s+)?(final\\s+)?" +
                    "([\\w<>\\[\\]]+)\\s+" +
                    "([a-zA-Z_][a-zA-Z0-9_]*)" +
                    "\\s*(=.*)?;");

    private static final Pattern METHOD_PATTERN = Pattern.compile(
            "^\\s*(public|private|protected)?\\s*(static)?\\s*(final)?\\s*([\\w<>\\[\\]]+)\\s+(\\w+)\\s*\\(([^)]*)\\)\\s*(throws\\s+[\\w<>, ]+)?\\s*\\{?");

    public FileParser(String filePath, UMLModel umlModel) {
        this.filePath = filePath;
        this.umlModel = umlModel;
    }

    private void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }

    private void parseClassInterfaceAbstractName(String line) {
        Matcher classMatcher = CLASS_PATTERN.matcher(line);
        Matcher interfaceMatcher = INTERFACE_PATTERN.matcher(line);
        Matcher abstractMatcher = ABSTRACT_CLASS_PATTERN.matcher(line);

        if (classMatcher.find()) {
            String className = classMatcher.group(1);
            setClassName(className);
            umlModel.addClass(className);
        } else if (interfaceMatcher.find()) {
            String interfaceName = interfaceMatcher.group(1);
            setClassName(interfaceName);
            umlModel.addInterface(interfaceName);
        } else if (abstractMatcher.find()) {
            String abstractName = abstractMatcher.group(1);
            setClassName(abstractName);
            umlModel.addAbstract(abstractName);
        }
    }

    private void parseAttributeInfo(String line) {
        Matcher attributeMatcher = ATTRIBUTE_PATTERN.matcher(line);
        if (attributeMatcher.find()) {
            // Get the groups for visibility, return type, attribute name, and assignment
            String visibility = attributeMatcher.group(1); // will always stay the same

            // Can add a ternary to switch the groups
            Boolean isStatic = (attributeMatcher.group(2) == "static") ? true : null;
            Boolean isFinal = (attributeMatcher.group(3) == "final") ? true : null;
            String returnType = attributeMatcher.group(4);
            String attributeName = attributeMatcher.group(5);
            String attributeAssignment = attributeMatcher.group(6);

            // Check if visibility is null, then assign package-private
            if (visibility == null) {
                visibility = "package-private";
            }

            umlModel.addAttribute(this.className, attributeName);
            umlModel.addAttributeVisibility(attributeName, visibility);
            umlModel.addAttributeStatic(this.className, attributeName, isStatic);
            umlModel.addAttributeFinal(this.className, attributeName, isFinal);
            umlModel.addAttributeReturnType(this.className, attributeName, returnType);
            umlModel.addAttributeAssignment(this.className, attributeName, attributeAssignment);

        }
    }

    private void parseMethods(String line) {
        Matcher methodMatcher = METHOD_PATTERN.matcher(line);

        // need to develop a pattern for constructor

        if (methodMatcher.find()) {
            String visibility = methodMatcher.group(1);
            Boolean isStatic = (methodMatcher.group(2) == "static") ? true : null;
            Boolean isFinal = (methodMatcher.group(3) == "final") ? true : null;
            String returnType = methodMatcher.group(4);
            String methodName = methodMatcher.group(5);
            String params = methodMatcher.group(6);

            // Taking into account for the constructor
            if ((visibility == null) && (isStatic == null) && (isFinal == null)) {
                visibility = methodMatcher.group(4);
                methodName = methodMatcher.group(5);
                params = methodMatcher.group(6);
                umlModel.addMethod(this.className, methodName);
                umlModel.addMethodVisibility(methodName, visibility);
                umlModel.addMethodParams(this.className, methodName, params);
            } else {
                umlModel.addMethodVisibility(methodName, visibility);
                umlModel.addMethod(this.className, methodName);
                umlModel.addMethodReturnType(this.className, methodName, returnType);
                umlModel.addMethodStatic(this.className, methodName, isStatic);
                umlModel.addMethodFinal(this.className, methodName, isFinal);
                umlModel.addMethodParams(this.className, methodName, params);

            }

        }

    }

    @Override
    public void run() {

        try {
            List<String> lines = Files.readAllLines(new File(filePath).toPath());
            // System.out.println(lines);
            for (String line : lines) {
                parseClassInterfaceAbstractName(line);
                parseAttributeInfo(line);
                parseMethods(line);
            }

            System.out.println(umlModel.toString());
        } catch (IOException e) {
            System.err.println("Error parsing file: " + e);
        }
    }

    public static void main(String[] args) {
        UMLModel model = new UMLModel();
        FileParser parser = new FileParser("src//TestFIle.java", model);
        Thread thread = new Thread(parser);
        thread.start();
    }

}
