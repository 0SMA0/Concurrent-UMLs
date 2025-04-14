package TestingFiles;

public class Cat implements Animal {
    private String name;

    public Cat(String name) {
        this.name = name;
    }

    @Override
    public void makeSound() {
        System.out.println(name + " says: Meow!");
    }

    @Override
    public void eat() {
        System.out.println(name + " is eating.");
    }

    public void scratch() {
        System.out.println(name + " is scratching the post!");
    }
}

