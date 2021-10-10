import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Lab13 implements Runnable {
    private static final int PHILOSOPHER_COUNT = 5;
    private static final int FORK_COUNT = 5;
    private static ReentrantLock[] forks = new ReentrantLock[5];
    private static final Random random = new Random();

    private static final ReentrantLock forkLock = new ReentrantLock();
    private static final Condition tryGrabForks = forkLock.newCondition();

    private final int philosopher_id;
    private boolean philosopherHasEaten = false;

    public Lab13(int philosopher_id) {
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

            forkLock.lock();
            while (!philosopherHasEaten) {

                System.out.printf("Philosopher %s - capture lock\n", philosopher_id);

                if (forks[philosopher_id].tryLock()) {
                    System.out.printf("Philosopher %s - capture: %s\n", philosopher_id, philosopher_id);

                    if (forks[((philosopher_id + 1) % PHILOSOPHER_COUNT)].tryLock()) {
                        System.out.printf("Philosopher %s - capture: %s\n", philosopher_id, (philosopher_id + 1) % FORK_COUNT);
                        forkLock.unlock();
                        System.out.printf("Philosopher %s - release lock\n", philosopher_id);
                        System.out.printf("Eating - id: %s\n", philosopher_id);
                        philosopherHasEaten = true;

                        try { Thread.sleep(random.nextInt(5000) + 500); }
                        catch (InterruptedException e) { e.printStackTrace(); }

                        forks[((philosopher_id + 1) % PHILOSOPHER_COUNT)].unlock();
                        System.out.printf("Philosopher %s - release: %s\n", philosopher_id, (philosopher_id + 1) % FORK_COUNT);
                    }
                    forks[philosopher_id].unlock();
                    System.out.printf("Philosopher %s - release: %s\n", philosopher_id, philosopher_id);
                }

                if (philosopherHasEaten) {
                    forkLock.lock();
                    System.out.printf("Philosopher %s - notify\n", philosopher_id);
                    tryGrabForks.signal();
                    forkLock.unlock();
                    break;
                }
                else {
                    System.out.printf("Philosopher %s - release lock\n", philosopher_id);
                    try { tryGrabForks.await(); }
                    catch (InterruptedException e) { e.printStackTrace(); }
                }
            }

            philosopherHasEaten = false;
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < FORK_COUNT; i++) {
//            forks.add(new ReentrantLock());
            forks[i] = new ReentrantLock();
        }

        for (int i = 0; i < PHILOSOPHER_COUNT; i++) {
            Lab13 philosopher = new Lab13(i);
            new Thread(philosopher).start();
        }
    }
}
