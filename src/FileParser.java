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
   
            private static final Pattern ATTRIBUTE_PATTERN = Pattern
            .compile("^\\s*(?:(public|protected|private|static|final)\\s*)*(\\w+)\\s+(\\w+)\\s*(=.*)?;$");

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
            System.out.println("Class Name: " + this.className);
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
            String visibility = attributeMatcher.group(1); 
            String returnType = attributeMatcher.group(2); 
            String attributeName = attributeMatcher.group(3); 
            String assignment = attributeMatcher.group(4); 
    
            // Check if visibility is null, then assign package-private
            if (visibility == null) {
                visibility = "package-private"; 
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
                // parseAttributeInfo(line);
            }

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
