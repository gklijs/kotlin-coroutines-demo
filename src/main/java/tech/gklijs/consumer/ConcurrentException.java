package tech.gklijs.consumer;

public class ConcurrentException extends RuntimeException {
    private static final String MESSAGE_FORMAT = "called consumer from %s but was called from %s before";

    ConcurrentException(String now, String before) {
        super(String.format(MESSAGE_FORMAT, now, before));
    }
}
