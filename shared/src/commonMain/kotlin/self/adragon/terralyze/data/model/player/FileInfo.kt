package self.adragon.terralyze.data.model.player

import self.adragon.terralyze.data.model.FileType

data class FileInfo(val version: Int, val type: FileType, val revision: UInt, val isFavorite: Boolean)