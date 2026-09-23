package terralyze.data.model.player

import kotlinx.serialization.Serializable
import terralyze.data.model.Color

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