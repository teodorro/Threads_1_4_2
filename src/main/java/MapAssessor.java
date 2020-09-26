import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class MapAssessor {
    public static final int TIMEOUT = 1;
    public static final int NUMBER_TESTINGS = 10;
    private Map<Integer, Integer> mapchik;
    private ExecutorService es;


    public void start(int threadsNum, int bounds, int numberOperations, Map<Integer, Integer> mapchik){
        this.mapchik = mapchik;
        testWritings(threadsNum, bounds, numberOperations);
        testReadings(threadsNum, bounds, numberOperations);
    }

    private void testWritings(int threadsNum, int bounds, int numberOperations) {
        int sum = 0;
        System.out.println(NUMBER_TESTINGS + " writing tests --------");
        for (int i = 0; i < NUMBER_TESTINGS; i++) {
//            System.out.println("test " + (i + 1));
            ArrayList<Integer[]> indices = generateIndicesSequences(threadsNum, bounds, numberOperations);
            sum += testWriting(indices);
        }
        System.out.println("avg writing = " + (sum / NUMBER_TESTINGS) );
    }

    private void testReadings(int threadsNum, int bounds, int numberOperations) {
        int sum = 0;
        System.out.println(NUMBER_TESTINGS + " reading tests --------");
        for (int i = 0; i < NUMBER_TESTINGS; i++) {
//            System.out.println("test " + (i + 1));
            ArrayList<Integer[]> indices = generateIndicesSequences(threadsNum, bounds, numberOperations);
            sum += testReading(indices);
        }
        System.out.println("avg reading = " + (sum / NUMBER_TESTINGS) );
    }

    private long testReading(ArrayList<Integer[]> indices) {
        long timespan = -1;
        try {
            es = Executors.newFixedThreadPool(Main.THREADS_NUM);
            long timeStart = System.currentTimeMillis();
            read(indices);
            es.awaitTermination(TIMEOUT, TimeUnit.SECONDS);
            long timeEnd = System.currentTimeMillis();
            timespan = timeEnd - timeStart - TIMEOUT * 1000;
            System.out.println("reading timespan: " + timespan);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            es.shutdown();
        }
        return timespan;
    }

    private void read(ArrayList<Integer[]> indices) {
        IntStream.range(0, indices.size()).forEach(threadInd -> {
            Integer[] indicesForThread = indices.get(threadInd);
            es.submit(() -> {
                for (int i = 0; i < indicesForThread.length; i++) {
                    Integer a = mapchik.get(indicesForThread[i]);
                }
            });
        });
    }

    private long testWriting(ArrayList<Integer[]> indices) {
        long timespan = -1;
        try {
            es = Executors.newFixedThreadPool(Main.THREADS_NUM);
            long timeStart = System.currentTimeMillis();
            write(indices);
            es.awaitTermination(TIMEOUT, TimeUnit.SECONDS);
            long timeEnd = System.currentTimeMillis();
            timespan = timeEnd - timeStart - TIMEOUT * 1000;
            System.out.println("writing timespan: " + timespan);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            es.shutdown();
        }
        return timespan;
    }

    private ArrayList<Integer[]> generateIndicesSequences(int threadNum, int bounds, int numberOperations) {
        Random rand = new Random(System.currentTimeMillis());
        ArrayList<Integer[]> indices = new ArrayList<>();
        for (int i = 0; i < threadNum; i++) {
            Integer[] indicesForThread = new Integer[numberOperations];
            for (int j = 0; j < numberOperations; j++) {
                indicesForThread[j] = rand.nextInt(bounds);
            }
            indices.add(indicesForThread);
        }
        return indices;
    }

    private void write(ArrayList<Integer[]> indices) {
        IntStream.range(0, indices.size()).forEach(threadInd -> {
                Integer[] indicesForThread = indices.get(threadInd);
                es.submit(() -> {
                    for (int i = 0; i < indicesForThread.length; i++) {
                        mapchik.put(indicesForThread[i], threadInd);
                    }
                });
        });
    }
}
