package self.adragon.terralyze.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Routes {

    @Serializable
    data object PlayerLoad : Routes

    @Serializable
    data object PlayerOverview : Routes
}