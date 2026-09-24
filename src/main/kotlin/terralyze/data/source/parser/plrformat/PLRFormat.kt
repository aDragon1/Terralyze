package terralyze.data.source.parser.plrformat


object PLRFormat {
    fun forVersion(version: Int) = PLRFormatSpec(
        version = version,

        difficultyFormat = when {
            version < 10 -> DifficultyFormat.ABSENT
            version < 17 -> DifficultyFormat.LEGACY_BOOLEAN
            else -> DifficultyFormat.BYTE
        },
        isPlayTimeAvailable = version >= 138,
        isHairDyeAvailable = version >= 82,
        isTeamAvailable = version >= 283,
        hideAccessoriesFormat = when {
            version >= 124 -> HideAccessoriesFormat.TEN_BITS
            version >= 83 -> HideAccessoriesFormat.EIGHT_BITS
            else -> HideAccessoriesFormat.ABSENT
        },

        isHideMiscAvailable = version >= 119,
        isExtraAccessoryAvailable = version >= 125,
        isUnlockedBiomeTorchesAvailable = version >= 229,
        isUsingBiomeTorchesAvailable = version >= 229,
        isAteArtisanBreadAvailable = version >= 256,
        shouldSkipBeforeShimmer = version >= 324,
        isShimmerUpgradesAvailable = version >= 260,
        isDownedDD2EventAnyDifficultyAvailable = version >= 182,
        isTaxMoneyAvailable = version >= 128,
        isNumberOfDeathsAvailable = version >= 254,
        isArmorFavoritedAvailable = version >= 322,
        isInventoryFavoritedAvailable = version >= 114,
        armorSize = 3,
        accessoriesSize = 7,
        dyeSize = 10,
        inventorySize = 58,
        miscEquipmentsFormat =
            when {
                version < 117 -> MiscEquipmentsFormat.ABSENT
                version < 136 -> MiscEquipmentsFormat.IGNORE_INDEX_ONE
                else -> MiscEquipmentsFormat.NORMAL
            },
        bankSize = if (version < 58) 20 else 40,
        isBank3Available = version >= 182,
        isBank4Available = version >= 198,
        isBank4FavoriteAvailable = version >= 255,
        bank4Size = 40,
        isVoidVaultInfoAvailable = version >= 199,
        isBuffAvailable = version >= 11,
        buffSize = when {
            version < 74 -> 10
            version >= 252 -> 44
            else -> 22
        },
        isHotbarLockedAvailable = version >= 16,
        isHideInfoAvailable = version >= 115,
        isAnglerQuestsFinishedAvailable = version >= 98,
        isDpadRadialBindingAvailable = version >= 162,
        isBartenderQuestLogAvailable = version >= 181,
        isPlayerDeadAvailable = version >= 200,
        isLastTimePlayerWasSavedAvailable = version >= 202,
        isGolferScoreAccumulatedAvailable = version >= 206,
        isResearchAvailable = version >=218,
        shouldSkipBeforeResearches = version >= 282
    )
}

