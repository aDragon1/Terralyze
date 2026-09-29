package self.adragon.terralyze.ui.playerLoadScreen


sealed interface PlayerLoadState {
    data object Empty : PlayerLoadState
    data object Loading : PlayerLoadState
    data class Error(val message: String) : PlayerLoadState
}