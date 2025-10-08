package flux;

import reactor.core.publisher.Flux;

public class FluxThreadsExample {
    static void main() {
        long start = System.currentTimeMillis();

        Flux.range(0, VirtualThreadsExample.REQUEST_COUNT)
                .flatMap(id -> handleRequest(id))
                .blockLast();
        long stop = System.currentTimeMillis();
        System.out.println(stop - start);
    }

    private static Flux<Integer> handleRequest(Integer id) {
        return Flux.defer(() -> {
            System.out.println("Handling request: " + id);
            return Flux.just(1);
        });
    }
}
