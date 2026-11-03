package self.adragon.terralyze.ui.hub.editor

import self.adragon.terralyze.ui.hub.storage.ResolvedEquipment
import self.adragon.terralyze.ui.hub.storage.ResolvedEquipmentSlot
import self.adragon.terralyze.ui.hub.storage.ResolvedItem
import self.adragon.terralyze.ui.hub.storage.ResolvedMiscEquipmentSlot

sealed interface SlotLocation {
    data class StorageLocation(
        val type: StorageType,
        val index: Int,
    ) : SlotLocation

    data class EquipmentLocation(
        val type: EquipmentType,
        val part: EquipmentPart,
        val index: Int,
    ) : SlotLocation
}

fun ResolvedEquipment.itemAt(location: SlotLocation): ResolvedItem? {
    if (location !is SlotLocation.EquipmentLocation) return null

    fun ResolvedEquipmentSlot.getItem() = when (location.part) {
        EquipmentPart.MAIN -> main
        EquipmentPart.VANITY -> vanity
        EquipmentPart.DYE -> dye
    }

    fun ResolvedMiscEquipmentSlot.getItem() = when (location.part) {
        EquipmentPart.MAIN -> main
        EquipmentPart.DYE -> dye
        EquipmentPart.VANITY -> null
    }

    return when (location.type) {
        EquipmentType.ARMOR -> armor[location.index].getItem()
        EquipmentType.ACCESSORIES -> accessories[location.index].getItem()
        EquipmentType.MISC -> misc[location.index].getItem()
    }
}

enum class StorageType {
    INVENTORY,
    PIGGY_BANK,
    SAFE,
    DEFENDERS_FORGE,
    VOID_VAULT,
}

enum class EquipmentType {
    ARMOR,
    ACCESSORIES,
    MISC,
}

enum class EquipmentPart {
    MAIN,
    VANITY,
    DYE,
}