import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.synchronizedMap;

public class Main {
    public static final int THREADS_NUM = 99;
    public static final int ARRAY_SIZE = 999;
    public static final int NUMBER_OPERATIONS = 299999;

    public static void main(String[] args) {
        System.out.println("threads number: " + THREADS_NUM);
        System.out.println("array size: " + ARRAY_SIZE);
        System.out.println("number of operations: " + NUMBER_OPERATIONS);
        System.out.println("\n========= ConcurrentHashMap ===========\n");
        new MapAssessor().start(THREADS_NUM, ARRAY_SIZE, NUMBER_OPERATIONS, new ConcurrentHashMap());
        System.out.println("\n========= synchronizedMap ===========\n");
        new MapAssessor().start(THREADS_NUM, ARRAY_SIZE, NUMBER_OPERATIONS, synchronizedMap(new HashMap<>()));
    }
}
