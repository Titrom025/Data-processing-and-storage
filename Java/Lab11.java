import java.util.concurrent.Semaphore;

public class Lab11 extends Thread {

    private final Semaphore semMain;
    private final Semaphore semChild;

    public Lab11(Semaphore semMain, Semaphore semChild) {
        this.semMain = semMain;
        this.semChild = semChild;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            try {
                semChild.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("Thread #2 - Line: %d\n", i);
            semMain.release();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Semaphore semMain = new Semaphore(1);
        Semaphore semChild = new Semaphore(0);

        Lab11 t = new Lab11(semMain, semChild);
        t.start();

        for (int i = 0; i < 10; i++) {
            try {
                semMain.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("Main thread - Line: %d\n", i);
            semChild.release();
        }
    }
}
