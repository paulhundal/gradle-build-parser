package options

import picocli.CommandLine.Option
import java.nio.file.Path

internal class CommonOptions {
  @Option(
    names = ["--repository-path"],
    defaultValue = Option.NULL_VALUE,
    converter = [RepositoryConverter::class],
    description = [
      "Define which repository to parse AST data for."
    ]
  )
  var repository: Path? = null
}