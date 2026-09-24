package terralyze.data.source.parser.plrformat

data class PLRFormatSpec(
    val version: Int,
    val difficultyFormat: DifficultyFormat,
    val isPlayTimeAvailable: Boolean,
    val isHairDyeAvailable: Boolean,
    val isTeamAvailable: Boolean,
    val hideAccessoriesFormat: HideAccessoriesFormat,
    val isHideMiscAvailable: Boolean,
    val isExtraAccessoryAvailable: Boolean,
    val isUnlockedBiomeTorchesAvailable: Boolean,
    val isUsingBiomeTorchesAvailable: Boolean,
    val isAteArtisanBreadAvailable: Boolean,
    val isShimmerUpgradesAvailable: Boolean,
    val isDownedDD2EventAnyDifficultyAvailable: Boolean,
    val isTaxMoneyAvailable: Boolean,
    val isNumberOfDeathsAvailable: Boolean,
    val isArmorFavoritedAvailable: Boolean,
    val isInventoryFavoritedAvailable: Boolean,
    val miscEquipmentsFormat: MiscEquipmentsFormat,
    val armorSize: Int,
    val accessoriesSize: Int,
    val dyeSize: Int,
    val inventorySize: Int,
    val bankSize: Int,
    val isBank3Available: Boolean,
    val isBank4Available: Boolean,
    val isVoidVaultInfoAvailable: Boolean,
    val isBuffAvailable: Boolean,
    val buffSize: Int,
    val isHotbarLockedAvailable: Boolean,
    val isHideInfoAvailable: Boolean,
    val isAnglerQuestsFinishedAvailable: Boolean,
    val isDpadRadialBindingAvailable: Boolean,
    val isBartenderQuestLogAvailable: Boolean,
    val isPlayerDeadAvailable: Boolean,
    val isLastTimePlayerWasSavedAvailable: Boolean,
    val isGolferScoreAccumulatedAvailable: Boolean,
    val isBank4FavoriteAvailable: Boolean,
    val bank4Size: Int,
    val shouldSkipBeforeShimmer: Boolean,
    val shouldSkipBeforeResearches: Boolean,
    val isResearchAvailable: Boolean
)

enum class DifficultyFormat {
    ABSENT,
    LEGACY_BOOLEAN,
    BYTE
}

enum class HideAccessoriesFormat {
    ABSENT,
    EIGHT_BITS,
    TEN_BITS
}

enum class MiscEquipmentsFormat {
    ABSENT,
    IGNORE_INDEX_ONE,
    NORMAL
}
