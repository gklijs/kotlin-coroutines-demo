package tech.gklijs.supplier;

import tech.gklijs.util.Delay;

import java.util.concurrent.atomic.AtomicInteger;

public final class DelayedInt {
    private DelayedInt() {
        //prevent instantiation
    }
    private static final AtomicInteger atom = new AtomicInteger(0);
    public static Integer get() {
        return Delay.getDelayed(1, atom::getAndIncrement);
    }
}
