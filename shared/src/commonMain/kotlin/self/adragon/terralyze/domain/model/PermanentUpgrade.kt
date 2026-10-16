package self.adragon.terralyze.domain.model

data class PermanentUpgrade(
    val name: String,
    val imageUrl: String,
    val effect: String,
    val isUsed: Boolean?,
)