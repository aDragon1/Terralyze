package terralyze.data.model.player

import kotlinx.serialization.Serializable

@Serializable
data class ShimmerUpgradesUsed(
    val aegisCrystal: Boolean,
    val aegisFruit: Boolean,
    val arcaneCrystal: Boolean,
    val galaxyPearl: Boolean,
    val gummyWorm: Boolean,
    val ambrosia: Boolean
)