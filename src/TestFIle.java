public class TestFIle {
    int z;
    String e;
    private int x = 5; // Visibility + Type + Name
    static String name = "A"; // Static + Type + Name
    final double y = 1; // Final + Type + Name
    public static final String s = "s";
    protected String thing;


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
