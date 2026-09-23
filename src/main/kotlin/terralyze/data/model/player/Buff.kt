package terralyze.data.model.player

import kotlinx.serialization.Serializable

@Serializable
data class Buff(val id: Int, val time: Int)