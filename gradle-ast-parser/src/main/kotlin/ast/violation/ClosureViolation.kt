package ast.violation

import java.nio.file.Path

internal data class ClosureViolation(
  override val message: String,
  override val buildFile: Path
) : Violation
