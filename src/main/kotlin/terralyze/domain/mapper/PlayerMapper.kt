package terralyze.domain.mapper

import terralyze.data.model.ItemInfo
import terralyze.data.model.equipment.Equipment
import terralyze.data.model.equipment.EquipmentSlot
import terralyze.data.model.equipment.MiscEquipmentSlot
import terralyze.data.model.getOrNull
import terralyze.data.model.item.BankItem
import terralyze.data.model.item.EquipmentItem
import terralyze.data.model.item.InventoryItem
import terralyze.data.model.item.RawItem
import terralyze.data.model.player.ParsedPlayer
import terralyze.data.model.player.ResearchEntry
import terralyze.data.repository.ItemCatalog
import terralyze.domain.model.Difficulty
import terralyze.domain.model.Item
import terralyze.domain.model.ItemSlot
import terralyze.domain.model.Player
import terralyze.domain.model.PlayerAppearance
import terralyze.domain.model.PlayerProgress
import terralyze.domain.model.PlayerStats
import terralyze.domain.model.PlayerStorage
import terralyze.domain.model.ResearchItem
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class PlayerMapper {
    fun parsedPlayerToDomain(catalog: ItemCatalog, parsed: ParsedPlayer): Player {
        return Player(
            metadata = parsed.metadata,
            name = parsed.name,
            difficulty = parsed.difficulty.getOrNull()?.let(::mapDifficulty) ?: Difficulty.NONE,
            playtime = parsed.playtimeTicks.getOrNull()?.let(::mapPlaytime),
            appearance = mapAppearance(parsed),
            playerStats = mapPlayerStats(parsed),
            team = parsed.team.getOrNull()?.toInt(),
            progress = mapProgress(parsed),
            taxMoney = parsed.taxMoney.getOrNull(),
            equipment = mapEquipment(catalog, parsed.equipment),
            inventory = mapInventory(catalog, parsed.inventory),
            playerStorage = mapStorage(catalog, parsed),
            voidVaultInfo = parsed.voidVaultInfo.getOrNull()?.toInt(),
            buffs = parsed.buffs.getOrNull(),
            sp = parsed.sp,
            usingBiomeTorches = parsed.usingBiomeTorches.getOrNull(),
            hotbarLocked = parsed.hotbarLocked.getOrNull(),
            hideInfo = parsed.hideInfo.getOrNull(),
            dpadRadialBinding = parsed.dpadRadialBinding.getOrNull(),
            builderAccStatus = parsed.builderAccStatus,
            isPlayerDead = parsed.isPlayerDead.getOrNull(),
            playerRespawnTimer = parsed.playerRespawnTimer.getOrNull(),
            lastTimePlayerWasSaved = parsed.lastTimePlayerWasSaved.getOrNull(),
            researchEntries = mapResearches(catalog, parsed.researchEntries.getOrNull()),
        )
    }

    fun mapResearches(catalog: ItemCatalog, entries: List<ResearchEntry>?) = entries?.map {
        ResearchItem(it.internalName, it.amount, catalog.getItemByInternalName(it.internalName))
    }

    fun mapStorage(catalog: ItemCatalog, parsed: ParsedPlayer): PlayerStorage {
        fun mapBankItem(rawBankItem: BankItem<RawItem>) =
            BankItem(resolveItemById(catalog, rawBankItem.item), rawBankItem.stack)

        val piggyBank = parsed.piggyBank.map { mapBankItem(it) }
        val safe = parsed.safe.map { mapBankItem(it) }
        val defendersForge = parsed.defendersForge.getOrNull()?.map { mapBankItem(it) }
        val voidVault = parsed.voidVault.getOrNull()?.map { mapInventoryItem(catalog, it) }

        return PlayerStorage(piggyBank, safe, defendersForge, voidVault)
    }

    private fun mapInventoryItem(catalog: ItemCatalog, rawItem: InventoryItem<RawItem>) =
        InventoryItem(resolveItemById(catalog, rawItem.item), rawItem.stack, rawItem.favorited)

    private fun mapInventory(catalog: ItemCatalog, inventory: List<InventoryItem<RawItem>>) =
        inventory.map { mapInventoryItem(catalog, it) }

    private fun mapEquipment(catalog: ItemCatalog, rawEquipment: Equipment<RawItem>): Equipment<ItemSlot> {
        fun mapEquipmentItem(rawEquipmentItem: EquipmentItem<RawItem>) = EquipmentItem(
            resolveItemById(catalog, rawEquipmentItem.item),
            rawEquipmentItem.favorited
        )

        fun mapEquipmentSlot(rawSlot: EquipmentSlot<RawItem>) = EquipmentSlot(
            mapEquipmentItem(rawSlot.main),
            mapEquipmentItem(rawSlot.vanity),
            mapEquipmentItem(rawSlot.dye)
        )

        fun mapMiscEquipmentSlot(rawSlot: MiscEquipmentSlot<RawItem>) = MiscEquipmentSlot(
            resolveItemById(catalog, rawSlot.main),
            resolveItemById(catalog, rawSlot.dye)
        )

        val rawArmor = rawEquipment.armor
        val rawAccessories = rawEquipment.accessories
        val rawMisc = rawEquipment.misc

        val armor = rawArmor.map { mapEquipmentSlot(it) }
        val accessories = rawAccessories.map { mapEquipmentSlot(it) }
        val misc = rawMisc.map { mapMiscEquipmentSlot(it) }

        return Equipment(armor, accessories, misc)
    }

    private fun mapProgress(parsed: ParsedPlayer) = PlayerProgress(
        hasExtraAccessorySlot = parsed.hasExtraAccessorySlot.getOrNull(),
        unlockedBiomeTorches = parsed.unlockedBiomeTorches.getOrNull(),
        downedDd2Event = parsed.downedDd2Event.getOrNull(),
        ateArtisanBread = parsed.ateArtisanBread.getOrNull(),
        shimmerUpgradesUsed = parsed.shimmerUpgradesUsed.getOrNull(),
        numberOfAnglerQuestsFinished = parsed.anglerQuestsFinished.getOrNull(),
        golferScoreAccumulated = parsed.golferScoreAccumulated.getOrNull(),
        bartenderQuestLog = parsed.bartenderQuestLog.getOrNull()
    )

    private fun mapPlayerStats(parsed: ParsedPlayer) = PlayerStats(
        life = parsed.life,
        mana = parsed.mana,
        maxLife = parsed.maxLife,
        maxMana = parsed.maxMana,
        deathsPVE = parsed.numberOfDeathsPVE.getOrNull(),
        deathsPVP = parsed.numberOfDeathsPVP.getOrNull()
    )

    private fun mapAppearance(parsed: ParsedPlayer) = PlayerAppearance(
        hair = parsed.hair,
        hairDye = parsed.hairDye.getOrNull()?.toInt(),
        hideAccessories = parsed.hideAccessories.getOrNull(),
        hideMisc = parsed.hideMisc.getOrNull()?.toInt(),
        skinVariant = parsed.skinVariant.toInt(),
        colors = parsed.colors
    )

    private fun mapPlaytime(ticks: Long) = (ticks * 100).toDuration(DurationUnit.NANOSECONDS)

    private fun mapDifficulty(difficultyValue: UByte) = when (difficultyValue) {
        0.toUByte() -> Difficulty.CLASSIC
        1.toUByte() -> Difficulty.MEDIUMCORE
        2.toUByte() -> Difficulty.HARDCORE
        3.toUByte() -> Difficulty.JOURNEY
        else -> Difficulty.NONE
    }

    private fun mapItemInfo(raw: RawItem, info: ItemInfo) = Item(
        id = raw.id,
        prefix = raw.prefix.toInt(),
        name = info.name,
        tags = info.tags,
        itemUrl = info.itemUrl,
        imageUrl = info.imageUrl,
    )

    private fun resolveItemById(catalog: ItemCatalog, raw: RawItem): ItemSlot =
        when {
            raw.id == 0 -> ItemSlot.Empty
            else -> catalog.getItemById(raw.id)
                ?.let { info -> ItemSlot.Resolved(mapItemInfo(raw, info)) }
                ?: ItemSlot.UnknownById(raw.id)
        }
}