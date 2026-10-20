package self.adragon.terralyze.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import self.adragon.terralyze.data.repository.ItemCatalog
import self.adragon.terralyze.data.source.crypto.PlrDecryptor
import self.adragon.terralyze.data.source.parser.PlayerParser
import self.adragon.terralyze.domain.mapper.PlayerMapper
import self.adragon.terralyze.domain.model.Player

class LoadPlayerUseCase(
    val decryptor: PlrDecryptor, val parser: PlayerParser,
    val mapper: PlayerMapper, val catalog: ItemCatalog,
) {
    suspend operator fun invoke(bytes: ByteArray): Player =
        withContext(Dispatchers.Default) {
            val decrypted = decryptor.decrypt(bytes)
            val parsed = parser.parse(decrypted)
            mapper.parsedPlayerToDomain(catalog, parsed)
        }
}