package terralyze.playerfile.model.player

import terralyze.playerfile.model.Color
import kotlinx.serialization.Serializable

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