package utils

import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists

internal interface BuildFilesLocation {
  fun get(): Path?
}

internal class DefaultBuildFilesLocation : BuildFilesLocation {
  override fun get(): Path? {
    val home = System.getProperty("user.home")
    return try {
      val output = Paths.get(home).resolve("gradle-ast-parser")
      if(!output.exists()) {
        output.createDirectories()
      }
      val buildFiles = output.resolve(BUILD_FILES_NAME)
      buildFiles.deleteIfExists()
      buildFiles.createFile()
    } catch (ex: Exception) {
      null
    }
  }

  companion object {
    const val BUILD_FILES_NAME = "build-files.txt"
  }
}