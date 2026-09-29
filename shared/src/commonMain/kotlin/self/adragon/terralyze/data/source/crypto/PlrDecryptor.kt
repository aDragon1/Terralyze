package self.adragon.terralyze.data.source.crypto

import dev.whyoleg.cryptography.CryptographyProvider
import dev.whyoleg.cryptography.DelicateCryptographyApi
import dev.whyoleg.cryptography.algorithms.AES

@OptIn(DelicateCryptographyApi::class)
class PlrDecryptor {
    suspend fun decrypt(bytes: ByteArray): ByteArray {
        val provider = CryptographyProvider.Default
        val aes = provider.get(AES.CBC)
        val encryptionKeyBytes = PlrCrypto.encryptionKeyBytes()
        val key = aes.keyDecoder().decodeFromByteArray(AES.Key.Format.RAW, encryptionKeyBytes)

        return key.cipher().decryptWithIv(encryptionKeyBytes, bytes)
    }
}