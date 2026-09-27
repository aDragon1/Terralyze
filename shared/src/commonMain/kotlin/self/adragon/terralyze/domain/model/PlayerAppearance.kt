package self.adragon.terralyze.domain.model

import self.adragon.terralyze.data.model.player.PlayerColors

data class PlayerAppearance(
    val hair: Int,
    val hairDye: Int?,
    val hideAccessories: List<Boolean>?,
    val hideMisc: Int?,
    val skinVariant: Int,
    val colors: PlayerColors,
)
