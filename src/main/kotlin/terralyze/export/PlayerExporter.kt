package terralyze.export

import terralyze.data.model.player.ParsedPlayer

interface PlayerExporter {
    val fileExtension: String

    fun export(parsedPlayer: ParsedPlayer): String
}