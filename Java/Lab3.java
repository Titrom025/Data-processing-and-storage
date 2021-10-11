class Lab3 implements Runnable {
    private final int threadId;
    private final String[] strings;

    public Lab3(int threadId, String[] strings) {
        this.threadId = threadId;
        this.strings = strings.clone();
    }

    public void printInfo(String[] strings) {
        for (String str : strings) {
            System.out.printf("Thread %d - %s\n", threadId, str);
        }
    }

    @Override
    public void run() {
        printInfo(strings);
    }

    public static void createNewThread(int index, String[] strings) {
        Lab3 runnable = new Lab3(index, strings);
        new Thread(runnable).start();
    }

    public static void main(String[] args) {
        String[] strings = {"String 1", "String 1", "String 1", "String 1"};
        createNewThread(1, strings);
        strings[1] = "Hello";
        createNewThread(2, strings);
        strings[2] = "Hello";
        createNewThread(3, strings);
        strings[3] = "Hello";
        createNewThread(4, strings);
    }
}
