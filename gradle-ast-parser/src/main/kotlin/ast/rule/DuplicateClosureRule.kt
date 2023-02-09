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

import ast.violation.DuplicateClosureViolation
import ast.violation.Violation
import ast.visitor.Visitor
import ast.visitor.VisitorManager
import java.nio.file.Path
import java.util.TreeSet

internal class DuplicateClosureRule(
  private val visitorManager: VisitorManager,
  private val allowlistClosure: Set<String>
) : ClosureRule {

  override fun enforce(buildFile: Path, visitor: Visitor): Set<Violation> {
    val broken = TreeSet<Violation>()
    val nodes = visitorManager.getVisitedNodes()[visitor.javaClass] ?: return emptySet()

    nodes.forEach { (node, positions) ->
      if (positions.size > 1 && allowlistClosure.contains(node)) {
        broken.add(
          DuplicateClosureViolation(
            message = "The closure '$node' is duplicated ${positions.size} times in $buildFile. " +
              "Consider deleting all duplicative $node closures.",
            buildFile = buildFile
          )
        )
      }
    }
    return broken.toSortedSet()
  }
}
