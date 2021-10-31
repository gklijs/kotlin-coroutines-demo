package tech.gklijs

enum class ActionType(val description: String, val action: IntAction) {
    DELAYED(
        "a call that will block for the set time",
        GetSupplier { d -> tech.gklijs.supplier.DelayedInt.get(d) }),
    FUTURE(
        "creating a future which will be completed in the set time",
        GetFuture { d -> tech.gklijs.supplier.DelayedInt.getFuture(d) }),
    CALLABLE(
        "creating a callable that will be called in the st time",
        GetFutureWithCallBack { d, c -> tech.gklijs.supplier.DelayedInt.getFuture(d, c) }),
    UNSAFE_CONSUMER(
        "creating a consumer that should always be polled from the same thread, till it returned the set consume amount items",
        GetConsumer { d ->
            tech.gklijs.consumer.SingleThreadConsumer(d) { tech.gklijs.supplier.DelayedInt.get() }
        }),
    SAFE_CONSUMER(
        "creating a consumer that can be polled from multiple threads safely, till it returned the set consume amount items",
        GetConsumer { d ->
            tech.gklijs.consumer.ConcurrentConsumer(d) { tech.gklijs.supplier.DelayedInt.get() }
        })
}