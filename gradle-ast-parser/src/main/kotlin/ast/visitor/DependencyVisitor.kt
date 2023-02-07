package ast.visitor

import ast.rule.DependencyRule
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.stmt.ExpressionStatement

/**
 * Visits dependency block and returns all dependency statements without the reflection statement
 * (e.g. this.) block.
 */
internal class DependenciesVisitor(dependencyRules: List<DependencyRule>): Visitor(dependencyRules) {
  override val visitedNodes: HashMap<String, Int> = HashMap()

  private var depsStart = -1
  private var depsEnd = -1

  override fun visitMethodCallExpression(call: MethodCallExpression) {
    if (call.methodAsString == "dependencies") {
      depsStart = call.lineNumber
      depsEnd = call.lastLineNumber
    }
    super.visitMethodCallExpression(call)
  }

  override fun visitExpressionStatement(statement: ExpressionStatement) {
    if (statement.lineNumber > depsStart && statement.lastLineNumber < depsEnd) {
      visitedNodes[statement.text.replace("this.", "")] = statement.lineNumber
    }
    super.visitExpressionStatement(statement)
  }

  override fun clear() {
    visitedNodes.clear()
    depsStart = -1
    depsEnd = -1
  }
}
