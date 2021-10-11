import java.io.IOException;
import java.util.Scanner;

public class Lab12 implements Runnable {

    private static final int MAX_STRING_LENGTH = 80;
    private static boolean isRunning = true;
    private static final SortList sList = new SortList();

    public static class SortList {
        class Node {
            String data;
            Node next;

            public Node(String data)
            {
                this.data = data;
                this.next = null;
            }
        }

        public Node head = null;

        public void addNode(String data)
        {
            Node newNode = new Node(data);
            if (head != null) {
                newNode.next = head;
            }

            head = newNode;
        }

        public void sortList()
        {
            if (head != null) {
                Node current = head;
                Node nextNode;

                String temp;

                while (current != null) {
                    nextNode = current.next;
                    while (nextNode != null) {
                        if (CharSequence.compare(current.data, nextNode.data) > 0) {
                            temp = current.data;
                            current.data = nextNode.data;
                            nextNode.data = temp;
                        }
                        nextNode = nextNode.next;
                    }
                    current = current.next;
                }
            }
        }

        public void showList()
        {
            Node current = head;

            if (head == null) {
                System.out.println("List is empty");
                return;
            }
            while (current != null) {
                System.out.print(current.data + "\n");
                current = current.next;
            }

            System.out.println();
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            try { Thread.sleep(5000); }
            catch (InterruptedException e) { e.printStackTrace(); }

            synchronized (sList) {
                sList.sortList();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);

        new Thread(new Lab12()).start();

        while (true) {
            String string = in.nextLine();

            if (string.length() == 0) { break; }

            while (string.length() > MAX_STRING_LENGTH) {
                synchronized (sList) {
                    sList.addNode(string.substring(0, MAX_STRING_LENGTH));
                }
                string = string.substring(MAX_STRING_LENGTH);
            }

            synchronized (sList) {
                sList.addNode(string);
            }
        }

        isRunning = false;

        synchronized (sList) {
            sList.showList();
        }
    }
}
