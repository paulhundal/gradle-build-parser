package location

import location.Pwd.Companion.pwd
import java.nio.file.Path

internal class Binary(override val directory: Path) : Location {
  companion object {
    fun GlobalScope.binary() = pwd().resolve("binary")
  }
}