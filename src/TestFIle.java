import java.util.ArrayList;
import java.util.List;

public class TestFIle {
    int z;              //package private
    String e;           //package private
    private int x = 5; // Visibility + Type + Name  
    static String name = "A"; // Static + Type + Name
    final double y = 1; // Final + Type + Name
    public static final String s = "s";
    protected String thing;
    private final String sam = "sam";
    public List<String> hehe = new ArrayList<>();

    public TestFIle(String e){
        this.e = e;
    }

    public TestFIle(int z){
        this.z = z;
    }

    public final int getZ() {
        return z;
    }
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
    public void setS(String s, int one) {

    }


    

}
