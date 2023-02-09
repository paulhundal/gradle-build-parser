package di

import ast.AstGraph
import ast.DefaultAstGraph
import ast.visitor.DefaultVisitorManager
import ast.visitor.VisitorManager
import catalog.CatalogLocator
import commands.ViolationsCommand
import commands.SetupCommand
import converter.AllowlistConverter
import converter.DisallowedDependenciesConverter
import converter.IgnoreListConverter
import converter.ProjectConverter
import converter.RepositoryConverter
import di.GenericKoinModule.Companion.genericKoinApplication
import location.GlobalScope
import location.Pwd.Companion.pwd
import org.codehaus.groovy.ast.builder.AstBuilder
import org.koin.dsl.module
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import picocli.CommandLine
import picocli.CommandLine.IFactory
import utils.DefaultDistributionProvider
import utils.StringFileWriter
import utils.DefaultFindProjectFiles
import utils.Distribution
import utils.DistributionProvider
import utils.Files
import utils.FindProjectFiles
import utils.HelpFiles
import utils.RealDistribution
import utils.StringPathFileReader
import java.nio.file.FileSystems

internal class PicoFactory(
  private val globalScope: GlobalScope,
  private val distribution: Distribution,
  private val catalogLocator: CatalogLocator
) : IFactory {

  private val commands = module {
    single { SetupCommand(get(), get(), get(), get(), get(), get()) }
    single { ViolationsCommand(get(), get(), get(), get(), get(), get()) }
  }

  private val catalogs = module {
    single { ProjectConverter(catalogLocator.findProject()) }
    single { DisallowedDependenciesConverter(get()) }
    single { AllowlistConverter(get(), get()) }
    single { RepositoryConverter() }
    single { IgnoreListConverter() }
  }

  private val locations = module {
    single { globalScope }
    single { globalScope.userHome }
    single { globalScope.pwd() }
    single { globalScope.userHome.currentProject }
  }

  private val utils = module {
    single<AstGraph> { DefaultAstGraph(get(), AstBuilder()) }
    single<DistributionProvider> { DefaultDistributionProvider(get()) }
    single<FindProjectFiles> { DefaultFindProjectFiles() }
    single<Logger> { LoggerFactory.getLogger("AstParser") }
    single<Files> { HelpFiles(get()) }
    single<VisitorManager> { DefaultVisitorManager() }

    single { RealDistribution.of(FileSystems.getDefault()) }
    single { StringPathFileReader() }
    single { StringFileWriter(get()) }
  }

  private val koin by lazy {
    genericKoinApplication {
      modules(
        catalogs,
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