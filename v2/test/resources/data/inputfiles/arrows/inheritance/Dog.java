package v2.test.resources.data.inputfiles.arrows.inheritance;

public class Dog extends Animal{
    public Dog(String name) {
        super(name);
    }

    @Override
    public void speak() {
        System.out.println(name + " barks.");
    }
}
