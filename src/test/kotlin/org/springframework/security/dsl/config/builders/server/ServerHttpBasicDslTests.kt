package org.springframework.security.dsl.config.builders.server

import org.junit.Rule
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.dsl.config.builders.test.SpringTestRule
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.config.EnableWebFlux
import reactor.core.publisher.Mono
import java.util.*

/**
 * Tests for [ServerHttpBasicDsl]
 *
 * @author Eleftheria Stein
 */
internal class ServerHttpBasicDslTests {
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
    fun `http basic when no authorization header then responds with unauthorized`() {
        this.spring.register(HttpBasicConfig::class.java, UserDetailsConfig::class.java).autowire()

        this.client.get()
                .uri("/")
                .exchange()
                .expectStatus().isUnauthorized
    }

    @Test
    fun `http basic when valid authorization header then responds with ok`() {
        this.spring.register(HttpBasicConfig::class.java, UserDetailsConfig::class.java).autowire()

        this.client.get()
                .uri("/")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:password".toByteArray()))
                .exchange()
                .expectStatus().isOk
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class HttpBasicConfig {
        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                authorizeExchange {
                    authorize(anyExchange, authenticated)
                }
                httpBasic { }
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
    fun `http basic when custom authentication manager then manager used`() {
        given<Mono<Authentication>>(CustomAuthenticationManagerConfig.AUTHENTICATION_MANAGER.authenticate(any()))
                .willReturn(Mono.just<Authentication>(TestingAuthenticationToken("rob", "rob", "ROLE_USER", "ROLE_ADMIN")))

        this.spring.register(CustomAuthenticationManagerConfig::class.java).autowire()

        this.client.get()
                .uri("/")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:password".toByteArray()))
                .exchange()

        verify<ReactiveAuthenticationManager>(CustomAuthenticationManagerConfig.AUTHENTICATION_MANAGER)
                .authenticate(any())
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class CustomAuthenticationManagerConfig {
        companion object {
            var AUTHENTICATION_MANAGER: ReactiveAuthenticationManager = Mockito.mock(ReactiveAuthenticationManager::class.java)
        }

        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                authorizeExchange {
                    authorize(anyExchange, authenticated)
                }
                httpBasic {
                    authenticationManager = AUTHENTICATION_MANAGER
                }
            }
        }
    }

    @Test
    fun `http basic when custom security context repository then repository used`() {
        this.spring.register(CustomSecurityContextRepositoryConfig::class.java, UserDetailsConfig::class.java).autowire()

        this.client.get()
                .uri("/")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:password".toByteArray()))
                .exchange()

        verify<ServerSecurityContextRepository>(CustomSecurityContextRepositoryConfig.SECURITY_CONTEXT_REPOSITORY)
                .save(any(), any())
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class CustomSecurityContextRepositoryConfig {
        companion object {
            var SECURITY_CONTEXT_REPOSITORY: ServerSecurityContextRepository = mock(ServerSecurityContextRepository::class.java)
        }

        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                authorizeExchange {
                    authorize(anyExchange, authenticated)
                }
                httpBasic {
                    securityContextRepository = SECURITY_CONTEXT_REPOSITORY
                }
            }
        }
    }

    @Test
    fun `http basic when custom authentication entry point then entry point used`() {
        this.spring.register(CustomAuthenticationEntryPointConfig::class.java, UserDetailsConfig::class.java).autowire()

        this.client.get()
                .uri("/")
                .exchange()

        verify<ServerAuthenticationEntryPoint>(CustomAuthenticationEntryPointConfig.ENTRY_POINT)
                .commence(any(), any())
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class CustomAuthenticationEntryPointConfig {
        companion object {
            var ENTRY_POINT: ServerAuthenticationEntryPoint = mock(ServerAuthenticationEntryPoint::class.java)
        }

        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                authorizeExchange {
                    authorize(anyExchange, authenticated)
                }
                httpBasic {
                    authenticationEntryPoint = ENTRY_POINT
                }
            }
        }
    }

    @Configuration
    class UserDetailsConfig {
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