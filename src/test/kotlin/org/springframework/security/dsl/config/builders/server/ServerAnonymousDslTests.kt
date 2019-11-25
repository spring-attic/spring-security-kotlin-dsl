package org.springframework.security.dsl.config.builders.server

import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.dsl.config.builders.test.SpringTestRule
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.config.EnableWebFlux
import reactor.core.publisher.Mono

/**
 * Tests for [ServerAnonymousDsl]
 *
 * @author Eleftheria Stein
 */
internal class ServerAnonymousDslTests {
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
    fun `authentication when anonymous enabled then is of type anonymous authentication`() {
        this.spring.register(AnonymousConfig::class.java, HttpMeController::class.java).autowire()

        this.client.get()
                .uri("/principal")
                .exchange()
                .expectStatus().isOk
                .expectBody<String>().isEqualTo("anonymousUser")
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class AnonymousConfig {
        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                anonymous { }
            }
        }
    }

    @Test
    fun `anonymous when custom principal specified then custom principal is used`() {
        this.spring.register(CustomPrincipalConfig::class.java, HttpMeController::class.java).autowire()

        this.client.get()
                .uri("/principal")
                .exchange()
                .expectStatus().isOk
                .expectBody<String>().isEqualTo("anon")
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class CustomPrincipalConfig {
        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                anonymous {
                    principal = "anon"
                }
            }
        }
    }

    @Test
    fun `anonymous when disabled then principal is null`() {
        this.spring.register(AnonymousDisabledConfig::class.java, HttpMeController::class.java).autowire()

        this.client.get()
                .uri("/principal")
                .exchange()
                .expectStatus().isOk
                .expectBody<String>().consumeWith { body -> assertThat(body.responseBody).isNull() }
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class AnonymousDisabledConfig {
        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                anonymous {
                    disable()
                }
            }
        }
    }

    @Test
    fun `anonymous when custom key specified then custom key used`() {
        this.spring.register(CustomKeyConfig::class.java, HttpMeController::class.java).autowire()

        this.client.get()
                .uri("/key")
                .exchange()
                .expectStatus().isOk
                .expectBody<String>().isEqualTo("key".hashCode().toString())
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class CustomKeyConfig {
        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                anonymous {
                    key = "key"
                }
            }
        }
    }

    @Test
    fun `anonymous when custom authorities specified then custom authorities used`() {
        this.spring.register(CustomAuthoritiesConfig::class.java, HttpMeController::class.java).autowire()

        this.client.get()
                .uri("/principal")
                .exchange()
                .expectStatus().isOk
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class CustomAuthoritiesConfig {
        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                anonymous {
                    authorities = listOf(SimpleGrantedAuthority("TEST"))
                }
                authorizeExchange {
                    authorize(anyExchange, hasAuthority("TEST"))
                }
            }
        }
    }

    @RestController
    class HttpMeController {
        @GetMapping("/principal")
        fun principal(@AuthenticationPrincipal principal: String?): String? {
            return principal
        }

        @GetMapping("/key")
        fun key(@AuthenticationPrincipal principal: Mono<AnonymousAuthenticationToken>): Mono<String> {
            return principal
                    .map { it.keyHash }
                    .map { it.toString() }
        }
    }
}