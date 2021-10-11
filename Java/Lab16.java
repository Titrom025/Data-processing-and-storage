import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.atomic.AtomicInteger;

public class Lab16 {
    private static void waitUser() {
        System.out.println("\nEnter space to scroll down.");

        try {
            int key;
            do {
                key = System.in.read();
            } while (key != ' ' && key != -1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        final String requestURL = "https://table.nsu.ru/group/19213#weekday0";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestURL)).build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                .thenApply(HttpResponse::body)
                .thenAccept(stream -> {
                    AtomicInteger linesCount = new AtomicInteger();
                    stream.forEach(line -> {
                        System.out.println(line);
                        linesCount.getAndIncrement();

                        if (linesCount.get() % 25 == 0) { waitUser(); }
                    });
                })
                .join();
    }
}
