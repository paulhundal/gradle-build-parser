package location

import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

internal interface Location {
  val directory: Path

  fun resolve(
    other: String,
    vararg others: String
  ): Path = directory.resolve(other, *others)

  fun exists(): Boolean = directory.exists()

  fun create(): Path = directory.createDirectories()
}

internal fun Path.resolve(other: String, vararg others: String): Path {
  return others.fold(resolve(other), Path::resolve)
}
