package options

import picocli.CommandLine.ITypeConverter
import java.nio.file.Path
import java.nio.file.Paths

class RepositoryConverter : ITypeConverter<Path> {
  override fun convert(value: String?): Path? {
    if (value == null) return null

    return runCatching { Paths.get(value) }
      .fold(onSuccess = { it }, onFailure = { null })
  }
}