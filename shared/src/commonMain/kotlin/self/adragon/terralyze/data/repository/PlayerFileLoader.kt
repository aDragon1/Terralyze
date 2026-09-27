package self.adragon.terralyze.data.repository

import self.adragon.terralyze.data.model.player.ParsedPlayer
import self.adragon.terralyze.data.source.crypto.JvmPlrDecryptor
import self.adragon.terralyze.data.source.crypto.PlrDecryptor
import self.adragon.terralyze.data.source.parser.PlayerParser

class PlayerFileLoader(
    private val decryptor: PlrDecryptor = JvmPlrDecryptor(),
    private val parser: PlayerParser = PlayerParser()
) {
    suspend fun load(bytes: ByteArray): ParsedPlayer {
        val decrypted = decryptor.decrypt(bytes)
        return parser.parse(decrypted)
    }
}