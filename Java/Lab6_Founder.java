import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public final class Lab6_Founder {
    private static final int TOTAL_THREAD_COUNT = 7;
    private final List<Runnable> workers;
    public Lab6_Founder(final Lab6_Company company) {
        this.workers = new ArrayList<>(company.getDepartmentsCount());
    }

    public void start() {
        for (final Runnable worker : workers) {
            new Thread(worker).start();
        }
    }

    public static void main(String[] args)  {
        try {
            Lab6_Company company = new Lab6_Company(TOTAL_THREAD_COUNT);
            Lab6_Founder founder = new Lab6_Founder(company);

            CountDownLatch latch = new CountDownLatch(TOTAL_THREAD_COUNT);

            for (int i = 0; i < TOTAL_THREAD_COUNT; i++) {
                Lab6_Department freeDepartment = company.getFreeDepartment(i);
                founder.workers.add(new Lab6_Worker(freeDepartment, latch));
            }

            founder.start();
            latch.await();

            company.showCollaborativeResult();
        } catch (InterruptedException e) { }
    }
}