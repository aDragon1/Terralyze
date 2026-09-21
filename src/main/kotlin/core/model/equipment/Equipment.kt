package core.model.equipment

import kotlinx.serialization.Serializable

@Serializable
data class Equipment(
    val armor: List<EquipmentSlot>,
    val accessories: List<EquipmentSlot>,
    val misc: List<MiscEquipmentSlot>,
) {
    override fun toString(): String {
        val sb = StringBuilder()

        appendEquipmentSlots(sb, "Armor", armor)
        appendEquipmentSlots(sb, "Accessories", accessories)
        appendMiscSlots(sb, "Tools", misc)

        return sb.toString()
    }

    private fun appendEquipmentSlots(
        sb: StringBuilder,
        name: String,
        slots: List<EquipmentSlot>
    ) {
        sb.appendLine("\n$name [")

        slots.forEachIndexed { index, slot ->
            sb.appendLine("  [$index]")
            sb.appendLine("    main   = ${slot.main}")
            sb.appendLine("    vanity = ${slot.vanity}")
            sb.appendLine("    dye    = ${slot.dye}")
        }

        sb.appendLine("]")
    }

    private fun appendMiscSlots(
        sb: StringBuilder,
        name: String,
        slots: List<MiscEquipmentSlot>
    ) {
        sb.appendLine("\n$name [")

        slots.forEachIndexed { index, slot ->
            sb.appendLine("  [$index]")
            sb.appendLine("    main = ${slot.main}")
            sb.appendLine("    dye  = ${slot.dye}")
        }

        sb.appendLine("]")
    }
}