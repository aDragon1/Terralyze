package model

import self.adragon.model.FileType
import java.awt.Color

data class Player(val metadata: PlayerMetadata, val colors: PlayerColors)

data class PlayerMetadata(val type: FileType, val revision: UInt, val isFavorite: Boolean)

data class PlayerColors(
    val hair: Color,
    val skin: Color,
    val eyes: Color,
    val shirt: Color,
    val undershirt: Color,
    val pants: Color,
    val shoes: Color
)
