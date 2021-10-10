class Lab2 extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.printf("Thread #2 - Line: %d\n", i);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Lab2 t = new Lab2();
        t.start();
        t.join();

        for (int i = 0; i < 10; i++) {
            System.out.printf("Main thread - Line: %d\n", i);
        }
    }
}
