package tech.gklijs.consumer;

public class Util {
    private Util() {
        //prevent instantiation
    }

    static void delay(Long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new PollInterruptedException(e);
        }
    }
}
