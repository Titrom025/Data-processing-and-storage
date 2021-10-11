import java.util.concurrent.Semaphore;

public class Lab14 {
    private static final Semaphore AObject = new Semaphore(0);
    private static final Semaphore BObject = new Semaphore(0);
    private static final Semaphore CObject = new Semaphore(0);

    private static void runProductLine(Semaphore semaphore, int sleepTime, String objName) {
        new Thread(() -> {
            while (true) {
                try { Thread.sleep(sleepTime); }
                catch (InterruptedException e) { e.printStackTrace(); }

                semaphore.release();
                System.out.printf("Part %s produced\n", objName);
            }
        }).start();
    }

    public static void main(String[] args) throws InterruptedException {
        runProductLine(AObject, 1000, "A");
        runProductLine(BObject, 2000, "B");
        runProductLine(CObject, 3000, "C");

        while (true) {
            AObject.acquire();
            BObject.acquire();

            try { Thread.sleep(1000); }
            catch (InterruptedException e) { e.printStackTrace(); }

            System.out.println("A and B were combined in one module!");

            CObject.acquire();

            try { Thread.sleep(1000); }
            catch (InterruptedException e) { e.printStackTrace(); }

            System.out.println("Product received from Module and C!");
        }
    }
}
