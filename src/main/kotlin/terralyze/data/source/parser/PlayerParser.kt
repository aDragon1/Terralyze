package terralyze.data.source.parser

import terralyze.data.model.Color
import terralyze.data.model.FileType
import terralyze.data.model.ParsedFieldValue
import terralyze.data.model.equipment.Equipment
import terralyze.data.model.equipment.EquipmentSlot
import terralyze.data.model.equipment.MiscEquipmentSlot
import terralyze.data.model.getOrNull
import terralyze.data.model.item.BankItem
import terralyze.data.model.item.EquipmentItem
import terralyze.data.model.item.InventoryItem
import terralyze.data.model.item.RawItem
import terralyze.data.model.player.Buff
import terralyze.data.model.player.ParsedPlayer
import terralyze.data.model.player.PlayerColors
import terralyze.data.model.player.FileInfo
import terralyze.data.model.player.ResearchEntry
import terralyze.data.model.player.SP
import terralyze.data.model.player.ShimmerUpgradesUsed
import terralyze.data.source.binary.BinaryReader
import terralyze.data.source.parser.plrformat.DifficultyFormat
import terralyze.data.source.parser.plrformat.HideAccessoriesFormat
import terralyze.data.source.parser.plrformat.MiscEquipmentsFormat
import terralyze.data.source.parser.plrformat.PLRFormat
import terralyze.data.source.parser.plrformat.PLRFormatSpec

