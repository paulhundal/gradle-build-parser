import commands.AstCommand
import di.PicoFactory
import location.GlobalScope
import org.slf4j.LoggerFactory
import picocli.CommandLine
import utils.ProcessExecute

internal val LOGGER = LoggerFactory.getLogger("ast-parser")
private val root = GlobalScope.fromEnvironment()

fun main(args: Array<String>) {
  ProcessExecute(LOGGER).use {
    val picoCli = PicoFactory(root)
    val cli = CommandLine(AstCommand(), picoCli)
    cli.execute(*args)
  }
}