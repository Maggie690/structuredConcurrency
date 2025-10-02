package com.example.demo;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;

/**
 * Joiner.anySuccessfulResultOrThrow() to stream the first successful InputStream, cancelling the rest.
 *
 */
public class Downloader {

    void main() throws IOException, InterruptedException {
        List<URI> mirrors = List.of(
                URI.create("https://mirror‑a.example.com"),
                URI.create("https://examplefile.com/file-download/19"),
                URI.create("https://mirror‑a.example.com"));


        Path path = download(Path.of("download.txt"), mirrors);

        System.out.println("Saved to " + path.toAbsolutePath());
    }

    /**
     *
     * The first link that is connected successfully will write data in 'download.txt' file
     * Every task is forked in a scope.
     * 'join()' waits for the tasks. Because of 'anySuccessfulResultOrThrow()',
     * it returns the first successful InputStream.
     * If none succeed, it throws an exception.
     *
     * @param target
     * @param uriList
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private static Path download(Path target, List<URI> uriList) throws IOException, InterruptedException {
        try (var scope = StructuredTaskScope.open(
                StructuredTaskScope.Joiner.<InputStream>anySuccessfulResultOrThrow()
        )) {
            uriList.forEach(uri -> scope.fork(() -> fetchFromWebSite(uri)));

            try (InputStream in = scope.join()) {
                OutputStream out = Files.newOutputStream(target,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.WRITE);

                in.transferTo(out);
            }
            return target;
        }
    }

    /**
     * Take binary data from given uri
     *
     * @param uri
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    private static InputStream fetchFromWebSite(URI uri) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(uri.toURL().openStream())) {
            return new ByteArrayInputStream(in.readAllBytes());
        }
    }
}
