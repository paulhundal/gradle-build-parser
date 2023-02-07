package converter

import picocli.CommandLine.ITypeConverter
import java.nio.file.Files
import java.nio.file.Paths

class AllowlistConverter : ITypeConverter<Set<String>> {
  override fun convert(value: String): Set<String> {
    return try {
      return Files.readAllLines(Paths.get(value)).toSet()
    } catch (ex: Exception) {
      emptySet()
    }
  }
}