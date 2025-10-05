package com.example.demo.mirror;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    static void main() throws URISyntaxException, IOException, InterruptedException {
        List<URI> mirrors = List.of(
                new URI("https://www.learningcontainer.com/wp-content/uploads/2020/04/sample-text-file.txt"),
                new URI("https://txt2html.sourceforge.net/sample.txt")
        );

        DownloadExecutor.download(Path.of(DownloadExecutor.class.getSimpleName() + "_" + LocalDateTime.now()), mirrors);
        DownloadStructuredTask.download(Path.of(DownloadStructuredTask.class.getSimpleName() + "_" + LocalDateTime.now()), mirrors);
    }

    static InputStream fetchFromMirror(URI uri) throws IOException {
        LOGGER.info("Trying: " + uri);

        URLConnection conn = uri.toURL().openConnection();
        return conn.getInputStream();
    }
}
