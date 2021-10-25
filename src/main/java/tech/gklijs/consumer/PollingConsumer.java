package tech.gklijs.consumer;

import java.util.List;

/*
Consumer is something that collects classes in some way, and can be pooled to get those classes.
 */
public interface PollingConsumer<T> {
    /*
    Will return directly if instances of T are available, might block for at most the given millis and might return an empty list.
     */
    List<T> poll(Integer millis);

    /*
    Close the consumer
     */
    void close();
}
