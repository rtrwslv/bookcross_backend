package ru.itport.bookcross.services

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import ru.itport.bookcross.security.DatabaseUserRepository
import java.io.IOException

@Component
class JwtTokenFilter(
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService,
    private val userDatabaseUserRepository: DatabaseUserRepository
) : OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        var requestRq = request
        val contextType = requestRq.contentType
        if (contextType == null || !contextType.contains("multipart/from-data")) {
            requestRq = ContentCachingRequestWrapper(requestRq)
        }
        val token = jwtService.resolveToken(requestRq)
        if (token == null) {
            filterChain.doFilter(requestRq, response)
            return
        }
        val userDetails = userDetailsService.loadUserByUsername(jwtService.extractUserName(token))

        if (jwtService.isTokenValid(token, userDetails)) {
            val authentication =
                UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDatabaseUserRepository.initDefaultUserAuthorities()
                )
            authentication.details = WebAuthenticationDetailsSource().buildDetails(requestRq)
            SecurityContextHolder.getContext().authentication = authentication
        }
        filterChain.doFilter(requestRq, response)
    }
}