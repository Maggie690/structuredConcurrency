package com.example.demo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.StructuredTaskScope;
import java.util.logging.Logger;

public class Uploader {
    private static final Logger LOGGER = Logger.getLogger(Uploader.class.getName());

    enum Size {SMALL, MEDIUM, LARGE}

    void main() throws IOException, InterruptedException {
        Path tempDir = Files.createTempDirectory("imagesTest");
        for (int i = 0; i < 3; i++) {
            Files.createTempFile(tempDir, "img" + i, ".jpg");
        }

        processBatch(tempDir);
        deleteDirectory(tempDir);
    }

    static void processBatch(Path path) throws InterruptedException, IOException {
        try (var batch = StructuredTaskScope.open()) {
            try (var files = Files.list(path)) {
                files.filter(Files::isRegularFile)
                        .forEach(img -> batch.fork(() -> handleOne(img)));
            }

            batch.join();
        }
    }

    private static void handleOne(Path image) {
        try {
            try (var scope = StructuredTaskScope.open(
                    StructuredTaskScope.Joiner.allSuccessfulOrThrow()
            )) {
                scope.fork(() -> resizeAndUpload(image, Size.SMALL));
                scope.fork(() -> resizeAndUpload(image, Size.MEDIUM));
                scope.fork(() -> resizeAndUpload(image, Size.LARGE));

                scope.join();
            }
        } catch (InterruptedException e) {
            LOGGER.info("Image is not uploaded - " + image.getFileName());
        }
    }

    private static Void resizeAndUpload(Path image, Size size) throws InterruptedException {
        Thread.sleep(80);
        Thread.sleep(40);

        System.out.println("Upload " + image.getFileName() + "with size: " + size.name().toLowerCase());
        return null;
    }

    private static void deleteDirectory(Path tempDir) throws IOException {
        Files.walk(tempDir)
                .map(Path::toFile)
                .forEach(File::delete);

        tempDir.toFile().deleteOnExit();
    }
}
