package di

import ast.AstGraph
import ast.DefaultAstGraph
import commands.BuildAstCommand
import commands.ListBuildFilesCommand
import di.GenericKoinModule.Companion.genericKoinApplication
import org.koin.dsl.module
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import picocli.CommandLine
import picocli.CommandLine.IFactory
import utils.Location
import utils.DefaultLocation
import utils.StringFileWriter
import utils.DefaultFindProjectFiles
import utils.FindProjectFiles
import utils.StringPathFileReader

internal class PicoFactory : IFactory {

  private val commands = module {
    single { BuildAstCommand(get(), get(), get(), get()) }
    single { ListBuildFilesCommand(get(), get(), get(), get()) }
  }

  private val utils = module {
    single<AstGraph> { DefaultAstGraph(get()) }
    single<FindProjectFiles> { DefaultFindProjectFiles() }
    single<Location> { DefaultLocation() }
    single<Logger> { LoggerFactory.getLogger("AstParser") }

    // multi-binding with generics broken?
    single { StringPathFileReader() }
    single { StringFileWriter(get()) }
  }

  private val koin by lazy {
    genericKoinApplication {
      modules(
        commands,
        utils
      )
    }
  }
  override fun <K : Any> create(cls: Class<K>): K {
    // Classes passed into di.PicoFactory are expected to be concrete.
    return koin.getConcreteOrNull(cls.kotlin)
      ?: CommandLine.defaultFactory().create(cls)
  }
}