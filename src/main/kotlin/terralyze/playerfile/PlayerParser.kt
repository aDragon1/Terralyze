package terralyze.playerfile

import terralyze.playerfile.model.Color
import terralyze.playerfile.model.FileType
import terralyze.playerfile.model.equipment.Equipment
import terralyze.playerfile.model.equipment.EquipmentSlot
import terralyze.playerfile.model.equipment.MiscEquipmentSlot
import terralyze.playerfile.model.item.BankItem
import terralyze.playerfile.model.item.EquipmentItem
import terralyze.playerfile.model.item.InventoryItem
import terralyze.playerfile.model.item.Item
import terralyze.playerfile.model.player.Buff
import terralyze.playerfile.model.player.ParsedPlayer
import terralyze.playerfile.model.player.PlayerColors
import terralyze.playerfile.model.player.PlayerMetadata
import terralyze.playerfile.model.player.SP
import terralyze.playerfile.model.player.ShimmerUpgrades
import terralyze.playerfile.binary.BinaryReader

/*
* Take and readapt source code of the https://terraria-research-tracker.free.nf
* Thank's, I suppose?
*/
class PlayerParser() {
    fun parse(content: ByteArray): ParsedPlayer {
        val reader = BinaryReader(content)
        println("Total bytes to read - ${reader.leftToRead()}")

        val version = reader.readS4()
        val metadata = readMetadata(reader)

        val name = reader.readString()
        val difficulty = reader.readU1()
        val playtime = reader.readS8()
        val hair = reader.readS4()
        val hairDye = reader.readU1()
        val team = reader.readU1()

        val hideAccessories = reader.readBitsAndReset(10).map { it == 1 }
        val hideMisc = reader.readU1()
        val skinVariant = reader.readU1()

        val life = reader.readS4()
        val maxLife = reader.readS4()

        val mana = reader.readS4()
        val maxMana = reader.readS4()

        val hasExtraAccessorySlot = reader.readBoolean()
        val unlockedBiomeTorches = reader.readBoolean()
        val usingBiomeTorches = reader.readBoolean()

        val ateArtisanBread = reader.readBoolean()
        reader.skip(1)
        val usedAegisCrystal = reader.readBoolean()
        val usedAegisFruit = reader.readBoolean()
        val usedArcaneCrystal = reader.readBoolean()
        val usedGalaxyPearl = reader.readBoolean()
        val usedGummyWorm = reader.readBoolean()
        val usedAmbrosia = reader.readBoolean()

        val shimmerUpgrades = ShimmerUpgrades(
            ateArtisanBread, usedAegisCrystal, usedAegisFruit, usedArcaneCrystal,
            usedGalaxyPearl, usedGummyWorm, usedAmbrosia
        )

        val downedDd2Event = reader.readBoolean()
        val taxMoney = reader.readS4()

        val numberOfDeathsPVE = reader.readS4()
        val numberOfDeathsPVP = reader.readS4()

        val playerColors = PlayerColors(
            readColor(reader),
            readColor(reader),
            readColor(reader),
            readColor(reader),
            readColor(reader),
            readColor(reader),
            readColor(reader),
        )

        val armor = List(3) { readEquipmentItem(reader) }
        val accessories = List(7) { readEquipmentItem(reader) }
        val vanityArmor = List(3) { readEquipmentItem(reader) }
        val vanityAccessories = List(7) { readEquipmentItem(reader) }
        val dye = List(10) { readEquipmentItem(reader) }

        val inventory = List(58) { readInventoryItem(reader) }

        val equipment = Equipment(
            armor = armor.mapIndexed { i, item -> EquipmentSlot(item, vanityArmor[i], dye[i]) },
            accessories = accessories.mapIndexed { i, item -> EquipmentSlot(item, vanityAccessories[i], dye[i]) },
            misc = List(5) { readMiscEquipmentSlot(reader) }
        )

        val piggyBank = List(40) { readBankItem(reader) }
        val safe = List(40) { readBankItem(reader) }
        val defendersForge = List(40) { readBankItem(reader) }
        val voidVault = List(40) { readInventoryItem(reader) }
        val voidVaultInfo = reader.readU1()

        val buffs = List(44) { readBuff(reader) }.filter { it.id != 0 }
        val sp = buildList {
            repeat(200) {
                val sp = readSP(reader) ?: return@buildList
                add(sp)
            }
        }
        val hotbarLocked = reader.readBoolean()
        val hideInfo = List(13) { reader.readBoolean() }
        val anglerQuestsFinished = reader.readS4()
        val dpadRadialBinding = List(4) { reader.readS4() }

        val builderAccStatus = List(12) { reader.readS4() }
        val bartenderQuestLog = reader.readS4()

        val isPlayerDead = reader.readBoolean()
        val playerRespawnTimer = if (isPlayerDead) reader.readS4() else -1

        val lastTimePlayerWasSaved = reader.readS8()

        val golferScoreAccumulated = reader.readS4()

        // researches journey mode goes below

        // TODO: Journey mode is waitin'
        reader.skip(1)
        val researchedItems = reader.readS4()
        println("researchedItems = $researchedItems")

        println("Left to read - ${reader.leftToRead()} bytes")

        return ParsedPlayer(
            version = version,
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
            shimmerUpgrades = shimmerUpgrades,
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
            researchedItems = researchedItems
        )
    }

    private fun readMetadata(reader: BinaryReader): PlayerMetadata {
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

        return PlayerMetadata(fileType, revision, isFavorite)
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

    private fun readEquipmentItem(reader: BinaryReader): EquipmentItem {
        val id = reader.readS4()
        val prefix = reader.readU1()
        val favorited = reader.readBoolean()

        return EquipmentItem(Item(id, prefix), favorited)
    }

    private fun readMiscEquipmentSlot(reader: BinaryReader): MiscEquipmentSlot {
        val id = reader.readS4()
        val prefix = reader.readU1()

        val dyeId = reader.readS4()
        val dyePrefix = reader.readU1()

        return MiscEquipmentSlot(Item(id, prefix), Item(dyeId, dyePrefix))
    }

    private fun readInventoryItem(reader: BinaryReader): InventoryItem {
        val id = reader.readS4()
        val stack = reader.readS4()
        val prefix = reader.readU1()
        val favorited = reader.readBoolean()

        return InventoryItem(Item(id, prefix), stack, favorited)
    }

    private fun readBankItem(reader: BinaryReader): BankItem {
        val id = reader.readS4()
        val stack = reader.readS4()
        val prefix = reader.readU1()

        return BankItem(Item(id, prefix), stack)
    }

    private fun readColor(reader: BinaryReader): Color {
        val r = reader.readU1().toInt()
        val g = reader.readU1().toInt()
        val b = reader.readU1().toInt()

        return Color(r, g, b)
    }
}