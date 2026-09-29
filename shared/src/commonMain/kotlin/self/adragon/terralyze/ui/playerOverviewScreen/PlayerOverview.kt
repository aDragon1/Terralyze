package self.adragon.terralyze.ui.playerOverviewScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import self.adragon.terralyze.domain.model.Player

@Composable
fun PlayerOverview(player: Player) {
    PlayerInfo(player)
}

@Composable
private fun PlayerInfo(player: Player, modifier: Modifier = Modifier) {
    val stats = player.playerStats
    Column {
        Text("Имя: ${player.name}")
        Text("HP:   ${stats.life} /  ${stats.maxLife}")
        Text("Мана: ${stats.mana} /  ${stats.maxMana}")
    }
}