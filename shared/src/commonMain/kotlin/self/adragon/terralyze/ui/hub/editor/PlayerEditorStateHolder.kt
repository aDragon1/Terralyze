package self.adragon.terralyze.ui.hub.editor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import self.adragon.terralyze.data.model.equipment.Equipment
import self.adragon.terralyze.data.model.equipment.EquipmentSlot
import self.adragon.terralyze.data.model.item.Item
import self.adragon.terralyze.domain.model.Difficulty
import self.adragon.terralyze.domain.model.Player

class PlayerEditorStateHolder(
    val initialPlayer: Player,
    scope: CoroutineScope
    // savePlayerUseCase
) {
    private val _editedPlayer = MutableStateFlow(initialPlayer)
    val editedPlayer = _editedPlayer.asStateFlow()

    val hasUnsavedChanges = _editedPlayer.map { it != initialPlayer }
        .stateIn(scope, SharingStarted.WhileSubscribed(600), false)

    fun onIntent(intent: PlayerEditorIntent) = when (intent) {
        is PlayerEditorIntent.Stats -> onStatsIntent(intent)
        is PlayerEditorIntent.Progress -> onProgressIntent(intent)
        is PlayerEditorIntent.Storage -> onStorageIntent(intent)
        is PlayerEditorIntent.Research -> {}
        PlayerEditorIntent.ResetChanges -> {
            _editedPlayer.value = initialPlayer
        }

        PlayerEditorIntent.SavePlayer -> {}
    }

    private fun onStatsIntent(intent: PlayerEditorIntent.Stats) = when (intent) {
        is PlayerEditorIntent.Stats.UpdateName -> _editedPlayer.update { it.copy(name = intent.newName) }
        is PlayerEditorIntent.Stats.ChangeDifficulty -> _editedPlayer.update { it.copy(difficulty = intent.difficulty) }
        is PlayerEditorIntent.Stats.UpdateDeathsPVE -> _editedPlayer.update {
            it.copy(playerStats = it.playerStats.copy(deathsPVE = intent.deaths))
        }

        is PlayerEditorIntent.Stats.UpdateDeathsPVP -> _editedPlayer.update {
            it.copy(playerStats = it.playerStats.copy(deathsPVP = intent.deaths))
        }

        is PlayerEditorIntent.Stats.UpdateHealth -> _editedPlayer.update {
            if (intent.life > intent.maxLife) return@update it
            it.copy(playerStats = it.playerStats.copy(life = intent.life, maxLife = intent.maxLife))
        }

        is PlayerEditorIntent.Stats.UpdateMana -> _editedPlayer.update {
            if (intent.mana > intent.maxMana) return@update it
            it.copy(playerStats = it.playerStats.copy(mana = intent.mana, maxMana = intent.maxMana))
        }

        is PlayerEditorIntent.Stats.UpdatePlaytime -> {}
    }

    private fun onProgressIntent(intent: PlayerEditorIntent.Progress) = when (intent) {
        is PlayerEditorIntent.Progress.ToggleUpgrade -> _editedPlayer.update {
            it.copy(progress = it.progress.copy(permanentUpgrades = it.progress.permanentUpgrades.map { other ->
                other.copy(
                    isUsed = if (other.isUsed != null && other.name == intent.upgradeName)
                        !other.isUsed else other.isUsed
                )
            }))
        }

        is PlayerEditorIntent.Progress.UpdateAnglerQuests -> _editedPlayer.update {
            it.copy(progress = it.progress.copy(numberOfAnglerQuestsFinished = intent.count))
        }

        is PlayerEditorIntent.Progress.UpdateGolferScore -> _editedPlayer.update {
            it.copy(progress = it.progress.copy(golferScoreAccumulated = intent.score))
        }

        is PlayerEditorIntent.Progress.ToggleDownedDd2Event -> _editedPlayer.update {
            it.copy(
                progress = it.progress.copy(
                    downedDd2Event = if (it.progress.downedDd2Event != null) !it.progress.downedDd2Event
                    else null
                )
            )
        }

        is PlayerEditorIntent.Progress.UpdateBartenderQuestLog -> _editedPlayer.update {
            it.copy(
                progress = it.progress.copy(
                    bartenderQuestLog = if (it.progress.bartenderQuestLog != null) intent.value
                    else null
                )
            )
        }
    }

    private fun onStorageIntent(intent: PlayerEditorIntent.Storage) = when (intent) {
        is PlayerEditorIntent.Storage.UpdateSlot -> {
            _editedPlayer.update { player ->
                updateSlot(player, intent.item, intent.location)
            }
        }
    }

    private fun updateSlot(player: Player, updatedItem: Item, location: SlotLocation): Player {
        fun <T> List<T>.replaceAt(value: T, index: Int) = if (index in indices)
            mapIndexed { i, v -> if (i == index) value else v } else this

        return when (location) {
            is SlotLocation.StorageLocation -> {
                when (location.type) {
                    StorageType.INVENTORY -> player.copy(
                        playerStorage = player.playerStorage.copy(
                            inventory = player.playerStorage.inventory
                                .replaceAt(updatedItem, location.index)
                        )
                    )


                    StorageType.PIGGY_BANK -> player.copy(
                        playerStorage = player.playerStorage.copy(
                            piggyBank = player.playerStorage.piggyBank
                                .replaceAt(updatedItem, location.index)
                        )
                    )


                    StorageType.SAFE -> player.copy(
                        playerStorage = player.playerStorage.copy(
                            safe = player.playerStorage.safe
                                .replaceAt(updatedItem, location.index)
                        )
                    )


                    StorageType.DEFENDERS_FORGE -> {
                        val forge = player.playerStorage.defendersForge
                            ?: return player

                        player.copy(
                            playerStorage = player.playerStorage.copy(
                                defendersForge = forge.replaceAt(updatedItem, location.index)
                            )
                        )
                    }

                    StorageType.VOID_VAULT -> {
                        val vault = player.playerStorage.voidVault
                            ?: return player

                        player.copy(
                            playerStorage = player.playerStorage.copy(
                                voidVault = vault.replaceAt(updatedItem, location.index)
                            )
                        )
                    }
                }
            }

            is SlotLocation.EquipmentLocation -> player.copy(
                equipment = player.equipment
                    .updateSlot(location.type, location.part, updatedItem, location.index)
            )
        }
    }

    fun Equipment.updateSlot(
        type: EquipmentType, part: EquipmentPart, item: Item, index: Int,
    ): Equipment {
        fun <T> List<T>.updateAt(index: Int, transform: (T) -> T) =
            if (index in indices) mapIndexed { i, v -> if (i == index) transform(v) else v } else this

        fun EquipmentSlot.updatePart(part: EquipmentPart, item: Item) = when (part) {
            EquipmentPart.MAIN -> copy(main = item)
            EquipmentPart.VANITY -> copy(vanity = item)
            EquipmentPart.DYE -> copy(dye = item)
        }

        return when (type) {
            EquipmentType.ARMOR -> copy(armor = armor.updateAt(index) { it.updatePart(part, item) })
            EquipmentType.ACCESSORIES ->
                copy(accessories = accessories.updateAt(index) { it.updatePart(part, item) })

            EquipmentType.MISC -> copy(
                misc = misc.updateAt(index) { slot ->
                    when (part) {
                        EquipmentPart.MAIN -> slot.copy(main = item)
                        EquipmentPart.DYE -> slot.copy(dye = item)
                        EquipmentPart.VANITY -> error("Misc equipment does not have a vanity slot")
                    }
                }
            )
        }
    }
}

