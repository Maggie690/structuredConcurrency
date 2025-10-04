package com.example.demo.mirror;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DownloadStructuredTask {
    private static final Logger logger = Logger.getLogger(DownloadStructuredTask.class.getName());

    static void download(Path target, List<URI> mirrors) throws IOException, InterruptedException {
        logger.info("Starting method 'download' to test 'StructuredTaskScope'");

        try (var scope = StructuredTaskScope.open(
                StructuredTaskScope.Joiner.allSuccessfulOrThrow()
        ); OutputStream out = Files.newOutputStream(target,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE)) {

            mirrors.forEach(uri -> scope.fork(() -> Main.fetchFromMirror(uri)));

            List<InputStream> texts = scope.join()
                    .map(t -> (InputStream) t.get())
                    .collect(Collectors.toUnmodifiableList());

            for (var text : texts) text.transferTo(out);
        }
    }
}

