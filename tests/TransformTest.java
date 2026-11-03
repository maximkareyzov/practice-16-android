import com.example.practice16.Transform;

public class TransformTest {
    public static void main(String[] args) {
        check(Transform.parseIntOrDefault("20", 19), 20);
        check(Transform.parseIntOrDefault("", 19), 19);
        check(Transform.parseIntOrDefault("abc", 19), 19);
        check(Transform.parseIntOrDefault("9999999999999", 19), 19);
        check(Transform.parseIntOrDefault(null, 19), 19);
        System.out.println("TransformTest: 5 checks passed");
    }
    private static void check(int actual, int expected) {
        if (actual != expected) throw new AssertionError(actual + " != " + expected);
    }
}
