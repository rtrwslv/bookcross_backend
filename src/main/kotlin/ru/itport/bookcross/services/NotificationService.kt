package ru.itport.bookcross.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import ru.itport.bookcross.models.auth.CheckSubscribeBody
import ru.itport.bookcross.models.auth.CheckSubscribeRs

interface NotificationService {
    fun userInChat(chatId: String?, userId: String): Boolean
    fun sendMessage(chatId: String, text: String)
}

@Service
class NotificationServiceImpl(
    @Value("\${security.bot.token}") private val token: String,
    private val restTemplate: RestTemplate = RestTemplate()
) : NotificationService {

    override fun userInChat(chatId: String?, userId: String): Boolean {
        val body = CheckSubscribeBody(
            chat_id = chatId?.toLong() ?: 0,
            user_id = userId.toLong()
        )

        val request: HttpEntity<CheckSubscribeBody> = HttpEntity(body)
        val uri = "https://api.telegram.org/bot$token/getChatMember"
        val response: CheckSubscribeRs? =
            kotlin.runCatching {
                restTemplate.postForObject(
                    uri, request, CheckSubscribeRs::class.java
                )
            }.getOrElse {
                return false
            }

        return response!!.ok && response.result.status != "left"
    }


    override fun sendMessage(chatId: String, text: String) {
        if (chatId.isBlank()) {
            println("Ошибка: chatId пустой. Сообщение не отправлено.")
            return
        }

        val uri = "https://api.telegram.org/bot$token/sendMessage"

        val requestBody = mapOf(
            "chat_id" to chatId,
            "text" to text
        )

        kotlin.runCatching {
            restTemplate.postForObject(uri, requestBody, String::class.java)
        }.onFailure {
            println("Ошибка при отправке сообщения пользователю с chat_id=$chatId: ${it.message}")
            it.printStackTrace()
        }
    }
}