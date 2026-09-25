package terralyze.domain.model

import terralyze.data.model.ItemInfo

data class ResearchItem(
    val internalName: String,
    val amount: Int,
    val info: ItemInfo?
)

