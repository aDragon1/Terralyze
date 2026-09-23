package terralyze.data.model.player

import terralyze.data.model.ParsedFieldValue
import terralyze.data.model.equipment.Equipment
import terralyze.data.model.item.BankItem
import terralyze.data.model.item.InventoryItem
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class ParsedPlayer(
    val version: Int,
    val metadata: PlayerMetadata,

    val name: String,
    val difficulty: ParsedFieldValue<UByte>,
    val playtimeTicks: ParsedFieldValue<Long>,

    val hair: Int,
    val hairDye: ParsedFieldValue<UByte>,
    val team: ParsedFieldValue<UByte>,
    val hideAccessories: ParsedFieldValue<List<Boolean>>,
    val hideMisc: ParsedFieldValue<UByte>,
    val skinVariant: UByte,

    val life: Int,
    val maxLife: Int,
    val mana: Int,
    val maxMana: Int,

    val hasExtraAccessorySlot: ParsedFieldValue<Boolean>,
    val unlockedBiomeTorches: ParsedFieldValue<Boolean>,
    val usingBiomeTorches: ParsedFieldValue<Boolean>,

    val ateArtisanBread: ParsedFieldValue<Boolean>,
    val shimmerUpgrades: ParsedFieldValue<ShimmerUpgrades>,

    val downedDd2Event: ParsedFieldValue<Boolean>,
    val taxMoney: ParsedFieldValue<Int>,

    val numberOfDeathsPVE: ParsedFieldValue<Int>,
    val numberOfDeathsPVP: ParsedFieldValue<Int>,

    val colors: PlayerColors,
    val equipment: Equipment,
    val inventory: List<InventoryItem>,

    val piggyBank: List<BankItem>,
    val safe: List<BankItem>,
    val defendersForge: ParsedFieldValue<List<BankItem>>,
    val voidVault: ParsedFieldValue<List<InventoryItem>>,
    val voidVaultInfo: ParsedFieldValue<UByte>,

    val buffs: ParsedFieldValue<List<Buff>>,
    val sp: List<SP>,

    val hotbarLocked: ParsedFieldValue<Boolean>,
    val hideInfo: ParsedFieldValue<List<Boolean>>,
    val anglerQuestsFinished: ParsedFieldValue<Int>,
    val dpadRadialBinding: ParsedFieldValue<List<Int>>,

    val builderAccStatus: List<Int>,
    val bartenderQuestLog: ParsedFieldValue<Int>,

    val isPlayerDead: ParsedFieldValue<Boolean>,
    val playerRespawnTimer: ParsedFieldValue<Int>,
    val lastTimePlayerWasSaved: ParsedFieldValue<Long>,

    val golferScoreAccumulated: ParsedFieldValue<Int>,

// TODO: Journey mode research data isn't fully read yet (see parser TODO).
// Keeping this raw count here so parse() can still return *something*
// for it without pretending the feature is finished.
    val researchedItems: Int
) {
    val playtimeDuration: Duration
        get() = when (playtimeTicks) {
            is ParsedFieldValue.Absent -> Duration.ZERO
            is ParsedFieldValue.Present -> {
                ((playtimeTicks.value) * 100).toDuration(DurationUnit.NANOSECONDS)
            }
        }
}