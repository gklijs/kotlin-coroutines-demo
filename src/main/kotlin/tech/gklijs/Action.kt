package tech.gklijs

import java.util.concurrent.Flow
import java.util.concurrent.Future

sealed interface Action<out S, out F, out P>
class Sup<S>(val value: S) : Action<S, Nothing, Nothing>
class Fut<F>(val value: F) : Action<Nothing, F, Nothing>
class Pub<P>(val value: P) : Action<Nothing, Nothing, P>

typealias IntAction = Action<(Int) -> Int, (Int) -> Future<Int>, (Int) -> Flow.Publisher<Int>>
