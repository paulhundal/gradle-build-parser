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

import ast.violation.ClosureViolation
import ast.violation.Violation
import ast.visitor.Visitor
import ast.visitor.VisitorManager
import org.slf4j.Logger
import java.lang.StringBuilder
import java.nio.file.Path
import java.util.TreeSet

internal class ClosureNotSupportedRule(
  private val allowList: Set<String>,
  private val logger: Logger,
  private val visitorManager: VisitorManager
) : ClosureRule {
  override fun enforce(buildFile: Path, visitor: Visitor): Set<Violation> {
    val broken = TreeSet<Violation>()
    val nodes = visitorManager.getVisitedNodes()[visitor.javaClass] ?: return emptySet()

    nodes.forEach { (node, positions) ->
      if (allowList.none { node.contains(it) }) {
        positions.forEach { position ->
          broken.add(
            ClosureViolation(
              "The closure '$node' at line $position in $buildFile is not supported. " +
                "Please consider using one of the following closures: ${allowList.commaSep()}",
              buildFile
            )
          )
        }
      }
    }

    return broken.toSet()
  }

  private fun Set<String>.commaSep(): String {
    var index = 0
    val sb = StringBuilder()
    this.forEach {
      sb.append(it)
      if (index < this.size - 1) {
        sb.append(", ")
      }
      index++
    }
    return sb.toString()
  }
}