package v2.test.resources.data.inputfiles.classnotation;

/*
 * This is used to test:
 *      Class name
 *      
 *      Field:
 *          - Field Visibility:
 *              - private
 *              + public
 *              # protected         
 *              ~ package private
 *          - Other field keywords
 *              - Static
 *              - final
 *          - Return type
 *              - primitives: int, double, boolean, char, byte, short, long, and float
 *              - class types: String, ArrayList<T>
 *      
 *      Constructors
 *      
 *      Methods:
 *          Visibility
 *          Synchronized (keyword will be used for sequence diagrams)
 *          Static
 *          final
 *          Return type
 *          name
 *          parameter name and return type
 */

@SuppressWarnings("unused")
public class Shape {
    private int length;
    public String name;
    protected char symbol;
    String color;
    private static int random1;

    private static final int random2 = 3;

    public Shape(int length, String name, char symbol, String color) {
        this.length = length;
        this.name = name;
        this.symbol = symbol;
        this.color = color;
    }

    public int getLength() {
        return length;
    }

    public String getName() {
        return name;
    }

    public char getSymbol() {
        return symbol;
    }

    public String getColor() {
        return color;
    }

    private void setLength(int length) {
        this.length = length;
    }

    // packaged private
    void setName(String name) {
        this.name = name;
    }

    protected void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    public static final void setColor(String color) {
        return;
    }

}