sealed interface PlayerEditorIntent {
    sealed interface Stats : PlayerEditorIntent {
        data class UpdateHealth(val life: Int, val maxLife: Int) : Stats
        data class UpdateMana(val mana: Int, val maxMana: Int) : Stats
        data class UpdateDeathsPVE(val deaths: Int) : Stats
        data class UpdateDeathsPVP(val deaths: Int) : Stats
        data class ChangeDifficulty(val difficulty: Difficulty) : Stats
        data class UpdatePlaytime(val ticks: Long) : Stats
        data class UpdateName(val newName: String) : Stats
    }

    sealed interface Progress : PlayerEditorIntent {
        data class ToggleUpgrade(val upgradeName: String) : Progress
        data class UpdateAnglerQuests(val count: Int) : Progress
        data class UpdateGolferScore(val score: Int) : Progress
        data class UpdateBartenderQuestLog(val value: Int) : Progress
        data object ToggleDownedDd2Event : Progress
    }

    sealed interface Storage : PlayerEditorIntent {
        data class UpdateSlot(val item: Item, val location: SlotLocation) : Storage
    }

    sealed interface Research : PlayerEditorIntent {
        data class SetAmount(val internalName: String, val amount: Int) : Research
        data object ResearchAll : Research
    }

    data object ResetChanges : PlayerEditorIntent
    data object SavePlayer : PlayerEditorIntent
}

