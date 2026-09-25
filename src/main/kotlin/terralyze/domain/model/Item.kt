package terralyze.domain.model

data class Item(
    val id: Int,
    val prefix: Int,
    val name: String,
    val tags: Map<String, List<String>>, // TODO: Item.tags
    val itemUrl: String,
    val imageUrl: String,
)