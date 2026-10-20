package self.adragon.terralyze.ui.hub.research

import self.adragon.terralyze.data.model.ItemInfo
import self.adragon.terralyze.domain.model.ResearchItem

sealed interface ResearchItemUiState {

    data class Unknown(
        val internalName: String,
        val amount: Int
    ) : ResearchItemUiState

    sealed interface Identified : ResearchItemUiState {
        val info: ItemInfo
        val amount: Int

        val isCompleted: Boolean
            get() = amount >= info.neededForResearch
    }

    data class NotStarted(
        override val info: ItemInfo
    ) : Identified {
        override val amount: Int = 0
    }

    data class InProgress(
        override val info: ItemInfo,
        override val amount: Int
    ) : Identified

    data class Completed(
        override val info: ItemInfo,
        override val amount: Int
    ) : Identified
}

fun buildResearchUiState(
    catalogItems: List<ItemInfo>, playerResearches: List<ResearchItem>?
): List<ResearchItemUiState> {
    if (playerResearches == null) return emptyList()

    val catalogMap = catalogItems.associateBy { it.internalName }
    val playerResearchMap = playerResearches.associateBy { it.internalName }

    val identifiedStates = catalogItems.map { itemInfo ->
        val researchItem = playerResearchMap[itemInfo.internalName]
            ?: ResearchItem(itemInfo.internalName, 0, itemInfo)

        researchItem.toUiState()
    }

    val unknownStates = playerResearches.filter { it.internalName !in catalogMap }
        .map { it.toUiState() }

    return identifiedStates + unknownStates
}

fun ResearchItem.toUiState(): ResearchItemUiState {
    if (info == null) return ResearchItemUiState.Unknown(internalName, amount)

    return when {
        amount <= 0 -> ResearchItemUiState.NotStarted(info)
        amount >= info.neededForResearch -> ResearchItemUiState.Completed(info, amount)
        else -> ResearchItemUiState.InProgress(info, amount)
    }
}