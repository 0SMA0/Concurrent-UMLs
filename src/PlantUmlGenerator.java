import java.util.ArrayList;
import java.util.List;

public class PlantUmlGenerator {
    public final UMLModel umlModel;
    

    public PlantUmlGenerator (UMLModel umlModel) {
        this.umlModel = umlModel;
    }

    public synchronized void generateToFile(String fileName) {
        StringBuilder uml = new StringBuilder();

        uml.append("@startuml\n");
        uml.append("skinparam classAttributeIconSize 0\n");
        uml.append("hide circle \n");

        String className = this.umlModel.getClassName();

        uml.append("class ").append(className).append(" {\n");
        List<String> attributeInfo = this.umlModel.getAttributesInfo();

        for (String string : attributeInfo) {
            uml.append(string + "\n");
        }

        List<String> methodInfo = this.umlModel.getMethodInfo();

        for (String string : methodInfo) {
            uml.append(string + "\n");
        }

        uml.append("}\n").append("@enduml");

        System.out.println(uml);

    }


}
