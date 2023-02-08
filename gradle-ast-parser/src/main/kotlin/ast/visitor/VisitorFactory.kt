package ast.visitor

import ast.rule.ClosureNotSupportedRule
import ast.rule.UnsupportedDependencyRule
import org.slf4j.Logger

internal class VisitorFactory(
  private val allowList: Set<String>,
  private val disallowedDependenciesList: Set<String>,
  private val logger: Logger
) {
  fun create(): List<Visitor> {
    // Each visitor can enforce a list of rules. To append a rule to a visitor append it
    // to the list here
    val closureVisitors = ClosureVisitor(
      allowlistClosures = allowList,
      logger = logger,
      closureRules = listOf(ClosureNotSupportedRule(allowList, logger))
    )

    val dependencyVisitors = DependenciesVisitor(
      dependencyRules = listOf(UnsupportedDependencyRule(disallowedDependenciesList, logger))
    )

    return listOf(dependencyVisitors)
  }
}