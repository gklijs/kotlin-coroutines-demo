package tech.gklijs

import kotlinx.coroutines.launch

enum class RunType(val run: RunFunction) {
    //Serial Main
    SM({ d, t, a ->
        repeat(t) {
            a.action.run(d)
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

    //Fixed Thread pool
    FT({ d, t, a ->
        val pool = java.util.concurrent.Executors.newFixedThreadPool(t)
        val futures = mutableListOf<java.util.concurrent.Future<*>>()
        repeat(t) {
            futures.add(pool.submit { a.action.run(d) })
        }
        futures.forEach { x -> x.get() }
        pool.shutdown()
    })
}

typealias RunFunction = (delay: Int, times: Int, action: ActionType) -> Unit