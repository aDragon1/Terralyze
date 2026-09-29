package self.adragon.terralyze.export

import self.adragon.terralyze.data.model.player.ParsedPlayer
import self.adragon.terralyze.domain.model.Player

interface PlayerExporter {
    val fileExtension: String

    fun export(player: Player): String
    fun exportRawPlayer(parsedPlayer: ParsedPlayer): String
}