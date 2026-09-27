package self.adragon.terralyze.domain.model

import self.adragon.terralyze.data.model.ItemInfo

data class ResearchItem(
    val internalName: String,
    val amount: Int,
    val info: ItemInfo?
)

