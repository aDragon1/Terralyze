package terralyze.data.model.player

import terralyze.data.model.ParsedFieldValue
import terralyze.data.model.equipment.Equipment
import terralyze.data.model.item.BankItem
import terralyze.data.model.item.InventoryItem
import terralyze.data.model.item.RawItem

data class ParsedPlayer(
    val metadata: FileInfo,

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
    val shimmerUpgradesUsed: ParsedFieldValue<ShimmerUpgradesUsed>,

    val downedDd2Event: ParsedFieldValue<Boolean>,
    val taxMoney: ParsedFieldValue<Int>,

    val numberOfDeathsPVE: ParsedFieldValue<Int>,
    val numberOfDeathsPVP: ParsedFieldValue<Int>,

    val colors: PlayerColors,
    val equipment: Equipment<RawItem>,
    val inventory: List<InventoryItem<RawItem>>,

    val piggyBank: List<BankItem<RawItem>>,
    val safe: List<BankItem<RawItem>>,
    val defendersForge: ParsedFieldValue<List<BankItem<RawItem>>>,
    val voidVault: ParsedFieldValue<List<InventoryItem<RawItem>>>,
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
    val researchEntries: ParsedFieldValue<List<ResearchEntry>>,
    )