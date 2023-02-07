package ast.rule

import ast.violation.ClosureViolation
import ast.violation.Violation
import ast.visitor.Visitor
import java.lang.StringBuilder
import java.nio.file.Path
import java.util.TreeSet

internal class ClosureNotSupportedRule(
  private val allowList: Set<String>
) : ClosureRule {
  override fun enforce(buildFile: Path, visitor: Visitor): Set<Violation> {
    val broken: TreeSet<Violation> = TreeSet()
    visitor.visitedNodes.keys.forEach { node ->
      if(allowList.none { node.contains(it) } ) {
        broken.add(
          ClosureViolation("The closure '$node' at line " +
            "${visitor.visitedNodes[node]} in $buildFile is not supported. " +
            "Please consider using one of the following closures: " +
            allowList.commaSep(), buildFile)
        )
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