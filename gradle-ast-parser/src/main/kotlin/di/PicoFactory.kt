package di

import ast.AstGraph
import ast.DefaultAstGraph
import ast.visitor.DefaultVisitorManager
import ast.visitor.Visitor
import ast.visitor.VisitorFactory
import ast.visitor.VisitorManager
import catalog.CatalogLocator
import catalog.Project
import commands.ScanCommand
import commands.ViolationsCommand
import commands.SetupCommand
import configuration.SetupConfiguration
import configuration.ViolationsConfiguration
import converter.AllowlistConverter
import converter.DisallowedDependenciesConverter
import converter.IgnoreListConverter
import converter.ProjectConverter
import converter.RepositoryConverter
import di.GenericKoinModule.Companion.genericKoinApplication
import di.GenericKoinModule.Companion.genericModule
import location.Binary.Companion.binary
import location.GlobalScope
import location.Pwd.Companion.pwd
import org.codehaus.groovy.ast.builder.AstBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import picocli.CommandLine
import picocli.CommandLine.IFactory
import utils.ALLOWLIST_CLOSURES
import utils.Configuration
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
import kotlin.io.path.readLines

internal class PicoFactory(
  private val globalScope: GlobalScope,
  private val distribution: Distribution,
  private val catalogLocator: CatalogLocator
) : IFactory {

  private val commands = genericModule {
    single { SetupCommand(get(), get(), get()) }
    single { ViolationsCommand(get(), get(), get(), get()) }
    single { ScanCommand(globalScope, get()) }
  }

  private val configuration = genericModule {
    single<Configuration<Project, GlobalScope>> {
      SetupConfiguration(get(), get(), get(), get(), get())
    }
    single<Configuration<List<Visitor>, GlobalScope>> {
      ViolationsConfiguration(get(), get(), get(), get(), get())
    }
  }

  private val catalogs = genericModule {
    single { ProjectConverter(catalogLocator.findProject()) }
    single { DisallowedDependenciesConverter(get()) }
    single { AllowlistConverter(get(), get()) }
    single { RepositoryConverter(globalScope.userHome) }
    single { IgnoreListConverter() }
  }

  private val locations = genericModule {
    single { globalScope }
    single { globalScope.userHome }
    single { globalScope.pwd() }
    single { globalScope.userHome.currentProject }
  }

  private val utils = genericModule {
    single<AstGraph> { DefaultAstGraph(get(), AstBuilder()) }
    single<DistributionProvider> { DefaultDistributionProvider(get()) }
    single<FindProjectFiles> { DefaultFindProjectFiles() }
    single<Logger> { LoggerFactory.getLogger("AstParser") }
    single<Files> { HelpFiles(get()) }
    single<VisitorManager> { DefaultVisitorManager() }
    single {
      VisitorFactory(
        allowList = globalScope.binary().resolve(ALLOWLIST_CLOSURES).readLines().toSet(),
        logger = get(),
        visitorManager = get()
      )
    }

    single { RealDistribution.of(FileSystems.getDefault()) }
    single { StringPathFileReader() }
    single { StringFileWriter(get()) }
  }

  private val koin by lazy {
    genericKoinApplication {
      modules(
        catalogs,
        configuration,
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