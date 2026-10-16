package self.adragon.terralyze.domain.model

data class PlayerProgress(
    val permanentUpgrades: List<PermanentUpgrade>,
    val downedDd2Event: Boolean?,
    val numberOfAnglerQuestsFinished: Int?,
    val golferScoreAccumulated: Int?,
    val bartenderQuestLog: Int?,
)
