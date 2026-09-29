package self.adragon.terralyze.ui.initScreen

import self.adragon.terralyze.domain.model.Player
import self.adragon.terralyze.domain.usecase.LoadPlayerUseCase

sealed interface InitState {
    data object Loading : InitState
    data class Ready(val loadPlayer: LoadPlayerUseCase) : InitState
    data class Error(val message: String) : InitState
}