package com.example.demo;

import java.time.Duration;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.StructuredTaskScope;

public class QuoteService {

    static void main() throws InterruptedException {
        double q = quote("ACME");
        System.out.printf("Quote for ACME: %.2f%n", q);
    }

    static double quote(String symbol) throws InterruptedException {

        var firstSuccess = new StructuredTaskScope.Joiner<Double, Optional<Double>>() {
            private volatile Double value;

            @Override
            public boolean onComplete(StructuredTaskScope.Subtask<? extends Double> subtask) {
                if (subtask.state() == StructuredTaskScope.Subtask.State.SUCCESS) {
                    value = subtask.get();
                }
                return value != null;
            }

            @Override
            public Optional<Double> result() throws Throwable {
                return Optional.ofNullable(value);
            }
        };

        try (var scope = StructuredTaskScope.open(firstSuccess,
                cfg -> cfg.withTimeout(Duration.ofMillis(30)))) {
            scope.fork(() -> markFeed(symbol));

            Optional<Double> latest = scope.join();

            return latest.orElseGet(() -> cache(symbol));
        }
    }

    private static double markFeed(String symbol) throws InterruptedException {
        long delay = new Random().nextBoolean() ? 20 : 60;
        Thread.sleep(delay);

        return 100 + new Random().nextDouble();
    }

    private static double cache(String symbol) {
        return 95.00;
    }

}
