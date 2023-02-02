package di

import commands.ListBuildFilesCommand
import di.GenericKoinModule.Companion.genericKoinApplication
import org.koin.dsl.module
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import picocli.CommandLine
import picocli.CommandLine.IFactory
import utils.DefaultFindProjectFiles
import utils.FindProjectFiles

internal class PicoFactory : IFactory {

  private val commands = module {
    single { ListBuildFilesCommand(get(), get()) }
  }

  private val utils = module {
    single<FindProjectFiles> { DefaultFindProjectFiles() }
    single<Logger> { LoggerFactory.getLogger("AstParser") }
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