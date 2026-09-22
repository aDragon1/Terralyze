package terralyze.application

import terralyze.playerfile.PlrDecryptor
import terralyze.playerfile.model.player.ParsedPlayer
import terralyze.playerfile.PlayerParser
import java.io.File

class PlayerFileLoader(
    private val decryptor: PlrDecryptor = PlrDecryptor(),
    private val parser: PlayerParser = PlayerParser()
) {

    fun load(file: File): ParsedPlayer {
        val bytes = file.readBytes()
        val decrypted = decryptor.decrypt(bytes)
        return parser.parse(decrypted)
    }
}