package ru.itport.bookcross.security

import io.jmix.core.JmixSecurityFilterChainOrder
import io.jmix.security.util.JmixHttpSecurityUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import ru.itport.bookcross.services.JwtTokenFilter
import java.util.*

/**
 * This configuration complements standard security configurations that come from Jmix modules (security-flowui, oidc,
 * authserver).
 * <p>
 * You can configure custom API endpoints security by defining [SecurityFilterChain] beans in this class.
 * In most cases, custom SecurityFilterChain must be applied first, so the proper
 * [org.springframework.core.annotation.Order] should be defined for the bean. The order value from the
 * [io.jmix.core.JmixSecurityFilterChainOrder#CUSTOM] is guaranteed to be smaller than any other filter chain
 * order from Jmix.
 *
 * @see io.jmix.securityflowui.security.FlowuiVaadinWebSecurity
 */
@Configuration
open class BasicAppSecurityConfiguration(private val jwtTokenFilter: JwtTokenFilter) {

    @Bean
    @Order(JmixSecurityFilterChainOrder.CUSTOM)
    open fun apiFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .securityMatcher("custom/api/v1/**", "rest/**")
            .authorizeHttpRequests { authorize ->
                authorize.requestMatchers("custom/api/v1/auth/login").permitAll()
                authorize.requestMatchers("rest/files/**").permitAll()
                authorize.anyRequest().permitAll()
            }
            .csrf { csrf -> csrf.disable() }
            .cors().and()
            .addFilterBefore(jwtTokenFilter, AnonymousAuthenticationFilter::class.java)
        JmixHttpSecurityUtils.configureAnonymous(http)
        return http.build()
    }

    @Bean
    open fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = Arrays.asList("*")
        configuration.allowedMethods = Arrays.asList("*")
        configuration.allowedHeaders = Arrays.asList("*")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}