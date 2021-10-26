package tech.gklijs

enum class ActionType(val action: IntAction) {
    DELAYED(GetSupplier { d -> tech.gklijs.supplier.DelayedInt.get(d) }),
    FUTURE(GetFuture { d -> tech.gklijs.supplier.DelayedInt.getFuture(d) }),
    CALLABLE(GetFutureWithCallBack { d, c -> tech.gklijs.supplier.DelayedInt.getFuture(d, c) }),
    UNSAFE_CONSUMER(GetConsumer { d ->
        tech.gklijs.consumer.SingleThreadConsumer(d) { tech.gklijs.supplier.DelayedInt.get() }
    }),
    SAFE_CONSUMER(GetConsumer { d ->
        tech.gklijs.consumer.ConcurrentConsumer(d) { tech.gklijs.supplier.DelayedInt.get() }
    })
}