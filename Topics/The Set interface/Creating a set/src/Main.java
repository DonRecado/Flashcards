import java.util.Set;
import java.util.TreeSet;

public class Main {

    public static void main(String[] args) {
        Set<String> set = new TreeSet<>(Set.of("Gamma", "Alpha", "Omega"));
        System.out.println(set);
    }
}