import java.io.Serializable;

public class LocalAdder implements Serializable {
    private int base = 10;
    public int addToBase(int test) {
        return test + base;
    }
    public void setBase(int base) {
        this.base = base;
    }
}
