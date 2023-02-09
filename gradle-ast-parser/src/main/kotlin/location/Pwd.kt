package location

import java.nio.file.Path

internal class Pwd(override val directory: Path) : Location {
  companion object {
    fun GlobalScope.pwd() = fileSystem.getPath(".").toAbsolutePath().normalize()
  }
}