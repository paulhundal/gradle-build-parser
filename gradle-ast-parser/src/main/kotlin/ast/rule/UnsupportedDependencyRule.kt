package ast.rule

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

import ast.toDependency
import ast.violation.UnsupportedDependencyViolation
import ast.violation.Violation
import ast.visitor.Visitor
import ast.visitor.VisitorManager
import catalog.UndesiredDependency
import converter.UndesiredDependencyConverter
import org.slf4j.Logger
import java.nio.file.Path
import java.util.TreeSet

internal class UnsupportedDependencyRule(
  private val logger: Logger,
  private val visitorManager: VisitorManager,
  private val undesiredDependencyConverter: UndesiredDependencyConverter
) : DependencyRule {
  override fun enforce(buildFile: Path, visitor: Visitor): Set<Violation> {
    val violations = TreeSet<Violation>()
    val nodes = visitorManager.getVisitedNodes()[visitor.javaClass] ?: return emptySet()

    val undesiredDeps = getUndesiredDeps()
    nodes.flatMap {
      it.toDependency()
    }.filter { dep ->
      undesiredDeps.any { it.declaration == dep.toString() }
    }.associateBy { dep ->
      undesiredDeps.first { it.declaration == dep.toString() }
    }.forEach { (t, u) ->
      violations.add(
        UnsupportedDependencyViolation(
          message = "You declared $u in build file $buildFile at line ${u.lineNumber} ." +
            "This dependency declaration is not supported. ${t.reason}",
          buildFile = buildFile
        )
      )
    }
    return violations.toSortedSet()
  }

  private fun getUndesiredDeps(): List<UndesiredDependency> {
    return defaultUndesiredDeps.map { undesiredDependencyConverter.convert(it) }
  }

  companion object {
    val defaultUndesiredDeps = listOf("slf4j")
  }
}