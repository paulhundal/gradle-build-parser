package commands

import ast.AstGraph
import ast.violation.Violation
import ast.visitor.Visitor
import ast.visitor.VisitorFactory
import converter.AllowlistConverter
import location.GradleBuildParser.Companion.gradleAstParser
import location.UserHome
import org.slf4j.Logger
import picocli.CommandLine.Command
import picocli.CommandLine.HelpCommand
import picocli.CommandLine.Mixin
import picocli.CommandLine.Option
import utils.StringPathFileReader
import utils.exists
import java.util.TreeSet
import java.util.concurrent.Callable

@Command(
  name = "ast",
  version = ["1.0"],
  description = ["Build AST Graph"],
  subcommands = [
    HelpCommand::class
  ]
)
internal class BuildAstCommand(
  private val logger: Logger,
  private val astGraph: AstGraph,
  private val stringSetReader: StringPathFileReader,
  private val userHome: UserHome
) : Callable<Int> {

  private lateinit var visitors: List<Visitor>
  private val rulesBroken: TreeSet<Violation> = TreeSet()

  @Mixin
  val common = CommonOptions()

  @Option(
    names = ["--allow-closures"],
    required = false,
    defaultValue = "",
    converter = [AllowlistConverter::class],
    description = [
      "Set allowlist of closures that do not break violations"
    ]
  )
  lateinit var allowClosures: Set<String>

  // Options here for the following
  // --bad-deps <txt file>
  // --bad-closures <txt file>
  // --test-only-deps <json file>
  // --prod-only-deps <json file>

  override fun call(): Int {
    return runCatching {
      visitors = VisitorFactory(allowClosures).create()
      val buildFilesPath = userHome.gradleAstParser().resolve(common.repository)
        .resolve("build-files-list.txt")

      if (!buildFilesPath.exists()) {
        logger.error("You have not saved any build files to run ast parsing on. You can start " +
          "by running ./gradlew run --args=\"build-files --repository-path=<path>")
        return 1
      }

      logger.info(buildFilesPath.toString())

      val astGraph = astGraph.walk(stringSetReader.read(buildFilesPath))

      // astGraph.forEach { entry ->
      //   val buildFile = entry.key
      //   val nodes: List<ASTNode> = entry.value
      //   nodes.forEach { node ->
      //     visitors.forEach { visitor ->
      //       // Visit AST NODE
      //       node.visit(visitor)
      //       // Enforce Rules on that node
      //       visitor.rules.forEach { rule ->
      //         rulesBroken.addAll(rule.enforce(buildFile, visitor))
      //       }
      //       visitor.clear()
      //     }
      //   }
      // }
      rulesBroken.toSet()
    }.fold(
      onSuccess = {
      it.forEach {
        logger.info(it.toString())
      }
                  0
      },
      onFailure = { 1 }
    )
  }
}