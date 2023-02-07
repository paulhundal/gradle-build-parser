package commands

import converter.RepositoryConverter
import picocli.CommandLine.Option
import java.nio.file.Path

class CommonOptions {
  @Option(
    names = ["--repository-path"],
    required = true,
    converter = [RepositoryConverter::class],
    description = [
      "Define for which repository to parse AST data."
    ]
  )
  lateinit var repository: Path
}