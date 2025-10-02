package com.example.demo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.ThreadFactory;

public class GatherThenFail {
    record Product(long id, String name) { }

    record Stock(long id, int quantity) { }

    record Price(long id, double amount) { }

    record ProductPayload(Product product, Stock stock, Price price) { }

    void main() throws InterruptedException {
        ProductPayload productPayload = fetchProduct(1L);
        System.out.println(productPayload);
    }

    /**
     * Each sub-service is invoked in parallel inside a StructuredTaskScope.
     * 'allSuccessfulOrThrow' that enforces an 'all-or-nothing' policy: any failure or exceeding
     * the one-second deadline cancels the whole group and surfaces an error to the caller.
     *
     * @param id
     * @return
     * @throws InterruptedException
     */
    static ProductPayload fetchProduct(long id) throws InterruptedException {
        ThreadFactory threadFactory = Thread.ofVirtual().name("name-%d", 1).factory();

        try (var scope = StructuredTaskScope.open(
                StructuredTaskScope.Joiner.allSuccessfulOrThrow(),
                cfg -> cfg.withTimeout(Duration.ofSeconds(1)).withThreadFactory(threadFactory)
        )) {

            StructuredTaskScope.Subtask<Product> product = scope.fork(() -> productApi(id));
            StructuredTaskScope.Subtask<Stock> stoke = scope.fork(() -> stockApi(id));
            StructuredTaskScope.Subtask<Price> price = scope.fork(() -> priceApi(id));

            scope.join();

            return new ProductPayload(product.get(), stoke.get(), price.get());
        }
    }

    private static Product productApi(long id) throws InterruptedException {
        Thread.sleep(100); //simulate latency
        return new Product(id, "Product - " + id);
    }

    private static Stock stockApi(long id) throws InterruptedException {
        Thread.sleep(130); //simulate latency
        return new Stock(id, new Random().nextInt(100));
    }

    private static Price priceApi(long id) throws InterruptedException {
        Thread.sleep(160); //simulate latency
        double price = BigDecimal.valueOf(new Random().nextDouble(100))
                .setScale(2, RoundingMode.FLOOR)
                .doubleValue();
        return new Price(id, price);
    }
}
