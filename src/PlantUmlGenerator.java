import java.util.List;

public class PlantUmlGenerator {
    public final UMLModel umlModel;
    

    public PlantUmlGenerator (UMLModel umlModel) {
        this.umlModel = umlModel;
    }

    public synchronized void generateToFile(String fileName) {
        String className = this.umlModel.getClassName();
        
        StringBuilder uml = new StringBuilder();

        uml.append("@startuml\n");
        uml.append("skinparam classAttributeIconSize 0\n");
        uml.append("hide circle \n");
        
        if(className == "") {
            className = this.umlModel.getInterfaceName();
            uml.append("interface ").append(className).append(" {\n");
        } else {
            uml.append("class ").append(className).append(" {\n");
        }

        List<String> attributeInfo = this.umlModel.getAttributesInfo();

        if(attributeInfo != null) {
            for (String string : attributeInfo) {
                uml.append(string + "\n");
            }
        }

        List<String> methodInfo = this.umlModel.getMethodInfo();

        
        if(methodInfo != null) {
            for (String string : methodInfo) {
                uml.append(string + "\n");
            }
        }

        uml.append("}\n").append("@enduml");

        System.out.println(uml);

    }


}
