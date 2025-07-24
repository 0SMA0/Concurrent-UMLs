package v2.src.main.java.model;

public class DependencyModel {
    private final ClassModel fromClass;
    private final ClassModel toClass;
    private DependencyType type;

    public DependencyModel(ClassModel fromClass, ClassModel toClass, DependencyType type) {
        this.fromClass = fromClass;
        this.toClass = toClass;
        this.type = type;
    }

    public ClassModel getFromClass() {
        return fromClass;
    }

    public ClassModel getToClass() {
        return toClass;
    }

    public DependencyType getType() {
        return type;
    }

    public void setType(DependencyType new_Type) {
        this.type = new_Type;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        DependencyModel that = (DependencyModel) obj;
        return fromClass.getClassName().equals(that.fromClass.getClassName()) &&
                toClass.getClassName().equals(that.toClass.getClassName()) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(fromClass.getClassName(), toClass.getClassName(), type);
    }

    @Override
    public String toString() {
        String fromClassName = this.fromClass.getClassName();
        String toClassName = this.toClass.getClassName();
        String arrow = this.type.getArrow();
        String printOut = fromClassName + arrow + toClassName;
        return printOut;
    }

}
