package terralyze.data.model.player

import kotlinx.serialization.Serializable

@Serializable
data class ResearchEntry(
    val internalName: String,
    val amount: Int
)