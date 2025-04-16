package TestingFiles;

public class Dog implements Animal {
    private String name;

    public Dog(String name) {
        this.name = name;
    }

    @Override
    public void makeSound() {
        System.out.println(name + " says: Woof!");
    }

    @Override
    public void eat() {
        System.out.println(name + " is eating.");
    }

    @Override
    public void eating(String e) {
        
    }

    public void fetch() {
        System.out.println(name + " is fetching the ball!");
    }
}
