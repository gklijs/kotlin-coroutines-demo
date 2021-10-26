package tech.gklijs

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch

enum class RunType(val run: RunFunction) {
    //Serial Main
    SM({ d, t, a ->
        repeat(t) {
            a.action.run(d)
        }
    }),

    //Fixed Thread pool
    FT({ d, t, a ->
        val pool = java.util.concurrent.Executors.newFixedThreadPool(t)
        val futures = mutableListOf<java.util.concurrent.Future<*>>()
        repeat(t) {
            futures.add(pool.submit { a.action.run(d) })
        }
        futures.forEach { x -> x.get() }
        pool.shutdown()
    }),

    //Coroutine parent context
    CP({ d, t, a ->
        kotlinx.coroutines.runBlocking() {
            repeat(t) {
                launch { a.action.suspend(d) }
            }
        }
    }),

    //Coroutine Unconfined
    CU({ d, t, a ->
        kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.Unconfined) {
            repeat(t) {
                launch { a.action.suspend(d) }
            }
        }
    }),

    //Coroutine Default
    CD({ d, t, a ->
        kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.Default) {
            repeat(t) {
                launch { a.action.suspend(d) }
            }
        }
    }),

    //Coroutine, launch in new thread
    @OptIn(ObsoleteCoroutinesApi::class)
    CN({ d, t, a ->
        kotlinx.coroutines.runBlocking() {
            repeat(t) {
                launch(kotlinx.coroutines.newSingleThreadContext("new-coroutine-$it")) { a.action.suspend(d) }
            }
        }
    }),
}

typealias RunFunction = (delay: Int, times: Int, action: ActionType) -> Unit