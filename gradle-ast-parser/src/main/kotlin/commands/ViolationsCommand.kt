package commands

/**
 * Copyright 2022 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import ast.AstGraph
import ast.violation.Violation
import ast.visitor.Visitor
import ast.visitor.VisitorFactory
import ast.visitor.VisitorManager
import catalog.Project
import location.Binary.Companion.binary
import location.GlobalScope
import location.GradleBuildParser.Companion.gradleAstParser
import org.codehaus.groovy.ast.ASTNode
import org.slf4j.Logger
import picocli.CommandLine.Command
import picocli.CommandLine.HelpCommand
import utils.ALLOWLIST_CLOSURES
import utils.BUILD_FILES
import utils.DISALLOWED_DEPENDENCIES
import utils.Files
import utils.StringPathFileReader
import utils.VIOLATIONS
import utils.exists
import java.io.File
import java.nio.file.Path
import java.util.TreeSet
import java.util.concurrent.Callable
import kotlin.io.path.readLines

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
  private val globalScope: GlobalScope,
  private val files: Files,
  private val visitorManager: VisitorManager
) : Callable<Int> {

  private lateinit var visitors: List<Visitor>
  private val violations: TreeSet<Violation> = TreeSet()

  override fun call(): Int {
    return runCatching {
      // Get current project and convert the Strings to the correct paths
      val currentProject = getProject()
      val allowlist = globalScope.binary().resolve(ALLOWLIST_CLOSURES).readLines().toSet()
      val disallowedDeps = globalScope.binary().resolve(DISALLOWED_DEPENDENCIES).readLines().toSet()

      // Setup visitors that will visit each node of the AST
      visitors = VisitorFactory(
        allowlist,
        disallowedDeps,
        logger,
        visitorManager
      ).create()

      // Get all build files for this project.
      val buildFilesPath = globalScope.userHome.gradleAstParser().resolve(currentProject.name)
        .resolve(BUILD_FILES)

      if (!buildFilesPath.exists()) {
        logger.error(
          "You have not saved any build files to run ast parsing on. You can start " +
            "by running ./gradlew setup -p=<project-name>. You can find the list of projects " +
            "we have cataloged in project-catalog.json. If you're project is not on the list " +
            "please add the name and project path there."
        )
        return 1
      }

      // Walk the AST graph and visit each build files List<ASTNode>
      // Gather violations as they occur.
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
      // Don't need to write out to file if no errors found
      if (violations.size == 0) {
        logger.info("No build files violated any rules!")
        return 0
      }

      val violationOutput = writeViolations(project = currentProject, violations.toSet())

      logger.info(
        "There were ${violations.size} violations found for project ${currentProject.name}. " +
          "To see a detailed list of all violations open $violationOutput"
      )
    }.fold(
      onSuccess = { 0 },
      onFailure = { 1 }
    )
  }

  private fun writeViolations(project: Project, violations: Set<Violation>): Path {
    val root = globalScope.userHome.gradleAstParser().resolve(project.name)
    val out = files.createOrOverwriteFile(root.resolve(VIOLATIONS).toString())!!
    val violationString = violations.joinToString(separator = "\n") { it.message }
    File(out.toString()).printWriter().use { out ->
      out.print(violationString)
    }
    return out
  }

  private fun getProject(): Project = globalScope.userHome.currentProject
}