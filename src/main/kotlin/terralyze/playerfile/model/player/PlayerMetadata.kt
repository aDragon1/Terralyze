package terralyze.playerfile.model.player

import terralyze.playerfile.model.FileType

data class PlayerMetadata(val type: FileType, val revision: UInt, val isFavorite: Boolean)