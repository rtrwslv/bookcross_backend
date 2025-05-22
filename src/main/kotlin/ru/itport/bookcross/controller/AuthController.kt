package ru.itport.bookcross.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import ru.itport.bookcross.models.BaseResponse
import ru.itport.benifits.models.auth.JwtToken
import ru.itport.bookcross.models.auth.TelegramAuthRq
import ru.itport.bookcross.services.JwtService
import ru.itport.bookcross.utils.TelegramVerifier
import ru.itport.bookcross.services.UserService

@RestController
@CrossOrigin
@RequestMapping("custom/api/v1/auth")
class AuthController (
    private val jwtService: JwtService,
    private val userService: UserService,
    @Value("\${security.bot.token}") val botToken: String,
) {

    @PostMapping("/login")
    fun login(@RequestBody telegramAuthRq: TelegramAuthRq): BaseResponse<JwtToken> {
        val telegramVerifier = TelegramVerifier()
        if (!telegramVerifier.checkTelegramData(telegramAuthRq, botToken)) {
            return BaseResponse.fail(BaseResponse.Error(code = "403", message = "Forbidden"))
        }

        var user =  userService.loadByTelegramId(telegramAuthRq.telegramId)
        if (user == null) {
            val groupId = userService.checkMembership(telegramAuthRq.telegramId)
            if (groupId != null)
                user = userService.createUser(telegramAuthRq, groupId)
            else
                return BaseResponse.fail(BaseResponse.Error(code = "403", message = "Forbidden"))
        }

        val jwtToken = jwtService.generateToken(user.username!!)
        val rs = JwtToken(
            token = jwtToken.first,
            expiration = jwtToken.second,
            cityId = user.getCityId(),
            userId = user.id
        )
        return BaseResponse.ok(rs)
    }
}
