import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class UMLModel {
    private final Set<String> classNames = new HashSet<>();
    private final Set<String> interfaceNames = new HashSet<>();
    // private final Set<String> enumNames = new HashSet<>();
    private final Set<String> abstractNames = new HashSet<>();

    // Visability:
    // Key: Attibute Name / Method Name ; Value: visability
    private final Map<String, String> attributeVisibility = new HashMap<>();
    private final Map<String, String> methodVisibility = new HashMap<>();

    // Names:
    // Key: Class Name ; Value: list of attributes / methods
    private final Map<String, List<String>> classAttributes = new HashMap<>();
    private final Map<String, List<String>> classMethods = new HashMap<>();

    // Return Types:
    // Key 1: class name, Key 2: method name, Value: return type
    private final Map<String, Map<String, String>> attributeReturnTypes = new HashMap<>();
    private final Map<String, Map<String, String>> methodReturnTypes = new HashMap<>();

    // Keep track of initialized attributes:
    private final Map<String, Map<String, String>> attributeInitialValues = new HashMap<>();

    // Dependencies
    // private final DependenciesModel dependenciesModel = new DependenciesModel();

    // Check for "final" and "static" keyword
    // Key: clasName ; Value: attr/meth ; is__
    private final Map<String, Map<String, Boolean>> attributeFinal = new HashMap<>();
    private final Map<String, Map<String, Boolean>> methodFinal = new HashMap<>();
    private final Map<String, Map<String, Boolean>> attributeStatic = new HashMap<>();
    private final Map<String, Map<String, Boolean>> methodStatic = new HashMap<>();

    /*
     * 
     * What is ReentrantLock (a thread that already holds the lock can acquire it
     * again without deadlocking itself):
     * To ensure only one thread at a time can execute a critical section of code
     * - Preventing race condtions: whoever gets to it first will modify it, but
     * then it won't be in the right order
     * 
     * This is more flexible than Java's synchronized keyword since it
     * - Allows for explicit locking/unlocking
     * - supports fair locking: first-first, first-served order (like a queue)
     * - Allows try-locking: attempting to aquire a lock without blocking forever
     * blocking: waiting for a specific condition to be met before it can proceed
     * 
     * tldr: if it's the same thread it can proceed without blocking
     */
    private final ReentrantLock lock = new ReentrantLock();

    public void addClass(String className) {
        lock.lock();
        try {
            classNames.add(className);
            classMethods.putIfAbsent(className, new ArrayList<>());
            classAttributes.putIfAbsent(className, new ArrayList<>());
            System.out.println("Added to UML Class: CLASS NAME");
        } finally {
            lock.unlock();
        }
    }

    public void addInterface(String interfaceName) {
        lock.lock();
        try {
            interfaceNames.add(interfaceName);
            classMethods.putIfAbsent(interfaceName, new ArrayList<>());
        } finally {
            lock.unlock();
        }
    }

    public void addAbstract(String abstractName) {
        lock.lock();
        try {
            abstractNames.add(abstractName);
            classMethods.putIfAbsent(abstractName, new ArrayList<>());
            classAttributes.putIfAbsent(abstractName, new ArrayList<>());
        } finally {
            lock.unlock();
        }
    }

    public void addAttribute(String className, String attributeName) {
        lock.lock();
        try {
            if (attributeName != null && !attributeName.isEmpty()) {
                classAttributes.computeIfAbsent(className, k -> new ArrayList<>()).add(attributeName);
            }
        } finally {
            lock.unlock();
        }
    }

    public void addMethod(String className, String method) {
        lock.lock();
        try {
            classMethods.computeIfAbsent(className, k -> new ArrayList<>()).add(method);
        } finally {
            lock.unlock();
        }
    }

    public void addAttributeVisibility(String attributeName, String visibility) {
        lock.lock();
        try {
            if (visibility != null) {
                attributeVisibility.put(attributeName, visibility);
            }
        } finally {
            lock.unlock();
        }
    }

    public void addMethodVisibility(String methodName, String visibility) {
        lock.lock();
        try {
            methodVisibility.put(methodName, visibility);
        } finally {
            lock.unlock();
        }
    }

    public void addMethodReturnType(String className, String methodName, String returnType) {
        lock.lock();
        try {
            methodReturnTypes.computeIfAbsent(className, k -> new HashMap<>()).put(methodName, returnType);
        } finally {
            lock.unlock();
        }
    }

    public void addAttributeReturnType(String className, String attributeName, String returnType) {
        lock.lock();
        try {
            if (returnType != null && !returnType.isEmpty()) {
                attributeReturnTypes.computeIfAbsent(className, k -> new HashMap<>()).put(attributeName, returnType);
            }
        } finally {
            lock.unlock();
        }
    }

    public void addAttributeFinal(String className, String attributeName, Boolean isFinal) {
        // Key: className ; Value: attributeName, isFinal
        lock.lock();
        try {
            if (isFinal != null) {
                attributeFinal.computeIfAbsent(className, k -> new HashMap<>()).put(attributeName, isFinal);
            }
        } finally {
            lock.unlock();
        }
    }

    public void addAttributeStatic(String className, String attributeName, Boolean isStatic) {
        // Key: className ; Value: attributeName, isStatic
        lock.lock();
        try {
            if (isStatic != null) {
                attributeStatic.computeIfAbsent(className, k -> new HashMap<>()).put(attributeName, isStatic);
            }
        } finally {
            lock.unlock();
        }
    }

    public void addMethodFinal(String className, String methodName, Boolean isFinal) {
        // Key: className ; Value: attributeName, isFinal
        lock.lock();
        try {
            methodFinal.computeIfAbsent(className, k -> new HashMap<>()).put(methodName, isFinal);
        } finally {
            lock.unlock();
        }
    }

    public void addMethodStatic(String className, String methodName, Boolean isStatic) {
        // Key: className ; Value: methodName, isStatic
        lock.lock();
        try {
            methodStatic.computeIfAbsent(className, k -> new HashMap<>()).put(methodName, isStatic);
        } finally {
            lock.unlock();
        }
    }

    public void addAttributeAssignment(String className, String attributeName, String assignmentValue) {
        lock.lock();
        try {
            if (assignmentValue != null && !assignmentValue.isEmpty()) {
                attributeInitialValues
                        .computeIfAbsent(className, k -> new HashMap<>())
                        .put(attributeName, assignmentValue);
            }
        } finally {
            lock.unlock();
        }
    }

    

    @Override
    public String toString() {
        String s = "UML MODEL: ";
        // do a switch case for when it is a class, interface, 
        
        s += "\nClass Name: " + this.classNames + '\n';
        s += "Attribute Visibility: " + this.attributeVisibility + "\n";
        s += "Attributes: " + this.classAttributes.values() + "\n";
        s += "Attribute Return types: " + this.attributeReturnTypes.values();

        return s;
    }

} 