package converter

import picocli.CommandLine.ITypeConverter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class IgnoreListConverter : ITypeConverter<Set<Path>> {
  override fun convert(value: String): Set<Path> {
    if (value.isEmpty()) { return emptySet() }
    return runCatching {
      Files.readAllLines(Paths.get(value)).map {
        Paths.get(it)
      }.toSet()
    }.fold(
      onSuccess = { it },
      onFailure = { emptySet() }
    )
  }
}