/*
* Take and readapt source code of the https://terraria-research-tracker.free.nf
* Thank's, I suppose?
*/
class PlayerParser {
    fun parse(content: ByteArray): ParsedPlayer {
        val reader = BinaryReader(content)

        val version = reader.readS4()
        val spec = PLRFormat.forVersion(version)

        val metadata = readMetadata(reader, version)

        val name = reader.readString()
        val difficulty = readDifficulty(reader, spec.difficultyFormat)
        val playtime = readIfAvailable(spec.isPlayTimeAvailable) { reader.readS8() }

        val hair = reader.readS4()
        val hairDye = readIfAvailable(spec.isHairDyeAvailable) { reader.readU1() }

        val team = readIfAvailable(spec.isTeamAvailable) { reader.readU1() }

        val hideAccessories = readHideAccessories(reader, spec.hideAccessoriesFormat)
        val hideMisc = readIfAvailable(spec.isHideMiscAvailable) { reader.readU1() }

        val skinVariant = reader.readU1()

        val life = reader.readS4()
        val maxLife = reader.readS4()
        val mana = reader.readS4()
        val maxMana = reader.readS4()

        val hasExtraAccessorySlot = readIfAvailable(spec.isExtraAccessoryAvailable) { reader.readBoolean() }

        val unlockedBiomeTorches = readIfAvailable(spec.isUnlockedBiomeTorchesAvailable) { reader.readBoolean() }
        val usingBiomeTorches = readIfAvailable(spec.isUsingBiomeTorchesAvailable) { reader.readBoolean() }

        val ateArtisanBread = readIfAvailable(spec.isAteArtisanBreadAvailable) { reader.readBoolean() }
        if (spec.shouldSkipBeforeShimmer) reader.skip(1)
        val shimmerUpgradesUsed = readIfAvailable(spec.isShimmerUpgradesAvailable) {
            ShimmerUpgradesUsed(
                aegisCrystal = reader.readBoolean(),
                aegisFruit = reader.readBoolean(),
                arcaneCrystal = reader.readBoolean(),
                galaxyPearl = reader.readBoolean(),
                gummyWorm = reader.readBoolean(),
                ambrosia = reader.readBoolean()
            )
        }

        val downedDd2Event = readIfAvailable(spec.isDownedDD2EventAnyDifficultyAvailable) { reader.readBoolean() }
        val taxMoney = readIfAvailable(spec.isTaxMoneyAvailable) { reader.readS4() }

        val numberOfDeathsPVE = readIfAvailable(spec.isNumberOfDeathsAvailable) { reader.readS4() }
        val numberOfDeathsPVP = readIfAvailable(spec.isNumberOfDeathsAvailable) { reader.readS4() }

        val playerColors = PlayerColors(
            readColor(reader),
            readColor(reader),
            readColor(reader),
            readColor(reader),
            readColor(reader),
            readColor(reader),
            readColor(reader),
        )

        val armor = List(spec.armorSize) { readEquipmentItem(reader, spec.isArmorFavoritedAvailable) }
        val accessories = List(spec.accessoriesSize) { readEquipmentItem(reader, spec.isArmorFavoritedAvailable) }
        val vanityArmor = List(spec.armorSize) { readEquipmentItem(reader, spec.isArmorFavoritedAvailable) }
        val vanityAccessories = List(spec.accessoriesSize) { readEquipmentItem(reader, spec.isArmorFavoritedAvailable) }
        val dye = List(spec.dyeSize) { readEquipmentItem(reader, spec.isArmorFavoritedAvailable) }

        val inventory = List(spec.inventorySize) { readInventoryItem(reader, spec.isInventoryFavoritedAvailable) }

        val equipment = Equipment(
            armor = armor.mapIndexed { i, item ->
                EquipmentSlot(
                    item,
                    vanityArmor[i],
                    dye[i]
                )
            },
            accessories = accessories.mapIndexed { i, item -> EquipmentSlot(item, vanityAccessories[i], dye[i]) },
            misc = readMiscEquipments(reader, spec.miscEquipmentsFormat)
        )

        val piggyBank = List(spec.bankSize) { readBankItem(reader) }
        val safe = List(spec.bankSize) { readBankItem(reader) }
        val defendersForge = readIfAvailable(spec.isBank3Available) { List(spec.bankSize) { readBankItem(reader) } }
        val voidVault = readIfAvailable(spec.isBank4Available) {
            List(spec.bank4Size) { readInventoryItem(reader, spec.isBank4FavoriteAvailable) }
        }
        val voidVaultInfo = readIfAvailable(spec.isVoidVaultInfoAvailable) { reader.readU1() }

        val buffs = readIfAvailable(spec.isBuffAvailable) {
            List(spec.buffSize) { readBuff(reader) }.filter { it.id != 0 }
        }
        val sp = buildList {
            repeat(spec.spSize) {
                val sp = readSP(reader) ?: return@buildList
                add(sp)
            }
        }
        val hotbarLocked = readIfAvailable(spec.isHotbarLockedAvailable) { reader.readBoolean() }
        val hideInfo = readIfAvailable(spec.isHideInfoAvailable) { List(13) { reader.readBoolean() } }
        val anglerQuestsFinished = readIfAvailable(spec.isAnglerQuestsFinishedAvailable) { reader.readS4() }
        val dpadRadialBinding = readIfAvailable(spec.isDpadRadialBindingAvailable) { List(4) { reader.readS4() } }


        val builderAccStatus = List(spec.builderAccStatusSize) { reader.readS4() }
        val bartenderQuestLog = readIfAvailable(spec.isBartenderQuestLogAvailable) { reader.readS4() }

        val isPlayerDead = readIfAvailable(spec.isPlayerDeadAvailable) { reader.readBoolean() }
        val playerRespawnTimer = readIfAvailable(spec.isPlayerDeadAvailable) {
            if (isPlayerDead.getOrNull()!!) reader.readS4() else -1
        }

        val lastTimePlayerWasSaved = readIfAvailable(spec.isLastTimePlayerWasSavedAvailable) { reader.readS8() }
        val golferScoreAccumulated = readIfAvailable(spec.isGolferScoreAccumulatedAvailable) { reader.readS4() }
        val researchEntries = readIfAvailable(spec.isResearchAvailable) { readResearches(reader, spec) }

        return ParsedPlayer(
            metadata = metadata,
            name = name,
            difficulty = difficulty,
            playtimeTicks = playtime,
            hair = hair,
            hairDye = hairDye,
            team = team,
            hideAccessories = hideAccessories,
            hideMisc = hideMisc,
            skinVariant = skinVariant,
            life = life,
            maxLife = maxLife,
            mana = mana,
            maxMana = maxMana,
            hasExtraAccessorySlot = hasExtraAccessorySlot,
            unlockedBiomeTorches = unlockedBiomeTorches,
            usingBiomeTorches = usingBiomeTorches,
            ateArtisanBread = ateArtisanBread,
            shimmerUpgradesUsed = shimmerUpgradesUsed,
            downedDd2Event = downedDd2Event,
            taxMoney = taxMoney,
            numberOfDeathsPVE = numberOfDeathsPVE,
            numberOfDeathsPVP = numberOfDeathsPVP,
            colors = playerColors,
            equipment = equipment,
            inventory = inventory,
            piggyBank = piggyBank,
            safe = safe,
            defendersForge = defendersForge,
            voidVault = voidVault,
            voidVaultInfo = voidVaultInfo,
            buffs = buffs,
            sp = sp,
            hotbarLocked = hotbarLocked,
            hideInfo = hideInfo,
            anglerQuestsFinished = anglerQuestsFinished,
            dpadRadialBinding = dpadRadialBinding,
            builderAccStatus = builderAccStatus,
            bartenderQuestLog = bartenderQuestLog,
            isPlayerDead = isPlayerDead,
            playerRespawnTimer = playerRespawnTimer,
            lastTimePlayerWasSaved = lastTimePlayerWasSaved,
            golferScoreAccumulated = golferScoreAccumulated,
            researchEntries = researchEntries
        )
    }

    private fun <T> readIfAvailable(isAvailable: Boolean, read: () -> T): ParsedFieldValue<T> =
        if (isAvailable) ParsedFieldValue.Present(read()) else ParsedFieldValue.Absent

    private fun readResearches(reader: BinaryReader, spec: PLRFormatSpec): List<ResearchEntry> {
        fun fixItemInternalName(name: String) = when (name) {
            "EldMelter" -> "ElfMelter"
            "ThisIsCanonNow" -> "BrasilianSkies"
            "FoxparksTagEffect" -> "Deprecated6143"
            else -> name
        }
        if (spec.shouldSkipBeforeResearches) reader.skip(1)
        val researchedItemsCount = reader.readS4()

        val researchedEntries = mutableListOf<ResearchEntry>()
        repeat(researchedItemsCount) {
            val itemInternalName = fixItemInternalName(reader.readString())
            val itemAmount: Int = reader.readS4()
            researchedEntries.add(ResearchEntry(itemInternalName, itemAmount))
        }

        return researchedEntries
    }

