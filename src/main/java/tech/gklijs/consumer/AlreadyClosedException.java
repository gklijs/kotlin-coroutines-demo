package tech.gklijs.consumer;

public class AlreadyClosedException extends RuntimeException {
    AlreadyClosedException() {
        super("Consumer was already closed");
    }
}
