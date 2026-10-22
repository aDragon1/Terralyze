package self.adragon.terralyze.domain.model

data class Item(
    val id: Int,
    val prefix: Int,
    val name: String,
    val tags: List<ItemTag>,
    val itemUrl: String,
    val imageUrl: String,
)