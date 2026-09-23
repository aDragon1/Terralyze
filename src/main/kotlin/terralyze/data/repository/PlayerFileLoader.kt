package terralyze.data.repository

import terralyze.data.model.player.ParsedPlayer
import terralyze.data.source.parser.PlayerParser
import terralyze.data.source.crypto.JvmPlrDecryptor
import terralyze.data.source.crypto.PlrDecryptor

class PlayerFileLoader(
    private val decryptor: PlrDecryptor = JvmPlrDecryptor(),
    private val parser: PlayerParser = PlayerParser()
) {
    fun load(bytes: ByteArray): ParsedPlayer {
        val decrypted = decryptor.decrypt(bytes)
        return parser.parse(decrypted)
    }
}