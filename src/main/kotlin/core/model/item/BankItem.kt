package core.model.item

import kotlinx.serialization.Serializable

@Serializable
data class BankItem(val item: Item, val stack: Int)