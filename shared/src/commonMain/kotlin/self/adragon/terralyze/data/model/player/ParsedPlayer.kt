package self.adragon.terralyze.data.model.player

import self.adragon.terralyze.data.model.ParsedFieldValue
import self.adragon.terralyze.data.model.equipment.Equipment
import self.adragon.terralyze.data.model.item.Item

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

    val extraAccessorySlot: ParsedFieldValue<Boolean>,
    val biomeTorches: ParsedFieldValue<Boolean>,
    val usingBiomeTorches: ParsedFieldValue<Boolean>,
    val artisanLoaf: ParsedFieldValue<Boolean>,
    val aegisCrystal: ParsedFieldValue<Boolean>,
    val aegisFruit: ParsedFieldValue<Boolean>,
    val arcaneCrystal: ParsedFieldValue<Boolean>,
    val galaxyPearl: ParsedFieldValue<Boolean>,
    val gummyWorm: ParsedFieldValue<Boolean>,
    val ambrosia: ParsedFieldValue<Boolean>,

    val downedDd2Event: ParsedFieldValue<Boolean>,
    val taxMoney: ParsedFieldValue<Int>,

    val numberOfDeathsPVE: ParsedFieldValue<Int>,
    val numberOfDeathsPVP: ParsedFieldValue<Int>,

    val colors: PlayerColors,
    val equipment: Equipment,
    val inventory: List<Item>,

    val piggyBank: List<Item>,
    val safe: List<Item>,
    val defendersForge: ParsedFieldValue<List<Item>>,
    val voidVault: ParsedFieldValue<List<Item>>,
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