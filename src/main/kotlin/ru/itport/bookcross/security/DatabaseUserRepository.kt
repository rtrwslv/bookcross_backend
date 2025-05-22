package ru.itport.bookcross.security

import ru.itport.bookcross.entity.User
import io.jmix.securitydata.user.AbstractDatabaseUserRepository
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Primary
@Component("UserRepository")
class DatabaseUserRepository : AbstractDatabaseUserRepository<User>() {

    override fun getUserClass(): Class<User> = User::class.java

    override fun initSystemUser(systemUser: User) {
        val authorities = grantedAuthoritiesBuilder
            .addResourceRole(FullAccessRole.CODE)
            .build()
        systemUser.authorities = authorities
    }

    fun initDefaultUserAuthorities() = grantedAuthoritiesBuilder
        .addResourceRole(UserAccessRole.CODE)
        .build()


    override fun initAnonymousUser(anonymousUser: User) {
        val authorities = grantedAuthoritiesBuilder
            .addResourceRole(AnonymousAccessRole.CODE)
            .build()
        anonymousUser.authorities = authorities
    }
}