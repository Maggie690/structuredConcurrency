package flux;

import java.util.concurrent.Executors;

public class VirtualThreadsExample {

    static final int REQUEST_COUNT = 100_000;

    static void main() {
        long start = System.currentTimeMillis();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < REQUEST_COUNT; i++) {
                final int requestId = i;
                executor.submit(() -> {
                    System.out.println("Handling request: " + requestId);
                });
            }
        }
        long stop = System.currentTimeMillis();
        System.out.println("---->" + (stop - start));
    }
}
