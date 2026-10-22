package self.adragon.terralyze.ui.hub.research

import self.adragon.terralyze.data.model.item.CatalogItem
import self.adragon.terralyze.domain.model.ResearchItem

sealed interface ResearchItemUiState {

    data class Unknown(
        val internalName: String,
        val amount: Int
    ) : ResearchItemUiState

    sealed interface Identified : ResearchItemUiState {
        val catalogItem: CatalogItem
        val amount: Int

        val isCompleted: Boolean
            get() = amount >= catalogItem.neededForResearch
    }

    data class NotStarted(override val catalogItem: CatalogItem) : Identified {
        override val amount: Int = 0
    }

    data class InProgress(override val catalogItem: CatalogItem, override val amount: Int) : Identified
    data class Completed(override val catalogItem: CatalogItem, override val amount: Int) : Identified
}

fun buildResearchUiState(
    catalogItems: List<CatalogItem>, playerResearches: List<ResearchItem>?
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
    if (catalogItem == null) return ResearchItemUiState.Unknown(internalName, amount)

    return when {
        amount <= 0 -> ResearchItemUiState.NotStarted(catalogItem)
        amount >= catalogItem.neededForResearch -> ResearchItemUiState.Completed(
            catalogItem,
            amount
        )

        else -> ResearchItemUiState.InProgress(catalogItem, amount)
    }
}