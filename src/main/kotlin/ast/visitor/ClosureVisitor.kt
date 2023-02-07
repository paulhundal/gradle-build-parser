package ast.visitor

import ast.rule.ClosureRule
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression

internal class ClosureVisitor(closureRules: List<ClosureRule>) : Visitor(closureRules) {
  override val visitedNodes: Map<String, Int>
    get() = closures.toMap()

  private val methodCalls: HashMap<Int, String> = HashMap()
  private val closures: HashMap<String, Int> = HashMap()
  private var start = -1
  private var end = -1
  // There are a set of closures we don't check the nested closures for
  private val ignoredNestedClosureBlocks = listOf("android","dependencies")
  // Maintain a blocked out list of line numbers that we don't visit if they're within a closure
  // that is nested and that we ignore
  private val ignoredBlocks: ArrayList<Int> = ArrayList()

  override fun visitMethodCallExpression(call: MethodCallExpression) {
    if (ignoredNestedClosureBlocks.contains(call.methodAsString)) {
      start = call.lineNumber
      end = call.lastLineNumber
      for (x in start .. end) {
        ignoredBlocks.add(x)
      }
    }
    val method = call.method.text
    methodCalls[call.method.lineNumber] = method
    super.visitMethodCallExpression(call)
  }

  override fun visitClosureExpression(expression: ClosureExpression) {
    val lineNumber = expression.lineNumber
    // We don't care about closures within ignored blocks
    if(methodCalls.containsKey(lineNumber) && !ignoredBlocks.contains(lineNumber)) {
      val name = methodCalls.getValue(lineNumber)
      closures[name] = expression.lineNumber
    }
    super.visitClosureExpression(expression)
  }

  override fun clear() {
    methodCalls.clear()
    closures.clear()
    ignoredBlocks.clear()
    start = -1
    end = -1
  }
}

