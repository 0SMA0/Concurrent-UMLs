# UML Generator for Java

In software development, planning and implementation rarely align perfectly. Diagrams created early in the process can quickly become outdated—relationships shift, fields and methods change, and classes are deprecated or removed. **UML Generator for Java** bridges that gap by creating UML diagrams directly from your source code, ensuring you always have accurate, up-to-date representations that reflect the real structure of your project.

## Features
- **Generate UML from File or Folder**  
  Right-click on a Java file or folder in the Explorer to instantly generate UML.
- **Accurate Parsing**  
  Powered by a custom UML parser built with **JavaParser AST** for precise extraction of:
  - Classes
  - Interfaces
  - Attributes (with visibility modifiers)
  - Methods (with visibility modifiers)
  - Optional relationships
- **PlantUML Output**  
  Produces valid `.puml` files with `@startuml` and `@enduml` tags.
- **Custom Output Directory**  
  Choose where generated `.puml` files are saved. (currently produced at the root directory of the project to a folder, "output")
- **Automatic Preview**  
  Opens the generated file automatically in VS Code.
- **Integration-Ready**  
  Works seamlessly with PlantUML Preview extensions or external PlantUML renderers.

## Installation
1. Install from the VS Code Marketplace:  
   [UML Generator for Java](https://marketplace.visualstudio.com/items?itemName=0SMA0.uml-generator)

## Usage

### Generate from File
1. Right-click on a `.java` file in the Explorer.
2. Select **"Generate UML from File"**.
3. The `.puml` file will be created in the specified output directory.

### Generate from Folder
1. Right-click on a folder containing Java files.
2. Select **"Generate UML from Folder"**.
3. All Java files in that folder will be parsed and included in the diagram.

### Open in PlantUML Preview
After generation, open the `.puml` file in any PlantUML-compatible previewer to view the diagram.

![demo](resources\ex.gif)

## Future Features
- Downloading the diagrams
- Add stereotypes such as `ConcreteVisitor`, `Visitor`, `Element`, `Invoker`, `Action`, etc.
- Derive sequence diagrams from `main()`.
- Integrate AI assistance for designing according to functional and non-functional requirements.
- Support additional text-based diagramming tools such as Mermaid.
- Add support for other programming languages.

## Acknowledgments
This project uses [JavaParser](https://javaparser.org/) licensed under the Apache 2.0 License and [PlantUML](https://plantuml.com/) licensed under the MIT License.  
We gratefully acknowledge these open-source projects for their valuable tools.

