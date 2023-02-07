import catalog.CatalogLocator
import commands.AstCommand
import di.PicoFactory
import location.GlobalScope
import org.slf4j.LoggerFactory
import picocli.CommandLine
import utils.ProcessExecute
import utils.RealDistribution

internal val LOGGER = LoggerFactory.getLogger("ast-parser")
private val root = GlobalScope.fromEnvironment()

fun main(args: Array<String>) {
  ProcessExecute(LOGGER).use {
    val distribution = RealDistribution.of(root.fileSystem)
    val locator = CatalogLocator(distribution)
    val picoCli = PicoFactory(root, distribution, locator)
    val cli = CommandLine(AstCommand(), picoCli)
    cli.execute(*args)
  }
}