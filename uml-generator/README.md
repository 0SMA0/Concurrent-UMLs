# UML Generator VS Code Extension

A **Visual Studio Code extension** that generates PlantUML diagrams directly from Java source files or folders using your custom UML parser (powered by **JavaParser AST** for accuracy) and outputs `.puml` files ready for preview or rendering.

This extension is built to support **real-time, detailed UML generation** with robust error handling, customizable output options, and optional automatic file opening.

---

## ✨ Features

- **Generate UML from File or Folder**
  - Right-click on a Java file or folder in the Explorer to generate UML.
- **Accurate Parsing**
  - Uses a custom UML parser built with **JavaParser AST** for highly accurate extraction of:
    - Classes
    - Interfaces
    - Attributes (with visibility modifiers)
    - Methods (with visibility modifiers)
    - Relationships (optional)
- **PlantUML Output**
  - Output files are valid `.puml` files containing `@startuml` and `@enduml`.
- **Custom Output Directory**
  - Configure where generated `.puml` files are saved.
- **Verbose Logging**
  - Optional detailed logging for debugging.
- **Automatic Preview**
  - Auto-opens the generated file in VS Code.
- **Integration-Ready**
  - Works with PlantUML Preview extensions or external PlantUML renderers.

---

## 🚀 Installation

1. Clone this repository or install from the VS Code Marketplace (if published).
2. Ensure you have **Java** installed and available in your system path.
3. Place your `uml-generator.jar` (built from your UML parser project) in:
   - A custom path you configure, **or**
   - `resources/uml-generator.jar` inside the extension folder.

---

## ⚙️ Configuration

Open VS Code settings (`Ctrl+,`) and search for **umlGenerator**:

| Setting                         | Description                                                          | Default |
|---------------------------------|----------------------------------------------------------------------|---------|
| `umlGenerator.jarPath`          | Path to the `uml-generator.jar` file                                 | *(auto-detect)* |
| `umlGenerator.includeRelationships` | Whether to include relationship arrows in the diagram               | `true`  |
| `umlGenerator.verbose`          | Enable verbose console output from the Java process                  | `false` |
| `umlGenerator.autoOpenFile`     | Automatically open generated `.puml` file in VS Code                 | `true`  |
| `umlGenerator.outputDirectory`  | Custom output folder for generated UML files (relative to workspace) | *(same as input)* |

---

## 📂 Usage

### **Generate from File**
1. Right-click on a `.java` file in the Explorer.
2. Select **"Generate UML from File"**.
3. The `.puml` file will be created in the output directory.

### **Generate from Folder**
1. Right-click on a folder containing Java files.
2. Select **"Generate UML from Folder"**.
3. All Java files in that folder will be parsed and included in the diagram.

### **Open in PlantUML Preview**
- After generation, you can open the `.puml` file in any PlantUML-compatible previewer.

---

## 🛠 How It Works

### **Architecture**
- **VS Code Extension** (TypeScript)  
  - Handles user commands, progress UI, file path resolution, and running the Java process.
- **UML Parser JAR** (Java, powered by JavaParser)  
  - Parses Java source files into a UML model.
  - Extracts:
    - Class names
    - Attributes (with `public`, `private`, `protected`)
    - Methods (with `public`, `private`, `protected`)
    - Relationships (association, dependency, etc.)
  - Outputs PlantUML syntax.

### **Flow**
1. User triggers `Generate UML` command.
2. Extension resolves paths and reads settings.
3. Extension spawns `java -jar uml-generator.jar ...`.
4. Parser processes Java files into `.puml`.
5. Extension validates output and optionally opens it.

---

## 🔍 Error Handling

The extension includes robust error detection:
- Missing JAR file → prompts user to set path or browse.
- Java process errors → displays message with details.
- Empty or invalid `.puml` → warns user.
- Non-zero Java process exit codes → logs stderr.

---

## 📦 Building the UML Parser JAR

This extension expects your UML parser to be built into `uml-generator.jar`.

Your parser:
- Uses **JavaParser AST** for accurate parsing.
- Supports real-time updates and complex Java structures.
- Includes `attributeVisibility` and `methodVisibility` tracking maps.
- Generates `.puml` formatted output.

To build:
```bash
mvn clean package
# Result: target/uml-generator.jar
