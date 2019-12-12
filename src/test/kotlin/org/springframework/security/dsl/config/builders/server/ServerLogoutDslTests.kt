package org.springframework.security.dsl.config.builders.server

import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.dsl.config.builders.test.SpringTestRule
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.logout.ServerLogoutHandler
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.config.EnableWebFlux
import reactor.core.publisher.Mono

/**
 * Tests for [ServerLogoutDsl]
 *
 * @author Eleftheria Stein
 */
internal class ServerLogoutDslTests {
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
    fun `logout when defaults used then redirects to login page`() {
        this.spring.register(LogoutConfig::class.java).autowire()

        val result = this.client
                .mutateWith(csrf())
                .post()
                .uri("/logout")
                .exchange()
                .expectStatus().is3xxRedirection
                .returnResult(String::class.java)

        result.assertWithDiagnostics {
            assertThat(result.responseHeaders.location)
                    .hasPath("/login")
                    .hasParameter("logout")
        }
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class LogoutConfig {
        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                logout { }
            }
        }
    }

    @Test
    fun `logout when custom logout URL then custom URL redirects to login page`() {
        this.spring.register(CustomUrlConfig::class.java).autowire()

        val result = this.client
                .mutateWith(csrf())
                .post()
                .uri("/custom-logout")
                .exchange()
                .expectStatus().is3xxRedirection
                .returnResult(String::class.java)

        result.assertWithDiagnostics {
            assertThat(result.responseHeaders.location)
                    .hasPath("/login")
                    .hasParameter("logout")
        }
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class CustomUrlConfig {
        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                logout {
                    logoutUrl = "/custom-logout"
                }
            }
        }
    }

    @Test
    fun `logout when custom requires logout matcher then matching request redirects to login page`() {
        this.spring.register(RequiresLogoutConfig::class.java).autowire()

        val result = this.client
                .mutateWith(csrf())
                .post()
                .uri("/custom-logout")
                .exchange()
                .expectStatus().is3xxRedirection
                .returnResult(String::class.java)

        result.assertWithDiagnostics {
            assertThat(result.responseHeaders.location)
                    .hasPath("/login")
                    .hasParameter("logout")
        }
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class RequiresLogoutConfig {
        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                logout {
                    requiresLogout = PathPatternParserServerWebExchangeMatcher("/custom-logout")
                }
            }
        }
    }

    @Test
    fun `logout when custom logout handler then custom handler invoked`() {
        this.spring.register(CustomLogoutHandlerConfig::class.java).autowire()

        `when`(CustomLogoutHandlerConfig.LOGOUT_HANDLER.logout(any(), any()))
                .thenReturn(Mono.empty())

        this.client
                .mutateWith(csrf())
                .post()
                .uri("/logout")
                .exchange()

        verify<ServerLogoutHandler>(CustomLogoutHandlerConfig.LOGOUT_HANDLER)
                .logout(any(), any())
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class CustomLogoutHandlerConfig {
        companion object {
            var LOGOUT_HANDLER: ServerLogoutHandler = mock(ServerLogoutHandler::class.java)
        }

        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                logout {
                    logoutHandler = LOGOUT_HANDLER
                }
            }
        }
    }

    @Test
    fun `logout when custom logout success handler then custom handler invoked`() {
        this.spring.register(CustomLogoutSuccessHandlerConfig::class.java).autowire()

        this.client
                .mutateWith(csrf())
                .post()
                .uri("/logout")
                .exchange()

        verify<ServerLogoutSuccessHandler>(CustomLogoutSuccessHandlerConfig.LOGOUT_HANDLER)
                .onLogoutSuccess(any(), any())
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class CustomLogoutSuccessHandlerConfig {
        companion object {
            var LOGOUT_HANDLER: ServerLogoutSuccessHandler = mock(ServerLogoutSuccessHandler::class.java)
        }

        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                logout {
                    logoutSuccessHandler = LOGOUT_HANDLER
                }
            }
        }
    }

    @Test
    fun `logout when disabled then logout URL not found`() {
        this.spring.register(LogoutDisabledConfig::class.java).autowire()

        this.client
                .mutateWith(csrf())
                .post()
                .uri("/logout")
                .exchange()
                .expectStatus().isNotFound
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class LogoutDisabledConfig {
        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                authorizeExchange {
                    authorize(anyExchange, permitAll)
                }
                logout {
                    disable()
                }
            }
        }
    }
}