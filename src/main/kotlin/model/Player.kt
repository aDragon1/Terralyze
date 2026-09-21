package model

import kotlinx.serialization.Serializable
import self.adragon.model.FileType

data class Player(val metadata: PlayerMetadata, val colors: PlayerColors)

data class PlayerMetadata(val type: FileType, val revision: UInt, val isFavorite: Boolean)

@Serializable
data class PlayerColors(
    val hair: Color,
    val skin: Color,
    val eyes: Color,
    val shirt: Color,
    val undershirt: Color,
    val pants: Color,
    val shoes: Color
)

@Serializable
data class Color(
    val r: Int,
    val g: Int,
    val b: Int
)

@Serializable
data class SP(val x: Int, val y: Int, val i: Int, val n: String)