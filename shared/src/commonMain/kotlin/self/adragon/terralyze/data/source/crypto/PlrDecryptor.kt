package self.adragon.terralyze.data.source.crypto

interface PlrDecryptor {
    suspend fun decrypt(bytes: ByteArray): ByteArray
}