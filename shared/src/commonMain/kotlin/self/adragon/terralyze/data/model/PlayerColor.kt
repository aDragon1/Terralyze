package self.adragon.terralyze.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PlayerColor(
    val r: Int,
    val g: Int,
    val b: Int
)