package self.adragon.terralyze.data.model.player

import kotlinx.serialization.Serializable
import self.adragon.terralyze.data.model.PlayerColor

@Serializable
data class PlayerColors(
    val hair: PlayerColor,
    val skin: PlayerColor,
    val eyes: PlayerColor,
    val shirt: PlayerColor,
    val undershirt: PlayerColor,
    val pants: PlayerColor,
    val shoes: PlayerColor
)