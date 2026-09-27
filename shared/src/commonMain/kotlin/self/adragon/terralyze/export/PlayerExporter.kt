package self.adragon.terralyze.export

import self.adragon.terralyze.data.model.player.ParsedPlayer

interface PlayerExporter {
    val fileExtension: String

    fun exportRawPlayer(parsedPlayer: ParsedPlayer): String
}