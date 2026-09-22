package terralyze.playerfile.model.player

import kotlinx.serialization.Serializable

@Serializable
data class ShimmerUpgrades(
    val ateArtisanBread: Boolean,
    val usedAegisCrystal: Boolean,
    val usedAegisFruit: Boolean,
    val usedArcaneCrystal: Boolean,
    val usedGalaxyPearl: Boolean,
    val usedGummyWorm: Boolean,
    val usedAmbrosia: Boolean
)