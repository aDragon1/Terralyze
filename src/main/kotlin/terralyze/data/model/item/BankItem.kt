package terralyze.data.model.item

import kotlinx.serialization.Serializable

@Serializable
data class BankItem<ItemType>(val item: ItemType, val stack: Int)