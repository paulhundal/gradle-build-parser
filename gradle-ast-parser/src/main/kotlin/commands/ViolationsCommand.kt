package commands

import ast.AstGraph
import ast.violation.Violation
import ast.visitor.Visitor
import ast.visitor.VisitorFactory
import catalog.Project
import converter.AllowlistConverter
import location.GradleBuildParser.Companion.gradleAstParser
import location.UserHome
import org.codehaus.groovy.ast.ASTNode
import org.slf4j.Logger
import picocli.CommandLine.Command
import picocli.CommandLine.HelpCommand
import utils.Files
import utils.StringPathFileReader
import utils.exists
import java.io.File
import java.nio.file.Path
import java.util.TreeSet
import java.util.concurrent.Callable

@Command(
  name = "violations",
  version = ["1.0"],
  description = ["Find violations in the AST of the current project."],
  subcommands = [
    HelpCommand::class
  ]
)
internal class ViolationsCommand(
  private val logger: Logger,
  private val astGraph: AstGraph,
  private val stringSetReader: StringPathFileReader,
  private val userHome: UserHome,
  private val allowlistConverter: AllowlistConverter,
  private val files: Files
) : Callable<Int> {

  private lateinit var visitors: List<Visitor>
  private val violations: TreeSet<Violation> = TreeSet()

  override fun call(): Int {
    return runCatching {
      val currentProject = getProject()
      val allowlist = allowlistConverter.convert(currentProject.allowlistClosuresPathAsString)

      visitors = VisitorFactory(
        allowlist,
        logger
      ).create()

      val buildFilesPath = userHome.gradleAstParser().resolve(currentProject.name)
        .resolve("build-files-list.txt")

      if (!buildFilesPath.exists()) {
        logger.error("You have not saved any build files to run ast parsing on. You can start " +
          "by running ./gradlew setup -p=<project-name>. You can find the list of projects " +
          "we have cataloged in project-catalog.json.")
        return 1
      }

      val astGraph = astGraph.walk(stringSetReader.read(buildFilesPath))
      astGraph.entries.forEach { entry ->
        val buildFile = entry.key
        val nodes: List<ASTNode> = entry.value
        nodes.forEach { node ->
          visitors.forEach { visitor ->
            node.visit(visitor)
            visitor.rules.forEach { rule ->
              violations.addAll(rule.enforce(buildFile, visitor))
            }
          }
        }
      }
      val violationOutput = writeViolations(project = currentProject, violations.toSet())

      logger.info("There were ${violations.size} violations found for project ${currentProject.name}. " +
        "To see a detailed list of all violations open $violationOutput")
    }.fold(
      onSuccess = { 0 },
      onFailure = { 1 }
    )
  }

  private fun writeViolations(project: Project, violations: Set<Violation>): Path {
    val root = userHome.gradleAstParser().resolve(project.name)
    val out = files.createOrOverwriteFile(root.resolve("violations.txt").toString())!!
    val violationString = violations.map { it.message }.joinToString(separator = "\n")
    File(out.toString()).printWriter().use { out ->
      out.print(violationString)
    }
    return out
  }

  private fun getProject(): Project = userHome.currentProject

}