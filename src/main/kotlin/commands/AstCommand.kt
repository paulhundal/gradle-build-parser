package commands

import picocli.CommandLine.Command
import picocli.CommandLine.HelpCommand

@Command(
  name = "ast-parser",
  mixinStandardHelpOptions = true,
  version = ["1.0"],
  description = ["AST Parsing Assistant"],
  subcommands = [
    BuildAstCommand::class,
    HelpCommand::class,
    ListBuildFilesCommand::class
  ]
)
class AstCommand