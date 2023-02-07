package ast.visitor

import ast.rule.Rule
import org.codehaus.groovy.ast.CodeVisitorSupport

/**
 * Marker interface to identify a Visitor of the Groovy AST
 */
internal abstract class Visitor(val rules: List<Rule>) : CodeVisitorSupport() {
  abstract val visitedNodes: Map<String, Int>

  abstract fun clear()
}
