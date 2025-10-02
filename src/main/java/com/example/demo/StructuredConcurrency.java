package com.example.demo;

import java.util.Random;
import java.util.concurrent.*;

public class StructuredConcurrency {
    private static final Random rnd = new Random();

     static void main(String[] args) {
        for (int i = 1; i <= 5; i++) {
            try {
                String result = getUserInfo(String.valueOf(rnd.nextInt(200)));
                System.out.println(result);
            } catch (Exception e) {
                System.err.println("Attempt " + i + " failed.");
            }
        }
    }

    public static String getUserInfo(String userId) throws InterruptedException {
        try (var scope = StructuredTaskScope.open()) {
            StructuredTaskScope.Subtask<String> taskFindUser = scope.fork(() -> fetchUserData(userId));
            StructuredTaskScope.Subtask<String> taskPreferencesByUserId = scope.fork(() -> fetchUserPreferences(userId));

            scope.join();

            String userData = taskFindUser.get();
            String preferences = taskPreferencesByUserId.get();

            return userData + " with " + preferences;
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
