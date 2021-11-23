import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import tech.gklijs.ActionType
import tech.gklijs.Constants
import tech.gklijs.RunType
import java.time.Duration
import java.time.Instant
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val parser = ArgParser("kotlin coroutines demo")
    val delay by parser.option(ArgType.Int, shortName = "d", description = "delay for each action in milliseconds")
        .default(1000)
    val times by parser.option(ArgType.Int, shortName = "t", description = "times to run an action").default(10)
    val actionType by parser.option(ArgType.Choice<ActionType>(), shortName = "a", description = "action to execute")
        .default(ActionType.DELAYED)
    val runType by parser.option(ArgType.Choice<RunType>(), shortName = "r", description = "how to run the actions")
        .default(RunType.SAME_THREAD)
    val consumeDelay by parser.option(
        ArgType.Int,
        shortName = "cd",
        description = "max delay between poll calls in milliseconds"
    ).default(
        Constants.consumeDelay
    )
    val consumeAmount by parser.option(
        ArgType.Int,
        shortName = "ca",
        description = "amount of items to consume before closing the consumer"
    ).default(
        Constants.consumeAmount
    )
    val futureDelay by parser.option(
        ArgType.Int,
        shortName = "fd",
        description = "amount of time between checking if the future has resolved"
    ).default(
        Constants.futureDelay
    )
    val helperThreads by parser.option(
        ArgType.Int,
        shortName = "ht",
        description = "amount of threads the helper has, this will be used to suspend the delayed function in another context"
    ).default(
        Constants.helperThreads
    )
    val parallelism by parser.option(
        ArgType.Int,
        shortName = "p",
        description = "limited parallelism used with SUSPENDED_DEFAULT_LIMITED_PARALLELISM"
    ).default(
        Constants.parallelism
    )

    try {
        parser.parse(args)
        Constants.setAndPrintConsumeDelay(consumeDelay)
        Constants.setAndPrintConsumeAmount(consumeAmount)
        Constants.setAndPrintFutureDelay(futureDelay)
        Constants.setAndPrintHelperThreads(helperThreads)
        Constants.setAndPrintParallelism(parallelism)
        println("Will start ${actionType.description} with a delay of $delay milliseconds for a total of $times times, ${runType.description}.")

        val startTime = Instant.now()
        runType.run.invoke(delay, times, actionType)
        val endTime = Instant.now()

        println("Running the actions took approximately: ${Duration.between(startTime, endTime)}")
    } catch (e: Exception) {
        println("Encountered exception trying to run the tasks: $e")
        exitProcess(1)
    }
    exitProcess(0)
}
