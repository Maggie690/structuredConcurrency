package com.example.demo;

import jakarta.activation.FileDataSource;
import jakarta.mail.*;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EmailSender {

    static void main() throws IOException, InterruptedException {
        Path images = Path.of("your path");
        var files = Stream.of(new File(images.toString()).listFiles())
                .filter(file -> !file.isDirectory())
                .limit(3)
                .map(File::getAbsolutePath)
                .collect(Collectors.toList());

        processBatch(files, null);

    }

    static void processBatch(List<String> path, List<String> emails) throws InterruptedException, IOException {
        try (var batch = StructuredTaskScope.open()) {
            path.stream()
                    .limit(3)
                    .forEach(img -> batch.fork(() -> sendEmails(img, emails)));

            batch.join();
        }
    }

    /**
     *
     *   openssl s_client -connect smtp.gmail.com:25 -starttls smtp
     *   openssl s_client -connect imap.gmail.com:993 -starttls smtp
     *
     * @param image
     * @param emails
     */
    static void sendEmails(String image, List<String> emails) {
        Email email = EmailBuilder.startingBlank()
                .from("from")
                .to("to")
                .withSubject("send from code")
                .withPlainText("Test email. You should receive 1 attached image to this email. :)")
                .withAttachment(image, new FileDataSource(image))
                .buildEmail();

        Mailer mailer = MailerBuilder
                .withSMTPServer("smtp.gmail.com", 25, "from", "password")
                .buildMailer();

        mailer.sendMail(email);
    }
}
