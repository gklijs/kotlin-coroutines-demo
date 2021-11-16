package tech.gklijs

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import java.util.concurrent.Future

enum class RunType(val description: String, val run: RunFunction) {
    SAME_THREAD(
        "serially running all actions in same thread",
        { d, t, a ->
            repeat(t) {
                a.action.run(d)
            }
        }
    ),

    SAME_THREAD_COMPLICATED(
        "serially running all actions in same thread, does complicated things to reduce time when possible",
        { d, t, a ->
            when (val action = a.action) {
                is GetSupplier -> {
                    repeat(t) {
                        // no way around the delays in this case
                        action.value.invoke(d).log()
                    }
                }
                is GetFuture -> {
                    val futures = buildList(t) {
                        repeat(t) {
                            add(action.value.invoke(d))
                        }
                    }
                    futures.forEach { x -> x.get().log() }
                }
                is GetFutureWithCallBack -> {
                    val futures = buildList(t) {
                        repeat(t) {
                            add(action.value.invoke(d) { y -> y.log() })
                        }
                    }
                    futures.forEach { x -> x.get() }
                }
                is GetConsumer -> {
                    val consumers = mutableListOf<RunningConsumer>()
                    repeat(t) {
                        val consumer = action.value.invoke(d)
                        consumers.add(RunningConsumer(consumer, Constants.consumeAmount))
                    }
                    while (consumers.isNotEmpty()) {
                        consumers.forEach { c -> c.next() }
                        consumers.removeIf { c -> c.reachedGoal() }
                    }
                }
            }
        }
    ),

    THREAD_POOL(
        "running in parallel, each action in it's own thread", { d, t, a ->
            val pool = java.util.concurrent.Executors.newFixedThreadPool(t)
            val futures = mutableListOf<Future<*>>()
            repeat(t) {
                futures.add(pool.submit { a.action.run(d) })
            }
            futures.forEach { x -> x.get() }
            pool.shutdown()
        }
    ),

    SUSPENDED(
        "run suspended in the parent context", { d, t, a ->
            kotlinx.coroutines.runBlocking() {
                repeat(t) {
                    launch { a.action.suspend(d) }
                }
            }
        }
    ),

    SUSPENDED_DEFAULT("run suspended in default dispatch context", { d, t, a ->
        kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.Default) {
            repeat(t) {
                launch { a.action.suspend(d) }
            }
        }
    }),

    SUSPENDED_UNCONFINED("run suspended in unconfined dispatch context", { d, t, a ->
        kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.Unconfined) {
            repeat(t) {
                launch { a.action.suspend(d) }
            }
        }
    }),

    SUSPENDED_BLOCKING("run suspended in parent thread, but using blocking function calls", { d, t, a ->
        kotlinx.coroutines.runBlocking() {
            repeat(t) {
                launch { a.action.run(d) }
            }
        }
    }),

    SUSPENDED_DEFAULT_BLOCKING("run suspended in default context, but using blocking function calls", { d, t, a ->
        kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.Default) {
            repeat(t) {
                launch { a.action.run(d) }
            }
        }
    }),

    @OptIn(ObsoleteCoroutinesApi::class)
    SUSPENDED_NEW("run suspended, each action in it's own single threaded context", { d, t, a ->
        kotlinx.coroutines.runBlocking() {
            repeat(t) {
                launch(kotlinx.coroutines.newSingleThreadContext("new-coroutine-$it")) { a.action.suspend(d) }
            }
        }
    }),
}

typealias RunFunction = (delay: Int, times: Int, action: ActionType) -> Unit