    // TOOD: This shouldn't throw an error, create some format in PLRFormat to indicate an error
    private fun readMetadata(reader: BinaryReader, version: Int): FileInfo {
        val metadataHeader = reader.readU8()
        val signatureMask = 0xFFFFFFFFFFFFFFuL
        val metadataSignature = 27981915666277746uL
        val fileTypeShift = 56
        val fileTypeMask = 0xFFuL

        if ((metadataHeader and signatureMask) != metadataSignature)
            throw Error("${metadataHeader and signatureMask} is not valid file format")

        val typeByte = ((metadataHeader shr fileTypeShift) and fileTypeMask).toInt()
        val fileType = FileType.entries.find { it.type == typeByte }

        val isTypeValid = fileType != null
        val isTypePlayer = fileType?.type == FileType.Player.type

        if (!(isTypeValid && isTypePlayer))
            throw Error("$typeByte is not valid file player type")

        val revision = reader.readU4()
        val isFavorite = (reader.readU8() and 1uL) == 1uL

        return FileInfo(version, fileType, revision, isFavorite)
    }

    private fun readDifficulty(
        reader: BinaryReader,
        format: DifficultyFormat
    ): ParsedFieldValue<UByte> {
        return when (format) {
            DifficultyFormat.ABSENT ->
                ParsedFieldValue.Absent

            DifficultyFormat.LEGACY_BOOLEAN ->
                if (reader.readBoolean()) {
                    ParsedFieldValue.Present(2u)
                } else {
                    ParsedFieldValue.Absent
                }

            DifficultyFormat.BYTE ->
                ParsedFieldValue.Present(reader.readU1())
        }
    }

    fun readHideAccessories(reader: BinaryReader, format: HideAccessoriesFormat) = when (format) {
        HideAccessoriesFormat.ABSENT -> ParsedFieldValue.Absent
        HideAccessoriesFormat.EIGHT_BITS -> ParsedFieldValue.Present(reader.readBitsAndReset(8).map { it == 1 })
        HideAccessoriesFormat.TEN_BITS -> ParsedFieldValue.Present(reader.readBitsAndReset(10).map { it == 1 })
    }

    private fun readSP(reader: BinaryReader): SP? {
        val x = reader.readS4()
        if (x == -1) return null

        val y = reader.readS4()
        val i = reader.readS4()
        val n = reader.readString()

        return SP(x, y, i, n)
    }

    private fun readBuff(reader: BinaryReader): Buff {
        val id = reader.readS4()
        val time = reader.readS4()

        return Buff(id, time)
    }

    private fun readMiscEquipments(reader: BinaryReader, format: MiscEquipmentsFormat) = when (format) {
        MiscEquipmentsFormat.ABSENT -> emptyList()
        MiscEquipmentsFormat.IGNORE_INDEX_ONE -> List(5) {
            if (it == 1) MiscEquipmentSlot(RawItem(-1, 1.toUByte()), RawItem(-1, 1.toUByte()))
            else readMiscEquipmentSlot(reader)
        }

        MiscEquipmentsFormat.NORMAL -> List(5) { readMiscEquipmentSlot(reader) }
    }

    private fun readEquipmentItem(reader: BinaryReader, favoritedAvailable: Boolean): EquipmentItem<RawItem> {
        val id = reader.readS4()
        val prefix = reader.readU1()
        val favorited = favoritedAvailable && reader.readBoolean()

        return EquipmentItem(RawItem(id, prefix), favorited)
    }

    private fun readMiscEquipmentSlot(reader: BinaryReader): MiscEquipmentSlot<RawItem> {
        val id = reader.readS4()
        val prefix = reader.readU1()

        val dyeId = reader.readS4()
        val dyePrefix = reader.readU1()

        return MiscEquipmentSlot(
            RawItem(id, prefix),
            RawItem(dyeId, dyePrefix)
        )
    }

    private fun readInventoryItem(reader: BinaryReader, favoritedAvailable: Boolean): InventoryItem<RawItem> {
        val id = reader.readS4()
        val stack = reader.readS4()
        val prefix = reader.readU1()
        val favorited = favoritedAvailable && reader.readBoolean()

        return InventoryItem(RawItem(id, prefix), stack, favorited)
    }

    private fun readBankItem(reader: BinaryReader): BankItem<RawItem> {
        val id = reader.readS4()
        val stack = reader.readS4()
        val prefix = reader.readU1()

        return BankItem(RawItem(id, prefix), stack)
    }

    private fun readColor(reader: BinaryReader): Color {
        val r = reader.readU1().toInt()
        val g = reader.readU1().toInt()
        val b = reader.readU1().toInt()

        return Color(r, g, b)
    }
}