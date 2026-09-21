package core.model.player

import core.model.FileType

data class PlayerMetadata(val type: FileType, val revision: UInt, val isFavorite: Boolean)