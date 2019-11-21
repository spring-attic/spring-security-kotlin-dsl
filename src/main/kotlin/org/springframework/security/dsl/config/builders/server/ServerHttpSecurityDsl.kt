package org.springframework.security.dsl.config.builders.server

import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher
import org.springframework.web.server.ServerWebExchange

/**
 * Configures [ServerHttpSecurity] using a [ServerHttpSecurity Kotlin DSL][ServerHttpSecurityDsl].
 *
 * Example:
 *
 * ```
 * @EnableWebFluxSecurity
 * class SecurityConfig {
 *
 *  @Bean
 *  fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
 *      return http {
 *          authorizeExchange {
 *              exchange("/public", permitAll)
 *              exchange(anyExchange, authenticated)
 *          }
 *       }
 *   }
 * }
 * ```
 *
 * @author Eleftheria Stein
 * @param httpConfiguration the configurations to apply to [ServerHttpSecurity]
 */
operator fun ServerHttpSecurity.invoke(httpConfiguration: ServerHttpSecurityDsl.() -> Unit): SecurityWebFilterChain =
        ServerHttpSecurityDsl(this, httpConfiguration).build()

/**
 * A [ServerHttpSecurity] Kotlin DSL created by [`http { }`][invoke]
 * in order to configure [ServerHttpSecurity] using idiomatic Kotlin code.
 *
 * @author Eleftheria Stein
 * @param init the configurations to apply to the provided [ServerHttpSecurity]
 */
class ServerHttpSecurityDsl(private val http: ServerHttpSecurity, private val init: ServerHttpSecurityDsl.() -> Unit) {

    /**
     * Allows configuring the [ServerHttpSecurity] to only be invoked when matching the
     * provided [ServerWebExchangeMatcher].
     *
     * Example:
     *
     * ```
     * @EnableWebFluxSecurity
     * class SecurityConfig {
     *
     *  @Bean
     *  fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
     *      return http {
     *          securityMatcher(PathPatternParserServerWebExchangeMatcher("/api/&ast;&ast;"))
     *          formLogin {
     *              loginPage = "/log-in"
     *          }
     *       }
     *   }
     * }
     * ```
     *
     * @param securityMatcher a [ServerWebExchangeMatcher] used to determine whether this
     * configuration should be invoked.
     */
    fun securityMatcher(securityMatcher: ServerWebExchangeMatcher) {
        this.http.securityMatcher(securityMatcher)
    }

    /**
     * Enables form based authentication.
     *
     * Example:
     *
     * ```
     * @EnableWebFluxSecurity
     * class SecurityConfig {
     *
     *  @Bean
     *  fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
     *      return http {
     *          formLogin {
     *              loginPage = "/log-in"
     *          }
     *       }
     *   }
     * }
     * ```
     *
     * @param formLoginConfiguration custom configuration to apply to the form based
     * authentication
     * @see [ServerFormLoginDsl]
     */
    fun formLogin(formLoginConfiguration: ServerFormLoginDsl.() -> Unit) {
        val formLoginCustomizer = ServerFormLoginDsl().apply(formLoginConfiguration).get()
        this.http.formLogin(formLoginCustomizer)
    }

    /**
     * Allows restricting access based upon the [ServerWebExchange]
     *
     * Example:
     *
     * ```
     * @EnableWebFluxSecurity
     * class SecurityConfig {
     *
     *  @Bean
     *  fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
     *      return http {
     *          authorizeExchange {
     *              exchange("/public", permitAll)
     *              exchange(anyExchange, authenticated)
     *          }
     *       }
     *   }
     * }
     * ```
     *
     * @param authorizeExchangeConfiguration custom configuration that specifies
     * access for an exchange
     * @see [AuthorizeExchangeDsl]
     */
    fun authorizeExchange(authorizeExchangeConfiguration: AuthorizeExchangeDsl.() -> Unit) {
        val authorizeExchangeCustomizer = AuthorizeExchangeDsl().apply(authorizeExchangeConfiguration).get()
        this.http.authorizeExchange(authorizeExchangeCustomizer)
    }

    /**
     * Enables HTTP basic authentication.
     *
     * Example:
     *
     * ```
     * @EnableWebFluxSecurity
     * class SecurityConfig {
     *
     *  @Bean
     *  fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
     *      return http {
     *          httpBasic { }
     *       }
     *   }
     * }
     * ```
     *
     * @param httpBasicConfiguration custom configuration to be applied to the
     * HTTP basic authentication
     * @see [ServerHttpBasicDsl]
     */
    fun httpBasic(httpBasicConfiguration: ServerHttpBasicDsl.() -> Unit) {
        val httpBasicCustomizer = ServerHttpBasicDsl().apply(httpBasicConfiguration).get()
        this.http.httpBasic(httpBasicCustomizer)
    }

    /**
     * Enables CSRF protection.
     *
     * Example:
     *
     * ```
     * @EnableWebFluxSecurity
     * class SecurityConfig {
     *
     *  @Bean
     *  fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
     *      return http {
     *          csrf { }
     *       }
     *   }
     * }
     * ```
     *
     * @param csrfConfiguration custom configuration to apply to the CSRF protection
     * @see [ServerCsrfDsl]
     */
    fun csrf(csrfConfiguration: ServerCsrfDsl.() -> Unit) {
        val csrfCustomizer = ServerCsrfDsl().apply(csrfConfiguration).get()
        this.http.csrf(csrfCustomizer)
    }

    /**
     * Apply all configurations to the provided [ServerHttpSecurity]
     */
    internal fun build(): SecurityWebFilterChain {
        init()
        return this.http.build()
    }
}