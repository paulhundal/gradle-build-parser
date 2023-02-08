package ast.violation

import java.nio.file.Path

data class UnsupportedDependencyViolation(
  override val message: String,
  override val buildFile: Path
) : Violation