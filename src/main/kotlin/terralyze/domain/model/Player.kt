package terralyze.domain.model

import terralyze.data.model.equipment.Equipment
import terralyze.data.model.item.InventoryItem
import terralyze.data.model.player.Buff
import terralyze.data.model.player.FileInfo
import terralyze.data.model.player.SP
import kotlin.time.Duration

/*
    If field is Null that means what field is absent in that specific file version
    Handle it properly on UI
 */
data class Player(
    val metadata: FileInfo,

    val name: String,
    val difficulty: Difficulty,
    val playtime: Duration?,

    val appearance: PlayerAppearance,
    val playerStats: PlayerStats,
    val team: Int?,

    val progress: PlayerProgress,
    val taxMoney: Int?,

    val equipment: Equipment<ItemSlot>,
    val inventory: List<InventoryItem<ItemSlot>>,
    val playerStorage: PlayerStorage,
    val voidVaultInfo: Int?,

    val buffs: List<Buff>?,
    val sp: List<SP>,

    val usingBiomeTorches: Boolean?,
    val hotbarLocked: Boolean?,
    val hideInfo: List<Boolean>?,

    val dpadRadialBinding: List<Int>?,
    val builderAccStatus: List<Int>,

    val isPlayerDead: Boolean?,
    val playerRespawnTimer: Int?,
    val lastTimePlayerWasSaved: Long?,

    val researchEntries: List<ResearchItem>?,
)