class Lab5 implements Runnable {

    @Override
    public void run() {
        int count = 0;
        try {
            while (true) {
                System.out.printf("Text %s from child thread\n", count);
                count++;
                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            System.out.println("Child thread was interrupted");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Lab5 runnable = new Lab5();
        Thread childThread = new Thread(runnable);
        childThread.start();
        Thread.sleep(2000);
        System.out.println("Main thread will interrupt child thread");
        childThread.interrupt();
    }
}
