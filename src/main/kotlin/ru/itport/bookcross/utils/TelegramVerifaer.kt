package ru.itport.bookcross.utils


import ru.itport.bookcross.utils.HashUtils.sha256b
import ru.itport.bookcross.models.auth.TelegramAuthRq
import ru.itport.bookcross.models.auth.forceSerialize

class TelegramVerifier {

    fun checkTelegramData(payload: TelegramAuthRq, botToken: String): Boolean {
        val serializedString = payload.forceSerialize("\n", sorted = true)
            .split("\n").filter { !it.startsWith("hash") && !it.contains("null") }.joinToString("\n")
        return HashUtils.hex(HashUtils.hmac_sha256(serializedString, botToken.sha256b())) == payload.hash
    }

}