import java.sql.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Lab9Philosopher implements Runnable {
    private static final int PHILOSOPHER_COUNT = 5;
    private static final int FORK_COUNT = 5;
    private static final List<ReentrantLock> forks = new ArrayList<>(5);
    private static final Random random = new Random();

    private final int philosopher_id;

    public Lab9Philosopher(int philosopher_id) {
        this.philosopher_id = philosopher_id;
    }

    @Override
    public void run() {
        while (true) {
            try {
                System.out.printf("Philosopher %s - thinking\n", philosopher_id);
                Thread.sleep(random.nextInt(5000) + 500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (forks.get((philosopher_id - 1 + FORK_COUNT) % FORK_COUNT).tryLock()) {
                System.out.printf("Philosopher %s - capture: %s\n", philosopher_id, philosopher_id % FORK_COUNT);
                if (forks.get((philosopher_id + 1) % PHILOSOPHER_COUNT).tryLock()) {
                    System.out.printf("Philosopher %s - capture: %s\n", philosopher_id, (philosopher_id + 1) % FORK_COUNT);
                    try {
                        System.out.printf("Eating - id: %s\n", philosopher_id);
                        Thread.sleep(random.nextInt(5000) + 500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    forks.get((philosopher_id + 1) % PHILOSOPHER_COUNT).unlock();
                    System.out.printf("Philosopher %s - release: %s\n", philosopher_id, (philosopher_id + 1) % FORK_COUNT);
                }
                forks.get((philosopher_id - 1 + FORK_COUNT) % FORK_COUNT).unlock();
                System.out.printf("Philosopher %s - release: %s\n", philosopher_id, philosopher_id % FORK_COUNT);
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < FORK_COUNT; i++) {
            forks.add(new ReentrantLock());
        }

        for (int i = 0; i < PHILOSOPHER_COUNT; i++) {
            Lab9Philosopher philosopher = new Lab9Philosopher(i);
            new Thread(philosopher).start();
        }
    }
}
