package terralyze.domain.model

import terralyze.data.model.player.ShimmerUpgradesUsed

data class PlayerProgress(
    val hasExtraAccessorySlot: Boolean?,
    val unlockedBiomeTorches: Boolean?,
    val downedDd2Event: Boolean?,
    val ateArtisanBread: Boolean?,
    val shimmerUpgradesUsed: ShimmerUpgradesUsed?,
    val numberOfAnglerQuestsFinished: Int?,
    val golferScoreAccumulated: Int?,
    val bartenderQuestLog: Int?,
)
