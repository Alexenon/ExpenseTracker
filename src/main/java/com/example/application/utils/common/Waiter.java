package com.example.application.utils.common;

import java.time.Duration;

public class Waiter {

    public static void wait(Duration duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
