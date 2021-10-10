import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Lab10 extends Thread {

    private static final ReentrantLock lock = new ReentrantLock();
    private static final Condition childTurn = lock.newCondition();
    private static final Condition mainTurn = lock.newCondition();


    @Override
    public void run() {
        lock.lock();
        mainTurn.signal();
        lock.unlock();

        for (int i = 0; i < 10; i++) {
            lock.lock();
            try {
                childTurn.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("Thread #2 - Line: %d\n", i);
            mainTurn.signal();
            lock.unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Lab10 t = new Lab10();
        t.start();

        for (int i = 0; i < 10; i++) {
            lock.lock();
            try {
                mainTurn.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("Main thread - Line: %d\n", i);
            childTurn.signal();
            lock.unlock();
        }
    }
}
