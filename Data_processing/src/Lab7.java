import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Lab7 implements Callable {
    private static final long SERIES_MAX_DELIMETER = 1_000_000_000;
    private final int threadId;
    private final int totalThreadCount;

    public Lab7(int threadId, int totalThreadCount) {
        this.threadId = threadId;
        this.totalThreadCount = totalThreadCount;
    }

    @Override
    public Double call() {
        double result = 0;
        for (long iter = (threadId) * 2L + 1; iter <= SERIES_MAX_DELIMETER; iter += totalThreadCount * 2L) {
            result += (1.0 / iter * (1 - ((int)(iter / 2) % 2) * 2));
        }
        return result;
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        try {
            int totalThreadCount = Integer.parseInt(args[0]);
            ExecutorService executor;
            executor = Executors.newFixedThreadPool(totalThreadCount);

            List<Future<Double>> futuresValues;
            futuresValues = new ArrayList<>();

            for (int i = 0; i < totalThreadCount; i++){
                Future future;
                Callable callable = new Lab7(i, totalThreadCount);
                future = executor.submit(callable);
                futuresValues.add(future);
            }

            double result = 0;
            for (Future<Double> futureValue : futuresValues){
                result += futureValue.get();
            }

            executor.shutdown();

            System.out.printf("Result: %s\n", 4 * result);
        }
        catch (NumberFormatException e) {
            System.out.println("Incorrect integer format!");
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        double duration = (endTime - startTime) / 1000.0;

        System.out.printf("Time of execution: %s seconds\n", duration);
    }
}
