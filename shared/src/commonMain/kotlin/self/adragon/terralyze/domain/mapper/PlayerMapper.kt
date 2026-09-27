package self.adragon.terralyze.domain.mapper

import self.adragon.terralyze.data.model.ItemInfo
import self.adragon.terralyze.data.model.equipment.Equipment
import self.adragon.terralyze.data.model.equipment.EquipmentSlot
import self.adragon.terralyze.data.model.equipment.MiscEquipmentSlot
import self.adragon.terralyze.data.model.getOrNull
import self.adragon.terralyze.data.model.item.BankItem
import self.adragon.terralyze.data.model.item.EquipmentItem
import self.adragon.terralyze.data.model.item.InventoryItem
import self.adragon.terralyze.data.model.item.RawItem
import self.adragon.terralyze.data.model.player.ParsedPlayer
import self.adragon.terralyze.data.model.player.ResearchEntry
import self.adragon.terralyze.data.repository.ItemCatalog
import self.adragon.terralyze.domain.model.Difficulty
import self.adragon.terralyze.domain.model.Item
import self.adragon.terralyze.domain.model.ItemSlot
import self.adragon.terralyze.domain.model.Player
import self.adragon.terralyze.domain.model.PlayerAppearance
import self.adragon.terralyze.domain.model.PlayerProgress
import self.adragon.terralyze.domain.model.PlayerStats
import self.adragon.terralyze.domain.model.PlayerStorage
import self.adragon.terralyze.domain.model.ResearchItem
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

    private fun mapResearches(catalog: ItemCatalog, entries: List<ResearchEntry>?) = entries?.map {
        ResearchItem(it.internalName, it.amount, catalog.getItemByInternalName(it.internalName))


//        TODO: MAP it too
//        #addResearchOverride(items) {
//        const itemGroups = [
//            [2611, 5526],
//            [4131, 5325],
//            [4346, 5391],
//            [4767, 5453],
//            [5309, 5454],
//            [5323, 5455],
//            [5324, 5329, 5330],
//            [5358, 5359, 5360, 5361, 5437],
//            [6168, 6169, 6193, 6194],
//            [6190, 6195],
//        ];
//
//        const groupById = new Map();
//
//        for (const itemGroup of itemGroups) {
//        for (const itemId of itemGroup) {
//        groupById.set(itemId, itemGroup);
//    }
//    }
//
//        const markedGroups = new Set();
//
//        for (const item of items) {
//        if (item.fullyResearched) {
//            const itemGroup = groupById.get(item.id);
//            if (itemGroup) {
//                markedGroups.add(itemGroup);
//            }
//        }
//    }
//
//        for (const item of items) {
//        const itemGroup = groupById.get(item.id);
//
//        if (itemGroup && markedGroups.has(itemGroup)) {
//            item.fullyResearched = true;
//            item.researched = item.neededForResearch;
//        }
//    }
//    }
    }

    private fun mapStorage(catalog: ItemCatalog, parsed: ParsedPlayer): PlayerStorage {
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