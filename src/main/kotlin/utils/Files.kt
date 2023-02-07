package utils

import org.slf4j.Logger
import java.io.File
import java.nio.file.Path

internal interface Files {
  fun createOrOverwriteFile(fileName: String): Path?
}

internal class HelpFiles(
  private val logger: Logger
) : Files {
  override fun createOrOverwriteFile(fileName: String): Path? {
    return try {
      val file = File(fileName)
      if (file.exists()) file.delete()
      file.createNewFile()
      return file.toPath()
    } catch (ex: Exception) {
      logger.error("Could not create $fileName", ex)
      null
    }
  }
}