package terralyze.domain.model

data class PlayerStats(
    val life: Int,
    val mana: Int,
    val maxLife: Int,
    val maxMana: Int,
    val deathsPVE: Int?,
    val deathsPVP: Int?,
)