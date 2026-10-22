package self.adragon.terralyze.ui.hub

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import self.adragon.terralyze.domain.model.Difficulty
import self.adragon.terralyze.domain.model.ItemSlot
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
        is PlayerEditorIntent.Storage -> {}
        is PlayerEditorIntent.Research -> {}
        PlayerEditorIntent.ResetChanges -> {}
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

        is PlayerEditorIntent.Stats.UpdateHealth -> {}
        is PlayerEditorIntent.Stats.UpdateMana -> {}
        is PlayerEditorIntent.Stats.UpdatePlaytime -> {}
    }

    private fun onProgressIntent(intent: PlayerEditorIntent.Progress) = when (intent) {
        is PlayerEditorIntent.Progress.ToggleUpgrade -> _editedPlayer.update {
            it.copy(progress = it.progress.copy(permanentUpgrades = it.progress.permanentUpgrades.map { other ->
                other.copy(isUsed = if (other.name == intent.upgradeName) !intent.isUsed else other.isUsed)
            }))
        }

        is PlayerEditorIntent.Progress.UpdateAnglerQuests -> _editedPlayer.update {
            it.copy(progress = it.progress.copy(numberOfAnglerQuestsFinished = intent.count))
        }

        is PlayerEditorIntent.Progress.UpdateGolferScore -> _editedPlayer.update {
            it.copy(progress = it.progress.copy(golferScoreAccumulated = intent.score))
        }

        is PlayerEditorIntent.Progress.UpdateDownedDd2Event -> _editedPlayer.update {
            it.copy(progress = it.progress.copy(downedDd2Event = intent.isDowned))
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
        data class ToggleUpgrade(val upgradeName: String, val isUsed: Boolean) : Progress
        data class UpdateAnglerQuests(val count: Int) : Progress
        data class UpdateGolferScore(val score: Int) : Progress
        data class UpdateDownedDd2Event(val isDowned: Boolean) : Progress
    }

    sealed interface Storage : PlayerEditorIntent {
        data class UpdateInventorySlot(val index: Int, val item: ItemSlot) : Storage
//        data class UpdateEquipmentSlot(val slotType: EquipmentType, val index: Int, val item: ItemSlot) : Storage
    }

    sealed interface Research : PlayerEditorIntent {
        data class SetAmount(val internalName: String, val amount: Int) : Research
        data object ResearchAll : Research
    }

    data object ResetChanges : PlayerEditorIntent
    data object SavePlayer : PlayerEditorIntent
}