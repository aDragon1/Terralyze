package terralyze.domain.model

sealed interface ItemSlot {
    data object Empty : ItemSlot
    data class UnknownById(val id: Int) : ItemSlot
    data class Resolved(val item: Item) : ItemSlot
}