package di

import ast.AstGraph
import ast.DefaultAstGraph
import commands.SaveBuildFilesCommand
import di.GenericKoinModule.Companion.genericKoinApplication
import location.GlobalScope
import location.GradleBuildParser.Companion.gradleAstParser
import org.koin.dsl.module
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import picocli.CommandLine
import picocli.CommandLine.IFactory
import utils.DefaultDistributionProvider
import utils.StringFileWriter
import utils.DefaultFindProjectFiles
import utils.DistributionProvider
import utils.FindProjectFiles
import utils.RealDistribution
import utils.StringPathFileReader
import java.nio.file.FileSystems

internal class PicoFactory(
  private val globalScope: GlobalScope
) : IFactory {

  private val commands = module {
    single { SaveBuildFilesCommand(get(), get(), get(), get()) }
  }

  private val locations = module {
    single { globalScope }
    single { globalScope.userHome }
  }

  private val utils = module {
    single<AstGraph> { DefaultAstGraph(get()) }
    single<DistributionProvider> { DefaultDistributionProvider(get()) }
    single<FindProjectFiles> { DefaultFindProjectFiles() }
    single<Logger> { LoggerFactory.getLogger("AstParser") }

    single { RealDistribution.of(FileSystems.getDefault()) }
    single { StringPathFileReader() }
    single { StringFileWriter(get()) }
  }

  private val koin by lazy {
    genericKoinApplication {
      modules(
        commands,
        utils,
        locations
      )
    }
  }
  override fun <K : Any> create(cls: Class<K>): K {
    // Classes passed into di.PicoFactory are expected to be concrete.
    return koin.getConcreteOrNull(cls.kotlin)
      ?: CommandLine.defaultFactory().create(cls)
  }
}