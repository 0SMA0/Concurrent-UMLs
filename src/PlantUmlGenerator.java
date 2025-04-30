import java.util.HashSet;
import java.util.List;

public class PlantUmlGenerator {
    public UMLModel umlModel;
    public StringBuilder current;
    public String past;
    private HashSet<String> pastClassNames = new HashSet<>();
    public StringBuilder allCurrents = new StringBuilder();
    
    public PlantUmlGenerator (UMLModel umlModel) {
        this.umlModel = umlModel;
        this.current = new StringBuilder();
    }

    public void setUML(UMLModel umlModel) {
        this.umlModel = umlModel;
    }

    private void setCurrent(StringBuilder current) {
        this.current = current;
    }
    public StringBuilder getCurrent(){
        return this.current;
    }
    public StringBuilder putItAllTogether(){
        return allCurrents;
    }

    public synchronized StringBuilder genSingleClass() {
        current = new StringBuilder();
        StringBuilder uml = new StringBuilder();
        uml.append("@startuml\n");
        uml.append("skinparam classAttributeIconSize 0\n");
        uml.append("hide circle \n");
        
        // Check for interface first
        if (!umlModel.getInterfaceName().isBlank()) {
            String interfaceName = umlModel.getInterfaceName();
            uml.append("interface ").append(interfaceName).append(" {\n");
            
            List<String> methodInfo = umlModel.getMethodInfo();
            if (methodInfo != null) {
                for (String string : methodInfo) {
                    uml.append(string + "\n");
                }
            }
        } 
        // Then check for class
        else if (!umlModel.getClassName().isBlank()) {
            String className = umlModel.getClassName();
            uml.append("class ").append(className).append(" {\n");
            
            List<String> attributeInfo = umlModel.getAttributesInfo();
            if (attributeInfo != null) {
                for (String string : attributeInfo) {
                    uml.append(string + "\n");
                }
            }
            
            List<String> methodInfo = umlModel.getMethodInfo();
            if (methodInfo != null) {
                for (String string : methodInfo) {
                    uml.append(string + "\n");
                }
            }
        }
        
        uml.append("}\n").append("@enduml");
        uml.append("\n").append(umlModel.getDependenciesModel().generatePlantUmlRelationships());
        setCurrent(uml);
        
        // Reset the model after generating the UML
        this.umlModel.resetMethods();
        this.umlModel.resetVisibility();
        this.umlModel.resetAttributes();
        this.allCurrents.append(uml);
        
        return uml;
    }
    




}
