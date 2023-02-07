package location

import java.nio.file.Path

internal data class UserHome(override val directory: Path) : Location