import model.BankItem
import model.Equipment
import self.adragon.model.FileType
import model.EquipmentItem
import model.EquipmentSlot
import model.ShimmerUpgrades
import model.InventoryItem
import model.Item
import model.MiscEquipmentSlot
import model.PlayerColors
import model.PlayerMetadata
import self.adragon.BinaryReader
import java.awt.Color
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/*
* Take and readapt source code of the https://terraria-research-tracker.free.nf
* Thank's, I suppose?
*/
class PlayerParser(val content: ByteArray) {
    fun parse() {
        val reader = BinaryReader(content)

        val version = reader.readS4()
        val metadata = readMetadata(reader)

        val name = reader.readString()
        val difficulty = reader.readU1()
        val playtime = reader.readS8()
        val playtimeDuration: Duration = (playtime * 100).toDuration(DurationUnit.NANOSECONDS)
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

        val numberOfDeathPVE = reader.readS4()
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
        val voidVault = List(40) { readBankItem(reader) }
        val voidVaultInfo = reader.readB1()

        if (true) {
            println("version = $version")
            println("metadata = $metadata")
            println("name = $name")
            println("difficulty = $difficulty")
            println("playtime = $playtime")
            println("hair = $hair")
            println("hairDye = $hairDye")
            println("team = $team")
            println("hideAccessories = $hideAccessories")
            println("hideMisc = $hideMisc")
            println("skinVariant = $skinVariant")
            println("life = $life")
            println("maxLife = $maxLife")
            println("mana = $mana")
            println("maxMana = $maxMana")
            println("hasExtraAccessorySlot = $hasExtraAccessorySlot")
            println("unlockedBiomeTorches = $unlockedBiomeTorches")
            println("usingBiomeTorches = $usingBiomeTorches")
            println("shimmerUpgrades = $shimmerUpgrades")
            println("downedDd2Event = $downedDd2Event")
            println("taxMoney = $taxMoney")
            println("numberOfDeathPVE = $numberOfDeathPVE")
            println("numberOfDeathsPVP = $numberOfDeathsPVP")
            println("playerColors = $playerColors")
            println("equipment = $equipment")
            println("inventory = $inventory")
        }
    }

    fun readMetadata(reader: BinaryReader): PlayerMetadata {
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

    fun readEquipmentItem(reader: BinaryReader): EquipmentItem {
        val id = reader.readS4()
        val prefix = reader.readU1()
        val favorited = reader.readBoolean()

        return EquipmentItem(Item(id, prefix), favorited)
    }

    fun readMiscEquipmentSlot(reader: BinaryReader): MiscEquipmentSlot {
        val id = reader.readS4()
        val prefix = reader.readU1()

        val dyeId = reader.readS4()
        val dyePrefix = reader.readU1()

        return MiscEquipmentSlot(Item(id, prefix), Item(dyeId, dyePrefix))
    }

    fun readInventoryItem(reader: BinaryReader): InventoryItem {
        val id = reader.readS4()
        val stack = reader.readS4()
        val prefix = reader.readU1()
        val favorited = reader.readBoolean()

        return InventoryItem(Item(id, prefix), stack, favorited)
    }

    fun readBankItem(reader: BinaryReader): BankItem {
        val id = reader.readS4()
        val stack = reader.readS4()
        val prefix = reader.readU1()

        return BankItem(Item(id, prefix), stack)
    }

    fun readColor(reader: BinaryReader): Color {
        val r = reader.readU1().toInt()
        val g = reader.readU1().toInt()
        val b = reader.readU1().toInt()

        return Color(r, g, b)
    }
}