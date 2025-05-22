package ru.itport.bookcross.services

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ValidationException
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.security.Key
import java.util.*
import java.util.function.Function

@Service
class JwtService {
    @Value("\${security.jwt.secret-key}")
    private val jwtSigningKey: String? = null

    @Value("\${security.jwt.experation-time}")
    private var jwtExpiration: Long = 0

    /**
     * Извлечение имени пользователя из токена
     *
     * @param token токен
     * @return имя пользователя
     */
    fun extractUserName(token: String): String {
        try {
            return extractClaim(token) { obj: Claims -> obj.subject }
        } catch (e: Exception) {
            throw ValidationException("Ошибка валидации токена")
        }
    }


    /**
     * Генерация токена
     *
     * @param userDetails данные пользователя
     * @return токен
     */
    fun generateToken(username: String): Pair<String, Date> {
        return generateToken(HashMap(), username, jwtExpiration)
    }

    /**
     * Генерация токена
     *
     * @param extraClaims дополнительные данные
     * @param userDetails данные пользователя
     * @return токен
     */
    private fun generateToken(
        extraClaims: Map<String, Any?>, username: String,
        expiration: Long
    ): Pair<String, Date> {
        val timeExpiration = Date(System.currentTimeMillis() + expiration)
        val token = Jwts.builder()
            .claims(extraClaims)
            .subject(username)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(timeExpiration)
            .signWith(signingKey, SignatureAlgorithm.HS256).compact()
    return Pair(token,timeExpiration)
    }


    /**
     * Проверка токена на валидность
     *
     * @param token       токен
     * @param userDetails данные пользователя
     * @return true, если токен валиден
     */
    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val userName = extractUserName(token)
        return (userName == userDetails.username) && !isTokenExpired(token)
    }

    /**
     * Извлечение данных из токена
     *
     * @param token           токен
     * @param claimsResolvers функция извлечения данных
     * @param <T>             тип данных
     * @return данные
    </T> */
    private fun <T> extractClaim(token: String, claimsResolvers: Function<Claims, T>): T {
        val claims = extractAllClaims(token)
        return claimsResolvers.apply(claims)
    }

    fun resolveToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7)
        }
        return null
    }

    /**
     * Проверка токена на просроченность
     *
     * @param token токен
     * @return true, если токен просрочен
     */
    private fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }

    /**
     * Извлечение даты истечения токена
     *
     * @param token токен
     * @return дата истечения
     */
    private fun extractExpiration(token: String): Date {
        return extractClaim(token) { obj: Claims -> obj.expiration }
    }

    /**
     * Извлечение всех данных из токена
     *
     * @param token токен
     * @return данные
     */
    private fun extractAllClaims(token: String): Claims {
        return Jwts.parser().setSigningKey(signingKey).build().parseClaimsJws(token)
            .body
    }

    private val signingKey: Key
        /**
         * Получение ключа для подписи токена
         *
         * @return ключ
         */
        get() {
            val keyBytes = Decoders.BASE64.decode(jwtSigningKey)
            return Keys.hmacShaKeyFor(keyBytes)
        }
}