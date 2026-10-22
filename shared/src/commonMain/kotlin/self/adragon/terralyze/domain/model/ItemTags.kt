package self.adragon.terralyze.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ItemTag(val category: String, val name: String)