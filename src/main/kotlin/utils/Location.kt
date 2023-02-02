package utils

import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.exists

internal interface Location {
  fun get(fileName: String): Path?
}

internal class DefaultLocation : Location {
  override fun get(fileName: String): Path? {
    val home = System.getProperty("user.home")
    return try {
      val output = Paths.get(home).resolve("gradle-ast-parser")
      if(!output.exists()) {
        output.createDirectories()
      }
      val location = output.resolve(fileName)
      if (!location.exists()) {
        location.createFile()
      }
      location
    } catch (ex: Exception) {
      null
    }
  }

  companion object {
    const val BUILD_FILES_NAME = "build-files.txt"
  }
}