package utils

import org.slf4j.Logger
import java.nio.file.Path
import kotlin.io.path.writeText

interface FileWriter<OUTPUT> {
  fun write(out: OUTPUT, to: Path): Boolean
}

internal class StringFileWriter(
  private val logger: Logger
) : FileWriter<String> {
  override fun write(out: String, to: Path): Boolean {
    logger.info("Writing to $to")
    return runCatching {
      to.writeText(out)
    }.fold(
      onSuccess = {
        logger.info("Successfully wrote out to $to")
        true
      },
      onFailure = {
        logger.error("Unable to write to $to", it)
        false
      }
    )
  }
}