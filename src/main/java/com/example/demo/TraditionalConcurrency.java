package com.example.demo;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TraditionalConcurrency {

    private static final Random rnd = new Random();

    public static void main(String[] args) {
        for (int i = 1; i <= 5; i++) {
            try {
                String result = getUserInfo(String.valueOf(rnd.nextInt(200)));
                System.out.println(result);
            } catch (Exception e) {
                System.err.println("Attempt " + i + " failed.");
            }
        }
    }

    public static String getUserInfo(String userId) throws ExecutionException, InterruptedException {
        try (ExecutorService executor = Executors.newCachedThreadPool()) {
            Future<String> future = executor.submit(() -> fetchUserData(userId));
            Future<String> future0 = executor.submit(() -> fetchUserPreferences(userId));

            try {
                String userData = future.get();
                String preferences = future0.get();

                return userData + " with " + preferences;
            } catch (Exception e) {
                System.out.println("Error occurred, attempting cleanup...");
                throw e;
            } finally {
                future.cancel(true);
                future0.cancel(true);
            }
        }
    }


    private static String fetchUserData(String userId) throws InterruptedException {
        Thread.sleep(1000 + rnd.nextInt(2000));
        if (rnd.nextBoolean()) {
            throw new RuntimeException("User service invalid");
        }
        return "User id: " + userId;
    }

    private static String fetchUserPreferences(String userId) throws InterruptedException {
        Thread.sleep(800 + rnd.nextInt(1500));
        if (rnd.nextBoolean()) {
            throw new RuntimeException("Preferences service down");
        }
        return "Preference id: " + userId;
    }
}
