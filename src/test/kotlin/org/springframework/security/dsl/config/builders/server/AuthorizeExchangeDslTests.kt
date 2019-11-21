package org.springframework.security.dsl.config.builders.server

import org.junit.Rule
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.dsl.config.builders.test.SpringTestRule
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.config.EnableWebFlux
import java.util.*

/**
 * Tests for [AuthorizeExchangeDsl]
 *
 * @author Eleftheria Stein
 */
internal class AuthorizeExchangeDslTests {
    @Rule
    @JvmField
    val spring = SpringTestRule()

    private lateinit var client: WebTestClient

    @Autowired
    fun setup(context: ApplicationContext) {
        this.client = WebTestClient
                .bindToApplicationContext(context)
                .configureClient()
                .build()
    }

    @Test
    fun `request when secured by matcher then responds with unauthorized`() {
        this.spring.register(MatcherAuthenticatedConfig::class.java).autowire()

        this.client.get()
                .uri("/")
                .exchange()
                .expectStatus().isUnauthorized
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class MatcherAuthenticatedConfig {
        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                authorizeExchange {
                    authorize(anyExchange, authenticated)
                }
            }
        }
    }

    @Test
    fun `request when allowed by matcher then responds with ok`() {
        this.spring.register(MatcherPermitAllConfig::class.java).autowire()

        this.client.get()
                .uri("/")
                .exchange()
                .expectStatus().isOk
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class MatcherPermitAllConfig {
        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                authorizeExchange {
                    authorize(anyExchange, permitAll)
                }
            }
        }

        @RestController
        internal class PathController {
            @RequestMapping("/")
            fun path() {
            }
        }
    }

    @Test
    fun `request when secured by pattern then responds with unauthorized`() {
        this.spring.register(PatternAuthenticatedConfig::class.java).autowire()

        this.client.get()
                .uri("/")
                .exchange()
                .expectStatus().isUnauthorized
    }

    @Test
    fun `request when allowed by pattern then responds with ok`() {
        this.spring.register(PatternAuthenticatedConfig::class.java).autowire()

        this.client.get()
                .uri("/public")
                .exchange()
                .expectStatus().isOk
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class PatternAuthenticatedConfig {
        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                authorizeExchange {
                    authorize("/public", permitAll)
                    authorize("/**", authenticated)
                }
            }
        }

        @RestController
        internal class PathController {
            @RequestMapping("/public")
            fun public() {
            }
        }
    }

    @Test
    fun `request when missing required role then responds with forbidden`() {
        this.spring.register(HasRoleConfig::class.java).autowire()
        this.client
                .get()
                .uri("/")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:password".toByteArray()))
                .exchange()
                .expectStatus().isForbidden
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class HasRoleConfig {
        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                authorizeExchange {
                    authorize(anyExchange, hasRole("ADMIN"))
                }
                httpBasic { }
            }
        }

        @Bean
        fun userDetailsService(): MapReactiveUserDetailsService {
            val user = User.withDefaultPasswordEncoder()
                    .username("user")
                    .password("password")
                    .roles("USER")
                    .build()
            return MapReactiveUserDetailsService(user)
        }
    }
}