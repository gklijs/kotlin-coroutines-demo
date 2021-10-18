package tech.gklijs

import kotlinx.coroutines.launch

enum class RunType(val run: (Int, ActionType) -> Unit) {
    //Serial Main
    SM({ t, a ->
        repeat(t) {
            a.action.invoke()
        }
    }),
    //Coroutine Unconfined
    CU({ t, a ->
        kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.Unconfined){
            repeat(t) {
                launch { a.action.invoke() }
            }
        }
    }),
    //Coroutine Default
    CD({ t, a ->
        kotlinx.coroutines.runBlocking(kotlinx.coroutines.Dispatchers.Default){
            repeat(t) {
                launch { a.action.invoke() }
            }
        }
    }),
    //Fixed Thread pool
    FT({ t, a ->
        val pool = java.util.concurrent.Executors.newFixedThreadPool(t)
        val futures = mutableListOf<java.util.concurrent.Future<*>>()
        repeat(t) {
            futures.add(pool.submit(a.action))
        }
        futures.forEach{x -> x.get()}
        pool.shutdown()
    })
}