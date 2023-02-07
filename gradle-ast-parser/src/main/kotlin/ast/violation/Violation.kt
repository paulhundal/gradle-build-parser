package ast.violation

import java.nio.file.Path
import kotlin.io.path.name

internal interface Violation : Comparable<Violation> {
  val message: String
  val buildFile: Path

  override fun compareTo(other: Violation): Int {
    val comparison = this.buildFile.name.compareTo(other.buildFile.name)
    return if(comparison != 0) {
      comparison
    } else {
      this.message.compareTo(other.message)
    }
  }
}
