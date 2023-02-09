package configuration

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
import ast.visitor.VisitorManager
import catalog.Project
import location.Binary.Companion.binary
import location.GlobalScope
import location.GradleBuildParser.Companion.gradleAstParser
import org.codehaus.groovy.ast.ASTNode
import org.slf4j.Logger
import utils.BUILD_FILES
import utils.Configuration
import utils.Files
import utils.IGNORE_BUILDS
import utils.Status
import utils.Status.ERROR
import utils.StringPathFileReader
import utils.VIOLATIONS
import utils.exists
import java.io.File
import java.nio.file.Path
import java.util.TreeSet
import kotlin.io.path.readLines

internal class ViolationsConfiguration(
  private val astGraph: AstGraph,
  private val logger: Logger,
  private val stringSetReader: StringPathFileReader,
  private val visitorManager: VisitorManager,
  private val files: Files
) : Configuration<List<Visitor>, GlobalScope> {

  override fun applyFor(chosen: List<Visitor>, to: GlobalScope): Status {
    return runCatching {
      val currentProject = getProject(globalScope = to)
      val violations: TreeSet<Violation> = TreeSet()
      val ignoreList = to.binary().resolve(IGNORE_BUILDS).readLines().toSet()

      // Get all build files for this project.
      val buildFilesPath = to.userHome.gradleAstParser().resolve(currentProject.name)
        .resolve(BUILD_FILES)

      if (!buildFilesPath.exists()) {
        logger.error(
          "You have not saved any build files to run ast parsing on. You can start " +
            "by running ./gradlew setup -p=<project-name>. You can find the list of projects " +
            "we have cataloged in project-catalog.json. If you're project is not on the list " +
            "please add the name and project path there."
        )
        return@runCatching ERROR
      }

      val buildFilesToRead = stringSetReader.read(buildFilesPath)
        .filterNot { path -> ignoreList.any { path == to.userHome.resolve(it) } }
        .filter { it.exists() }.toSet()

      // Walk the AST graph and visit each build files List<ASTNode>
      // Gather violations as they occur.
      val astGraph = astGraph.walk(buildFilesToRead)
      astGraph.entries.forEach { entry ->
        val buildFile = entry.key
        val nodes: List<ASTNode> = entry.value
        chosen.forEach { visitor ->
          nodes.forEach { node ->
            node.visit(visitor)
          }
          visitor.clear()
        }

        chosen.forEach { visitor ->
          visitor.rules.forEach { rule ->
            violations.addAll(rule.enforce(buildFile, visitor))
          }
        }

        visitorManager.clear()
      }
      // Don't need to write out to file if no errors found
      if (violations.size == 0) {
        logger.info("No build files violated any rules!")
        return@runCatching Status.VALID
      }

      val violationOutput = writeViolations(to, currentProject, violations.toSet())

      logger.info(
        "There were ${violations.size} violations found for project ${currentProject.name}. " +
          "To see a detailed list of all violations open $violationOutput"
      )
    }.fold(
      onSuccess = {
        Status.VALID
      },
      onFailure = {
        logger.error("Failed to parse violations", it)
        ERROR
      }
    )
  }

  private fun writeViolations(
    globalScope: GlobalScope,
    project: Project,
    violations: Set<Violation>
  ): Path {
    val root = globalScope.userHome.gradleAstParser().resolve(project.name)
    val out = files.createOrOverwriteFile(root.resolve(VIOLATIONS).toString())!!
    val violationString = violations.joinToString(separator = "\n") { it.message }
    File(out.toString()).printWriter().use { o -> o.print(violationString) }
    return out
  }

  private fun getProject(globalScope: GlobalScope): Project = globalScope.userHome.currentProject
}