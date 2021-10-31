package tech.gklijs.consumer;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.LongStream;

import static tech.gklijs.consumer.Util.delay;

public class SingleThreadConsumer<T> implements PollingConsumer<T> {

    private final Instant startedAt;
    private final int millisPerItem;
    private final Supplier<T> supplier;
    private String threadName;
    private long itemsRetrieved;
    private boolean closed = false;

    public SingleThreadConsumer(int millisPerItem, Supplier<T> supplier) {
        this.millisPerItem = millisPerItem;
        this.supplier = supplier;
        startedAt = Instant.now();
    }


    @Override
    public List<T> poll(Integer millis) {
        if (closed) {
            throw new AlreadyClosedException();
        }
        String currentThreadName = Thread.currentThread().getName();
        if (threadName == null) {
            threadName = currentThreadName;
        } else if (!threadName.equals(currentThreadName)) {
            throw new ConcurrentException(currentThreadName, threadName);
        }
        long itemsAvailable = itemsAvailable();
        if (itemsAvailable > 0) {
            return getAvailableItems(itemsAvailable);
        }
        long millisTillNext = millisTillNext();
        if (millisTillNext > millis) {
            delay(millis.longValue());
            return Collections.emptyList();
        }
        if (millisTillNext > 0) {
            delay(millisTillNext);
        }
        return getAvailableItems(1);
    }

    @Override
    public void close() {
        closed = true;
    }

    private List<T> getAvailableItems(long itemsAvailable) {
        List<T> list = new ArrayList<>();
        LongStream.range(0, itemsAvailable).forEach(i -> list.add(supplier.get()));
        itemsRetrieved += itemsAvailable;
        return list;
    }

    private long itemsAvailable() {
        long totalItems = Duration.between(startedAt, Instant.now()).toMillis() / millisPerItem;
        if (totalItems <= itemsRetrieved) {
            return 0;
        }
        return totalItems - itemsRetrieved;
    }

    private long millisTillNext() {
        long millisToNext = (itemsRetrieved + 1) * millisPerItem;
        return Duration.between(Instant.now(), startedAt.plus(millisToNext, ChronoUnit.MILLIS)).toMillis();
    }
}
