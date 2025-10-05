package com.example.demo.mirror;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;


public class DownloadExecutor {
    private static final Logger LOGGER = Logger.getLogger(DownloadExecutor.class.getName());

    /**
     * Downside - if we have a failre we're still waiting all the threads to complete
     *
     * @param target
     * @param mirrors`
     * @return
     * @throws IOException
     */
    static Path download(Path target, List<URI> mirrors) throws IOException {
        LOGGER.info("Starting method 'download' to test 'ExecutorService'");

        try (var executor = Executors.newVirtualThreadPerTaskExecutor();
             OutputStream out = Files.newOutputStream(target,
                     StandardOpenOption.CREATE,
                     StandardOpenOption.TRUNCATE_EXISTING,
                     StandardOpenOption.WRITE);) {

            CompletionService<InputStream> completionService = new ExecutorCompletionService<>(executor);

            mirrors.forEach(uri -> completionService.submit(() -> Main.fetchFromMirror(uri)));

            for (int i = 0; i < mirrors.size(); i++) {
                try {
                    Future<InputStream> f = completionService.take();
                    var in = f.get();
                    in.transferTo(out);

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    LOGGER.warning("Mirror failed: " + e.getCause());
                    throw new RuntimeException(e);
                }
            }
        }
        return target;
    }
}
