package org.springframework.security.dsl.config.builders.server

import org.assertj.core.api.Assertions
import org.junit.Rule
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.dsl.config.builders.test.SpringTestRule
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint
import org.springframework.security.web.server.authorization.HttpStatusServerAccessDeniedHandler
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.config.EnableWebFlux
import java.util.*

/**
 * Tests for [ServerExceptionHandlingDsl]
 *
 * @author Eleftheria Stein
 */
internal class ServerExceptionHandlingDslTests {
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
    fun `unauthenticated request when custom entry point then directed to custom entry point`() {
        this.spring.register(EntryPointConfig::class.java).autowire()

        val result = this.client.get()
                .uri("/")
                .exchange()
                .expectStatus().is3xxRedirection
                .returnResult(String::class.java)

        result.assertWithDiagnostics {
            Assertions.assertThat(result.responseHeaders.location).hasPath("/auth")
        }
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class EntryPointConfig {
        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                authorizeExchange {
                    authorize(anyExchange, authenticated)
                }
                exceptionHandling {
                    authenticationEntryPoint = RedirectServerAuthenticationEntryPoint("/auth")
                }
            }
        }
    }

    @Test
    fun `unauthorized request when custom access denied handler then directed to custom access denied handler`() {
        this.spring.register(AccessDeniedHandlerConfig::class.java).autowire()

        this.client
                .get()
                .uri("/")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:password".toByteArray()))
                .exchange()
                .expectStatus().isSeeOther
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class AccessDeniedHandlerConfig {
        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                authorizeExchange {
                    authorize(anyExchange, hasRole("ADMIN"))
                }
                httpBasic { }
                exceptionHandling {
                    accessDeniedHandler = HttpStatusServerAccessDeniedHandler(HttpStatus.SEE_OTHER)
                }
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