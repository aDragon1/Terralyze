package terralyze.playerfile.model.player

import terralyze.playerfile.model.equipment.Equipment
import terralyze.playerfile.model.item.BankItem
import terralyze.playerfile.model.item.InventoryItem
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class ParsedPlayer(
    val version: Int,
    val metadata: PlayerMetadata,

    val name: String,
    val difficulty: UByte,
    val playtimeTicks: Long,

    val hair: Int,
    val hairDye: UByte,
    val team: UByte,
    val hideAccessories: List<Boolean>,
    val hideMisc: UByte,
    val skinVariant: UByte,

    val life: Int,
    val maxLife: Int,
    val mana: Int,
    val maxMana: Int,

    val hasExtraAccessorySlot: Boolean,
    val unlockedBiomeTorches: Boolean,
    val usingBiomeTorches: Boolean,

    val shimmerUpgrades: ShimmerUpgrades,

    val downedDd2Event: Boolean,
    val taxMoney: Int,

    val numberOfDeathsPVE: Int,
    val numberOfDeathsPVP: Int,

    val colors: PlayerColors,
    val equipment: Equipment,
    val inventory: List<InventoryItem>,

    val piggyBank: List<BankItem>,
    val safe: List<BankItem>,
    val defendersForge: List<BankItem>,
    val voidVault: List<InventoryItem>,
    val voidVaultInfo: UByte,

    val buffs: List<Buff>,
    val sp: List<SP>,

    val hotbarLocked: Boolean,
    val hideInfo: List<Boolean>,
    val anglerQuestsFinished: Int,
    val dpadRadialBinding: List<Int>,

    val builderAccStatus: List<Int>,
    val bartenderQuestLog: Int,

    val isPlayerDead: Boolean,
    val playerRespawnTimer: Int,
    val lastTimePlayerWasSaved: Long,

    val golferScoreAccumulated: Int,

    // TODO: Journey mode research data isn't fully read yet (see parser TODO).
    // Keeping this raw count here so parse() can still return *something*
    // for it without pretending the feature is finished.
    val researchedItems: Int
) {
    val playtimeDuration: Duration
        get() = (playtimeTicks * 100).toDuration(DurationUnit.NANOSECONDS)
}