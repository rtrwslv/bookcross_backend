package ru.itport.bookcross.controller

import io.jmix.core.security.CurrentAuthentication
import org.springframework.web.bind.annotation.*
import ru.itport.bookcross.models.BaseResponse
import ru.itport.bookcross.models.user.User
import ru.itport.bookcross.models.user.UserAboutRs

@RestController
@CrossOrigin
@RequestMapping("custom/api/v1/user")
class UserController(
    private val currentAuthentication: CurrentAuthentication,
) {
    @PostMapping("/about-me")
    fun aboutMe(): BaseResponse<UserAboutRs> {
        return BaseResponse.ok(
            UserAboutRs(
                user = User(
                    userName = currentAuthentication.user.username
                )
            )
        )
    }

}