class Lab1 extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            System.out.printf("Thread #2 - Line: %d\n", i);
        }
    }

    public static void main(String[] args) {
        Lab1 t = new Lab1();
        t.start();

        for (int i = 0; i < 10; i++) {
            System.out.printf("Main thread - Line: %d\n", i);
        }
    }
}
