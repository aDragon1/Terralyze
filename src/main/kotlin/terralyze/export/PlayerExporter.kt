package terralyze.export

import terralyze.playerfile.model.player.ParsedPlayer

interface PlayerExporter {
    val fileExtension: String

    fun export(parsedPlayer: ParsedPlayer): String
}