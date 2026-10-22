package self.adragon.terralyze.domain.model

import self.adragon.terralyze.data.model.item.CatalogItem

data class ResearchItem(
    val internalName: String,
    val amount: Int,
    val catalogItem: CatalogItem?
)

