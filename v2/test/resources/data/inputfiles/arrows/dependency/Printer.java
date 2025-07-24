package v2.test.resources.data.inputfiles.arrows.dependency;

public class Printer {
    public void print(Document doc) {
        // Dependency: uses Document temporarily as method parameter
        System.out.println("Printing Document: " + doc.getContent());
    }
}
