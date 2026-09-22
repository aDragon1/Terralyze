package terralyze.playerfile

import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

private const val ENCRYPTION_KEY = "h3y_gUyZ"

class PlrDecryptor {
    fun decrypt(bytes: ByteArray): ByteArray {
        val key = ENCRYPTION_KEY.toByteArray(Charsets.UTF_16LE)

        val secretKey = SecretKeySpec(key, "AES")
        val iv = IvParameterSpec(key)

        val cipher = Cipher.getInstance("AES/CBC/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv)

        return cipher.doFinal(bytes) ?: byteArrayOf()
    }
}