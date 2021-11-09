# Kotlin Coroutines Demo

## Contents

* [Introduction](#intro)
* [What this is not](#wtin)
* [Using this project](#utp)
* [Action types](#action-types)
  * [Delayed](#delayed)
  * [Future](#future)
  * [Callable](#callable)
  * [Unsafe consumer](#unsafe_consumer)
  * [Safe consumer](#safe_consumer)
* [Diagrams](#diagrams)
  * [Using a thread pool](#tp)
  * [Use a separate context for delayed](#delayed-coroutine)
  * [Future from coroutine on the main thread](#future-coroutine)
  * [Using the consumer in parallel](#consumer-parallel)

## <a id="intro">Introduction</a>

This is a cli project to get some insight on coroutines. It simulates using a Java API which might have blocking
methods, and what happens when calling those methods from Kotlin. When run using coroutines it might use extension
functions defined
in [Util.kt](https://github.com/gklijs/kotlin-coroutines-demo/blob/main/src/main/kotlin/tech/gklijs/Util.kt) to prevent
blocking the underlying threads. All the action types will provide integers in order, and depending on the run type they
may or may not be printed in order. By setting some parameters such as the delay to get an integer multiple ways of
running the same action can be compared.

## <a id="wtin">What this is not</a>

Since the main focus of this project is to keep things relatively simple, it's lacking some things you would want for
running something in production:

- Using a logging framework, just using println is sufficient for this project.
- Proper error handling, for example when using a callback, it's often possible an error shows up. At which point a
  decision need to be made to either resume with the exception, or do something else like providing a default.
- Being a good example of how coroutines make things simpler. Coroutines help reduce complexity when you have multiple
  async calls that need to be handled in succession. This project just deals with simply getting an Integer and logging
  it and thus doesn't touch that aspect of coroutines. Another nice property of coroutines is that they are easily
  canceled, which is also not handled in this project. If you are interested in this, please take a look
  at [this code](https://github.com/kotlin-hands-on/intro-coroutines.git) belonging
  to [this tutorial](https://play.kotlinlang.org/hands-on/Introduction%20to%20Coroutines%20and%20Channels/01_Introduction)
- Handling streaming/reactive API's. Although certainly interesting case to have a Java Publisher, and how to handle
  that, probably converting it into a flow, it's currently out of scope. Feel free to create a merge request or issue to
  include this, if it has your interest.

## <a id="utp">Using this project</a>

You can run grade with `run --args='-h'` to get the info needed to use the cli thanks
to [kotlinx-cli](https://github.com/Kotlin/kotlinx-cli). Depending on the parameters it wil run actions in a certain
way, and log the thread from which it's logged together with the time, so we get some insight into what is happening. It
will also give an approximation of the time needed to run everything.

For example when using `gradle run --args='-a future -t 3 -r suspended -d 500'` it will create a future 3 times and wait
till all three of them are realized. Since this is done from a coroutine context, and it doesn't wait for each future to
realize before starting the next one, it will be done running them in only a little more than 500 milliseconds. With
coroutines the actions will be run using launch, so it won't wait for a task to complete before starting the next task.

Here is the output from `run --args='-h' to get a quick idea:

```
Usage: kotlin coroutines demo options_list
Options: 
    --delay, -d [1000] -> delay for each action in milliseconds { Int }
    --times, -t [10] -> times to run an action { Int }
    --actionType, -a [DELAYED] -> action to execute { Value should be one of [delayed, future, callable, unsafe_consumer, safe_consumer] }
    --runType, -r [SAME_THREAD] -> how to run the actions { Value should be one of [same_thread, same_thread_complicated, thread_pool, suspended, suspended_default, suspended_unconfined, suspended_blocking, suspended_default_blocking, suspended_new] }
    --consumeDelay, -cd [100] -> max delay between poll calls in milliseconds { Int }
    --consumeAmount, -ca [10] -> amount of items to consume before closing the consumer { Int }
    --futureDelay, -fd [10] -> amount of time between checking if the future has resolved { Int }
    --helperThreads, -ht [2] -> amount of threads the helper has, this will be used to suspend the delayed function in another context { Int }
    --help, -h -> Usage info 
```

## <a id="action-types">Action types</a>

I'll go through all the action types, with some examples how they will be run with certain run types.

### <a id="delayed">Delayed</a>

Delayed is just a single call, that depending on the set delay might take a while to return. When run normally on the
same thread it thus needs to wait for each call to complete.
So `gradle run --args='-a delayed -t 20 -r same_thread -d 500'` takes a little over 10 seconds, 20 times half a second.
When run on a thread pool it's finished in just over half a
second, `gradle run --args='-a delayed -t 20 -r same_thread -d 500'`. The main problem with Thread Pools is that they
need many resources and have limited scalability, for me when I
run `gradle run --args='-a delayed -t 5000 -r thread_pool -d 500'` I get an
error: `[1.314s][warning][os,thread] Failed to start thread - pthread_create failed (EAGAIN) for attributes: stacksize: 1024k, guardsize: 4k, detached.`
because it can't create 5000 threads.

When run suspended it will use a separate coroutine context, that by default has two backing threads. Since it's a
blocking call, the context will still be saturated fast. Since the 'work' is seperated by two
threads, `gradle run --args='-a delayed -t 20 -r suspended -d 500'` will run twice as fast as `same_thread`, so about 5
seconds. Since the main thread is just logging the result, if we run it with 20 helper
threads, `gradle run --args='-a delayed -t 20 -r suspended -d 500 -ht 20'` it will finnish a little over half a second.
When run with `suspended_default` it will use the default context for the logging, which has as many treads as vCPU's
available. What is interesting is that with `gradle run --args='-a delayed -t 20 -r suspended_default -d 500'` only two
of the backing threads are used for the logging. It will still take about 5 seconds, since it's limited by the helper
context in the same way as `suspended`.

### <a id="future">Future</a>

This will return a future, which we can store and call the `get()` on later. This is tricky to get right, and is done
only when running as `same_thread_complicated`. Doing this we can
complete `gradle run --args='-a future -t 20 -r same_thread_complicated -d 500'` in a little over half a seconds. When
run in a blocking way with other runtimes we just directly call the `get()` effectively making it the same as `delayed`.
So `gradle run --args='-a future -t 20 -r same_thread -d 500'` will still take little over 10 seconds.

Luckily with coroutines we can check if the future is either canceled or completed, and otherwise delay for some amount
of time. Delay might look a lot like `Thread.sleep()`, but the big advantage is that delay won't block the thread. Using
this trick it's possible for `gradle run --args='-a future -t 20 -r suspended -d 500'` to finish in about half a second.
Optionally with -fd the time for the delay can be set, for
example `gradle run --args='-a future -t 20 -r suspended_unconfined -d 500 -fd 1000'`
will complete a little over a second, because it will delay a full second, before checking the future again.

### <a id="callable">Callable</a>

This provides an api, where we can pass a function which will be called once the future is completed. This won't help
much when run in the usual way, since we still need to `get()` the resulting future, otherwise the program will end
before the Integers are logged. When run with `same_thread_complicated` we apply a similar trick as with the futures,
first creating the futures and then calling `get()` on
them. `gradle run --args='-a callable -t 20 -r same_thread_complicated -d 500'` thus also completes in half a second,
this time it's logged from the Timer thread used in the Java part however, as can be seen from logs
like: `2021-11-08T20:34:08.647508Z - received number: 00019 - logged from Timer-0`.

With coroutines, we can use the callback to create a `suspendCoroutine` that resumes when the callback is called. So we
no longer need to check each time and delay, but we just 'wait' for the result till we can continue. This code is much
more readable compared to the construct needed for `same_thread_complicated`. Using the suspended
coroutines `gradle run --args='-a callable -t 20 -r suspended -d 500'` completes in about half a second.

### <a id="Unsafe Consumer">unsafe_consumer</a>

TODO

### <a id="Safe Consumer">safe_consumer</a>

TODO

## <a id="diagrams">Diagrams</a>

Here are some diagrams for some action type with run type combinations to make clear what happens.

### <a id="tp">Using a thread pool</a>

This is one of the ways to run multiple things in parallel. A poll with multiple threads is created and functions can be
executed on of the backing threads. The picture below is an approximation of
running `gradle run --args='-a delayed -t 2 -r thread_pool'`.
![main thread using two child threads to each do a delayed call](img/delayed.png "Delayed on thread pool")

### <a id="delayed-coroutine">Use a separate context for delayed</a>

With coroutines, it's possible to switch the coroutine context, thus preventing saturating or blocking one. When the
action `delayed` is run in a coroutine a separate context, `supplierContext` is used of which the backing threads can be
configured with the `-ht` option, by default it's two. The picture below is an approximation of
running `gradle run --args='-a delayed -t 2 -r suspended -ht 2'`
![suspend using the newly created context, with the underlying threads](img/delayed_suspended.png "Delayed with separate coroutine context")

### <a id="future-coroutine">Future from coroutine on the main thread</a>

Using the extension function `eventuallyLog()`
in [Util.kt](https://github.com/gklijs/kotlin-coroutines-demo/blob/main/src/main/kotlin/tech/gklijs/Util.kt) the main
thread just needs to check occasionally if the future is completed already, so isn't blocked like it would with
calling `get()`. The picture below is an approximation of running `gradle run --args='-a future -t 2 -r suspended'`.
![main thread created futures from coroutine and keeps checking if they are completed](img/future_suspended.png "Running futures suspended")

### <a id="consumer-parallel">Using the consumer in parallel</a>

Some actions return a consumer. This is a class that was inspired by the Kafka Consumer, but has been drastically
simplified to use as example. A consumer is a class which on the background get 'things', in out case Integers, and from
another thread we can cal poll to get those things. In thus case we will use the action `suspended` that will only be
using the main thread, so it's fine to use the unsafe consumer. In this case `poll(0)` is used to poll, so we don't
block. The picture below is an approximation of running `gradle run --args='-a unsafe_consumer -t 2 -r suspended -ca 2`.
![main thread created consumers, keeps polling and delaying till all consumers delivered 2 things](img/unsafe_consumer_suspended.png "Running consumer suspended")
