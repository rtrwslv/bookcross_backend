package ru.itport.bookcross.services

import io.jmix.core.DataManager
import io.jmix.core.querycondition.PropertyCondition
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.itport.bookcross.entity.TelegramChat
import ru.itport.bookcross.entity.User
import ru.itport.bookcross.entity.UserProfile
import ru.itport.bookcross.models.auth.TelegramAuthRq
import ru.itport.bookcross.security.DatabaseUserRepository

@Service
class UserService @Autowired constructor(
    private val dataManager: DataManager,
    private val notificationService: NotificationService,
    private val databaseUserRepository: DatabaseUserRepository
) {


    fun createUser(telegramAuthRq: TelegramAuthRq, groupId: String): User {

        val userProfile = dataManager.create(UserProfile::class.java)
        userProfile.telegramId = telegramAuthRq.telegramId

        userProfile.city = dataManager.load(TelegramChat::class.java).condition(
            PropertyCondition.equal("chatID",groupId))
            .one()
            .location

        val user = dataManager.create(User::class.java)
        user.username = telegramAuthRq.username
        user.firstName = telegramAuthRq.firstName
        user.lastName = telegramAuthRq.lastName

        val createdUser = dataManager.save(user)
        userProfile.user = createdUser
        dataManager.save(userProfile)

        return createdUser
    }

    fun checkMembership(telegramId: String): String? {
        return dataManager.load(TelegramChat::class.java)
            .all()
            .list()
            .firstOrNull{ notificationService.userInChat(it.chatID, telegramId) }?.chatID
    }

    fun loadUserByUsername(username: String): User? {
        return dataManager.load(User::class.java)
            .query("select u from User u where u.username = :username")
            .parameter("username", username)
            .optional()
            .orElse(null)
    }

    fun loadByTelegramId(telegramId: String): User? {
        return dataManager.load(User::class.java).condition(
            PropertyCondition.equal("userProfile.telegramId", telegramId)
        ).optional()
            .orElse(null)
    }
}