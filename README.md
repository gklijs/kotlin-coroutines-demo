# Kotlin Coroutines Demo

## Intro

This is a cli project to get some insight on coroutines. It simulates using a Java API which might have blocking
methods, and what happens when calling those methods from Kotlin. When run using coroutines it might use extension
functions defined
in [Util.kt](https://github.com/gklijs/kotlin-coroutines-demo/blob/main/src/main/kotlin/tech/gklijs/Util.kt) to prevent
blocking the underlying threads. All the action types will provide integers in order, and depending on the run type they
may or may not be printed in order. By setting some parameters such as the delay to get an integer multiple ways of
running the same action can be compared.

## Use

You can run grade with `run --args='-h'` to get the info needed to use the cli thanks
to [kotlinx-cli](https://github.com/Kotlin/kotlinx-cli). For example when
using `gradle run --args='-a future -t 3 -r suspended -d 500'` it will create a future 3 times and wait till all three
of them are realized. Since this is done from a coroutine context, and it doesn't wait for each future to realize before
starting the next one, it will be done running them in only a little more than 500 milliseconds.

## Diagrams

Here are some diagrams for some actions to make clear what happens.

### Using a thread pool

This is one of the ways to run multiple things in parallel. A poll with multiple threads is created and runnables can be
executed on of the backing threads. The picture below is an approximation of
running `gradle run --args='-a delayed -t 2 -r thread_pool`.
![main thread using two childs threads to each do a delayed call](img/delayed.png "Delayed on thread pool")

### Future from coroutine on the main (current) thread

Using the extension function `eventuallyLog()`
in [Util.kt](https://github.com/gklijs/kotlin-coroutines-demo/blob/main/src/main/kotlin/tech/gklijs/Util.kt) the main
thread just needs to check occasionally if the future is completed already, so isn't blocked like it would with
calling `get()`. The picture below is an approximation of running `gradle run --args='-a future -t 2 -r suspended`.
![main thread created futures from coroutine and keeps checking if they are completed](img/future_suspended.png "Running futures suspended")

### Using the consumer in parallel

Some actions return a consumer. This is a class that was inspired by the Kafka Consumer, but has been drastically
simplified to use as example. A consumer is a class which on the background get 'things', in out case Integers, and from
another thread we can cal poll to get those things. In thus case we will use the action `suspended` that will only be
using the main thread, so it's fine to use the unsafe consumer. In this case `poll(0)` is used to poll, so we don't
block. The picture below is an approximation of running `gradle run --args='-a unsafe_consumer -t 2 -r suspended -ca 2`.
![main thread created consumers, keeps polling and delaying till all consumers delivered 2 things](img/unsafe_consumer_suspended.png "Running consumer suspended")
