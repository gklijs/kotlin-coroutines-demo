package tech.gklijs.consumer;

public class PollInterruptedException extends RuntimeException {
    PollInterruptedException(Throwable cause) {
        super("Interrupted while polling", cause);
    }
}
