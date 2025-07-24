package v2.test.resources.data.inputfiles.arrows.inheritance;

public class Cat extends Animal {
    public Cat(String name) {
        super(name);
    }

    @Override
    public void speak() {
        System.out.println(name + " meows.");
    }
}
