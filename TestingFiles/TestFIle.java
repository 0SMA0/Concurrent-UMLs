package TestingFiles;
import java.util.ArrayList;
import java.util.List;

public class TestFIle {                             //start count brace is 1, but we ignore it
    int z;              //package private
    String e;           //package private
    private int x = 5; // Visibility + Type + Name  
    static String name = "A"; // Static + Type + Name
    final double y = 1; // Final + Type + Name
    public static final String s = "s";
    protected String thing;
    private final String sam = "sam";
    public List<String> hehe = new ArrayList<>();
    private Cat cat;
    private Dog dog;
    // didn't account for methods with the same name, but diff params

    public TestFIle(String e){                      //start count brace is 2           //diff: 2
        if(e == "2") {                              //start count brace is 3           //diff: 3
            this.e = e;                              
        }                                           //ending brace is 1                 //diff: 2
    }                                               //ending brace is now 2             //diff: 1 (CAN PARSE FOR ATTRIBUTES)

    public final int getZ(Dog e) {                       //start count is 4                  //diff: 2
        String rando = "ee";
        return z;
    }                                               //ending brace is 3                 //diff: 1 (CAN PARSE FOR ATTRIBUTES)
    
    public String getE() {
        return e;
    }
    public int getX() {
        return x;
    }
    public static String getName() {
        return name;
    }
    public double getY() {
        return y;
    }
    public static String getS() {
        return s;
    }

    public int numbe;

    public void setS(String s, int one) {

    }


    public String rando;

}
