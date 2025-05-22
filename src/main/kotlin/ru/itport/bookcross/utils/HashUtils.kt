package ru.itport.bookcross.utils

import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HashUtils {

    fun String.sha256b(): ByteArray {
        return hashString(this, "SHA-256", bytesOnly=true)
    }

    private fun hashString(input: String, algorithm: String, bytesOnly: Boolean = false): ByteArray {
        return MessageDigest
            .getInstance(algorithm)
            .digest(input.toByteArray())
    }

    fun hex(bytes: ByteArray): String  {
        val DIGITS = "0123456789abcdef".toCharArray()
        return buildString(bytes.size * 2) {
            bytes.forEach { byte ->
                val b = byte.toInt() and 0xFF
                append(DIGITS[b shr 4])
                append(DIGITS[b and 0x0F])
            }
        }
    }

    fun hmac_sha256(data: String, key: ByteArray): ByteArray {
        val hmacSha256: Mac = Mac.getInstance("HmacSHA256")
        hmacSha256.init(SecretKeySpec(key, "HmacSHA256"))
        return hmacSha256.doFinal(data.toByteArray())
    }

}