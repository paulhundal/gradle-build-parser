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
import org.slf4j.Logger
import java.nio.file.Path
import java.util.TreeSet

internal class UnsupportedDependencyRule(
  private val disallowedDependencies: Set<String>,
  private val logger: Logger,
  private val visitorManager: VisitorManager
) : DependencyRule {
  override fun enforce(buildFile: Path, visitor: Visitor): Set<Violation> {
    val violations = TreeSet<Violation>()
    val nodes = visitorManager.getVisitedNodes()[visitor.javaClass] ?: return emptySet()

    nodes.forEach {
      val locations = it.value
      val depStr = it.key
      val dep = it.toDependency()
      locations.forEach {
        if (disallowedDependencies.contains(depStr)) {
          violations.add(
            UnsupportedDependencyViolation(
              message = "You declared $dep at line $it in build file $buildFile. " +
                "This dependency is not supported. Please replace or remove.",
              buildFile = buildFile
            )
          )
        }
      }
    }
    return violations.toSet()
  }
}