package org.apache.dubbo.gateway.admin.utils;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

/**
 * 防止重复消费的消费者包装类
 *
 * @param <T1> 入参1
 * @param <T2> 入参2
 */
public class IdempotentBiConsumer<T1, T2> implements BiConsumer<T1, T2> {

    private final AtomicBoolean hasConsume = new AtomicBoolean(false);

    private final BiConsumer<T1, T2> delegate;

    public IdempotentBiConsumer(BiConsumer<T1, T2> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void accept(T1 t1, T2 t2) {
        if (hasConsume.compareAndSet(false, true)) {
            delegate.accept(t1, t2);
        }
    }
}