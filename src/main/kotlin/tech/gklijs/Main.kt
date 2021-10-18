import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import tech.gklijs.ActionType
import tech.gklijs.RunType
import tech.gklijs.util.Delay
import java.time.Duration
import java.time.Instant

fun main(args: Array<String>) {
    val parser = ArgParser("kotlin coroutines demo")
    val times by parser.option(ArgType.Int, shortName = "t", description = "times to run an action").default(10)
    val actionType by parser.option(ArgType.Choice<ActionType>(), shortName = "a", description = "action to execute").default(ActionType.DELAYED)
    val runType by parser.option(ArgType.Choice<RunType>(), shortName = "r", description = "how to run the actions").default(RunType.CD)

    parser.parse(args)

    val startTime = Instant.now()
    runType.run.invoke(times, actionType)
    val endTime = Instant.now()
    println("Running the actions took approximately: ${Duration.between(startTime, endTime)}")
    Delay.stop()
}