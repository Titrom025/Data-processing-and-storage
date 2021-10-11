import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Lab8 implements Callable {
    private static final long SERIES_MAX_DELIMETER = 2_000_000_000;

    private final int threadId;
    private final int totalThreadCount;

    private volatile static boolean isRunning = true;
    private volatile static long maxIteration = 0;

    private static CountDownLatch latch;
    private static final Object obj = new Object();

    public Lab8(int threadId, int totalThreadCount) {
        this.threadId = threadId;
        this.totalThreadCount = totalThreadCount;
    }

    @Override
    public Double call() throws InterruptedException {
        double result = 0;

        long iter = (threadId) * 2L + 1;
        while (isRunning) {
            result += (1.0 / iter * (1 - ((long) (iter / 2) % 2) * 2));
            iter += totalThreadCount * 2L;
        }

        synchronized (obj) {
            if (maxIteration < iter) {
                maxIteration = iter;
            }
        }

        latch.countDown();
        latch.await();

        while (iter < maxIteration) {
            result += (1.0 / iter * (1 - ((long) (iter / 2) % 2) * 2));
            iter += totalThreadCount * 2L;
        }

        System.out.printf("Thread %d, iteration: %d\n", threadId, iter);
        return result;
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        try {
            int totalThreadCount = Integer.parseInt(args[0]);
            latch = new CountDownLatch(totalThreadCount);

            ExecutorService es = Executors.newFixedThreadPool(totalThreadCount);

            List<Future<Double>> futuresValues;
            futuresValues = new ArrayList<>();

            for (int i = 0; i < totalThreadCount; i++){
                Callable callable = new Lab8(i, totalThreadCount);
                Future future = es.submit(callable);
                futuresValues.add(future);
            }

            Runtime.getRuntime().addShutdownHook(new Thread()
            {
                @Override
                public void run()
                {
                    double result = 0;
                    isRunning = false;
                    for (Future<Double> futureValue : futuresValues){
                        try {
                            result += futureValue.get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                    System.out.printf("Result: %s\n", 4 * result);
                    long endTime = System.currentTimeMillis();
                    double duration = (endTime - startTime) / 1000.0;

                    System.out.printf("Time of execution: %s seconds\n", duration);
                }
            });
        }
        catch (NumberFormatException e) {
            System.out.println("Incorrect integer format!");
        }
    }
}
