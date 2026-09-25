package terralyze.export

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import terralyze.data.model.ParsedFieldValue
import terralyze.data.model.player.ParsedPlayer

class PlayerJsonExporter(private val json: Json = Json { prettyPrint = true }) : PlayerExporter {

    override val fileExtension: String = "json"
    override fun export(parsedPlayer: ParsedPlayer): String = json.encodeToString(toJson(parsedPlayer))

    private fun toJson(parsedPlayer: ParsedPlayer) = buildJsonObject {
        putValue("metadata") {
            putValue("version", parsedPlayer.metadata.version)
            putValue("type", parsedPlayer.metadata.type.name)
            putValue("revision", parsedPlayer.metadata.revision)
            putValue("isFavorite", parsedPlayer.metadata.isFavorite)
        }
        putValue("name", parsedPlayer.name)
        putValue("difficulty", parsedPlayer.difficulty)
        putValue("playtime") {
            putValue("time", parsedPlayer.playtimeTicks)
//            putValue("duration", parsedPlayer.playtimeDuration.toString())
        }
        putValue("hair", parsedPlayer.hair)
        putValue("hairDye", parsedPlayer.hairDye)
        putValue("team", parsedPlayer.team)
        putValue("hideAccessories", parsedPlayer.hideAccessories)
        putValue("hideMisc", parsedPlayer.hideMisc)
        putValue("skinVariant", parsedPlayer.skinVariant)
        putValue("life", parsedPlayer.life)
        putValue("maxLife", parsedPlayer.maxLife)
        putValue("mana", parsedPlayer.mana)
        putValue("maxMana", parsedPlayer.maxMana)
        putValue("hasExtraAccessorySlot", parsedPlayer.hasExtraAccessorySlot)
        putValue("unlockedBiomeTorches", parsedPlayer.unlockedBiomeTorches)
        putValue("usingBiomeTorches", parsedPlayer.usingBiomeTorches)
        putValue("shimmerUpgrades", parsedPlayer.shimmerUpgradesUsed)
        putValue("downedDd2Event", parsedPlayer.downedDd2Event)
        putValue("taxMoney", parsedPlayer.taxMoney)
        putValue("numberOfDeathPVE", parsedPlayer.numberOfDeathsPVE)
        putValue("numberOfDeathsPVP", parsedPlayer.numberOfDeathsPVP)
        putValue("playerColors", parsedPlayer.colors)
        putValue("equipment", parsedPlayer.equipment)
        putValue("inventory", parsedPlayer.inventory)
        putValue("piggyBank", parsedPlayer.piggyBank)
        putValue("safe", parsedPlayer.safe)
        putValue("defendersForge", parsedPlayer.defendersForge)
        putValue("voidVault", parsedPlayer.voidVault)
        putValue("voidVaultInfo", parsedPlayer.voidVaultInfo)
        putValue("buffs", parsedPlayer.buffs)
        putValue("sp", parsedPlayer.sp)
        putValue("hotbarLocked", parsedPlayer.hotbarLocked)
        putValue("hideInfo", parsedPlayer.hideInfo)
        putValue("anglerQuestsFinished", parsedPlayer.anglerQuestsFinished)
        putValue("DpadRadialBinding", parsedPlayer.dpadRadialBinding)
        putValue("builderAccStatus", parsedPlayer.builderAccStatus)
        putValue("bartenderQuestLog", parsedPlayer.bartenderQuestLog)
        putValue("isPlayerDead", parsedPlayer.isPlayerDead)
        putValue("playerRespawnTimer", parsedPlayer.playerRespawnTimer)
        putValue("lastTimePlayerWasSaved", parsedPlayer.lastTimePlayerWasSaved)
        putValue("golferScoreAccumulated", parsedPlayer.golferScoreAccumulated)
        putValue("researchEntries", parsedPlayer.researchEntries)
    }

    private inline fun <reified T> JsonObjectBuilder.putValue(key: String, value: ParsedFieldValue<T>) {
        if (value is ParsedFieldValue.Present)
            put(key, Json.encodeToJsonElement(value))
    }


    private inline fun <reified T> JsonObjectBuilder.putValue(key: String, value: T) {
        put(key, Json.encodeToJsonElement(value))
    }

    private inline fun JsonObjectBuilder.putValue(key: String, block: JsonObjectBuilder.() -> Unit) {
        put(key, buildJsonObject(block))
    }
}