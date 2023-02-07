package ast.rule

import ast.violation.Violation
import ast.visitor.Visitor
import java.nio.file.Path

internal interface Rule {
  fun enforce(buildFile: Path, visitor: Visitor): Set<Violation>
}

/** Marker interface specific to closures, to future-proof this multiple rules need to be applied
 * to closures
 */
internal interface ClosureRule : Rule

/** Marker interface specific to dependencies, to future-proof this multiple rules need to be applied
 * to dependencies
 */
internal interface DependencyRule : Rule
