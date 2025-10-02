package com.example.demo;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.StructuredTaskScope;
import java.util.stream.Stream;

public class CustomJoiner {
    List<String> urls = List.of("https://gencraft.com/generate", "https://www.youtube.com/watch?v=onr80iOoEXs", "https://github.com");

    void main() {

        try (var scope = StructuredTaskScope.open(new MyCollectionJoiner<String>())) {
            urls.forEach(urls -> scope.fork(() -> fetchForm(urls)));
            List<String> fetchedContent = scope.join().toList();

            System.out.println("Total fetched content: " + fetchedContent.size());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String fetchForm(String url) {
        return "Fetch from from url: " + url;
    }

    class MyCollectionJoiner<T> implements StructuredTaskScope.Joiner<T, Stream<T>> {
        private final Queue<T> results = new ConcurrentLinkedQueue<>();

        @Override
        public boolean onComplete(StructuredTaskScope.Subtask<? extends T> subtask) {
            if (subtask.state() == StructuredTaskScope.Subtask.State.SUCCESS) {
                results.add(subtask.get());
            }
            return false;
        }

        @Override
        public Stream<T> result() throws Throwable {
            return results.stream();
        }
    }

}
