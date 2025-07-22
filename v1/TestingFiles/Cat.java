package v1.TestingFiles;

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

    @Override
    public void eating(String e) {
        if(e == "2") {
            System.out.println("ewq");
        }
        int x=3;
        while(x == 2) {
            System.out.println("ee");
        }
    }
    
    public void scratch() {
        System.out.println(name + " is scratching the post!");
    }
}

