package ast.rule

import ast.toDependencyOrNull
import ast.violation.UnsupportedDependencyViolation
import ast.violation.Violation
import ast.visitor.Visitor
import org.slf4j.Logger
import java.nio.file.Path
import java.util.TreeSet

internal class UnsupportedDependencyRule(
  private val disallowedDependencies: Set<String>,
  private val logger: Logger,
) : DependencyRule {
  override fun enforce(buildFile: Path, visitor: Visitor): Set<Violation> {
    val violations: TreeSet<Violation> = TreeSet()
    val dependencies = visitor.visitedNodes.filter { entry ->
      disallowedDependencies.any { entry.key.contains(it) }
    }.map { it.toDependencyOrNull() }


    dependencies.filterNotNull().forEach { dep ->
      violations.add(UnsupportedDependencyViolation(
        message = "You declared $dep at line ${dep.lineNumber} in build file $buildFile. " +
          "This dependency is not supported. Please replace or remove.",
        buildFile = buildFile
      ))
    }
    return violations.toSet()
  }
}