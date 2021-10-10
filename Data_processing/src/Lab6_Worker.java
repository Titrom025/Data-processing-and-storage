import java.util.concurrent.CountDownLatch;

public class Lab6_Worker implements Runnable {

    CountDownLatch latch;
    Lab6_Department department;
    public Lab6_Worker(Lab6_Department department, CountDownLatch latch) {
        this.latch = latch;
        this.department = department;
    }

    @Override
    public void run() {
        department.performCalculations();
        latch.countDown();
    }
}
