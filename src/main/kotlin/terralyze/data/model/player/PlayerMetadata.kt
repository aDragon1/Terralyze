package terralyze.data.model.player

import terralyze.data.model.FileType
data class PlayerMetadata(val type: FileType, val revision: UInt, val isFavorite: Boolean)