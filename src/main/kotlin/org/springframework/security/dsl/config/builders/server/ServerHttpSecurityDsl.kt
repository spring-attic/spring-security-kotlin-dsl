package org.springframework.security.dsl.config.builders.server

import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.dsl.config.builders.servlet.AnonymousDsl
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
     * Allows configuring response headers.
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
     *          headers {
     *              referrerPolicy {
     *                  policy = ReferrerPolicy.SAME_ORIGIN
     *              }
     *              frameOptions {
     *                  mode = Mode.DENY
     *              }
     *          }
     *       }
     *   }
     * }
     * ```
     *
     * @param headersConfiguration custom configuration to be applied to the
     * response headers
     * @see [ServerHeadersDsl]
     */
    fun headers(headersConfiguration: ServerHeadersDsl.() -> Unit) {
        val headersCustomizer = ServerHeadersDsl().apply(headersConfiguration).get()
        this.http.headers(headersCustomizer)
    }

    /**
     * Allows configuring exception handling.
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
     *          exceptionHandling {
     *              authenticationEntryPoint = RedirectServerAuthenticationEntryPoint("/auth")
     *          }
     *       }
     *   }
     * }
     * ```
     *
     * @param exceptionHandlingConfiguration custom configuration to apply to
     * exception handling
     * @see [ServerExceptionHandlingDsl]
     */
    fun exceptionHandling(exceptionHandlingConfiguration: ServerExceptionHandlingDsl.() -> Unit) {
        val exceptionHandlingCustomizer = ServerExceptionHandlingDsl().apply(exceptionHandlingConfiguration).get()
        this.http.exceptionHandling(exceptionHandlingCustomizer)
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
     * Provides logout support.
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
     *          logout {
     *              logoutUrl = "/sign-out"
     *          }
     *       }
     *   }
     * }
     * ```
     *
     * @param logoutConfiguration custom configuration to apply to logout
     * @see [ServerLogoutDsl]
     */
    fun logout(logoutConfiguration: ServerLogoutDsl.() -> Unit) {
        val logoutCustomizer = ServerLogoutDsl().apply(logoutConfiguration).get()
        this.http.logout(logoutCustomizer)
    }

    /**
     * Enables and configures anonymous authentication.
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
     *          anonymous {
     *              authorities = listOf(SimpleGrantedAuthority("ROLE_ANON"))
     *          }
     *       }
     *   }
     * }
     * ```
     *
     * @param anonymousConfiguration custom configuration to apply to anonymous authentication
     * @see [AnonymousDsl]
     */
    fun anonymous(anonymousConfiguration: ServerAnonymousDsl.() -> Unit) {
        val anonymousCustomizer = ServerAnonymousDsl().apply(anonymousConfiguration).get()
        this.http.anonymous(anonymousCustomizer)
    }

    /**
     * Apply all configurations to the provided [ServerHttpSecurity]
     */
    internal fun build(): SecurityWebFilterChain {
        init()
        return this.http.build()
    }
}