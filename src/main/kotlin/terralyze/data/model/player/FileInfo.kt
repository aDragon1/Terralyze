package terralyze.data.model.player

import terralyze.data.model.FileType

data class FileInfo(val version: Int, val type: FileType, val revision: UInt, val isFavorite: Boolean)