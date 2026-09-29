package self.adragon.terralyze.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import self.adragon.terralyze.data.repository.ItemCatalog
import self.adragon.terralyze.data.source.crypto.PlrDecryptor
import self.adragon.terralyze.data.source.parser.PlayerParser
import self.adragon.terralyze.domain.mapper.PlayerMapper
import self.adragon.terralyze.domain.model.Player


// TODO: Сделать проверку на .endsWith(".plr")
class LoadPlayerUseCase(
    private val decryptor: PlrDecryptor,
    private val parser: PlayerParser,
    private val mapper: PlayerMapper,
    private val catalog: ItemCatalog,
) {
    suspend operator fun invoke(bytes: ByteArray): Player =
        withContext(Dispatchers.Default) {
            val decrypted = decryptor.decrypt(bytes)
            val parsed = parser.parse(decrypted)
            mapper.parsedPlayerToDomain(catalog, parsed)
        }
}