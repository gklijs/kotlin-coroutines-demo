package tech.gklijs

import tech.gklijs.consumer.SingleThreadConsumer
import java.util.concurrent.Future
import java.util.function.Consumer

sealed interface Action<out S, out F, out C, out STC>
class GetSupplier<S>(val value: S) : Action<S, Nothing, Nothing, Nothing>
class GetFuture<F>(val value: F) : Action<Nothing, F, Nothing, Nothing>
class GetFutureWithCallBack<C>(val value: C) : Action<Nothing, Nothing, C, Nothing>
class GetSingleThreadConsumer<STC>(val value: STC) : Action<Nothing, Nothing, Nothing, STC>

typealias IntAction = Action<
            (Int) -> Int,
            (Int) -> Future<Int>,
            (Int, Consumer<Int>) -> Future<Int>,
            (Int) -> SingleThreadConsumer<Int>
        >
