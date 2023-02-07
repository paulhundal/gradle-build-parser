package ast.visitor

import ast.rule.ClosureNotSupportedRule

internal class VisitorFactory(
  private val allowList: Set<String>
) {
  fun create(): List<Visitor> {
    // Each visitor can enforce a list of rules. To append a rule to a visitor append it
    // to the list here
    val closureVisitors = ClosureVisitor(listOf(
      ClosureNotSupportedRule(allowList)
    ))

    return listOf(closureVisitors)
  }
}