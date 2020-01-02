package org.springframework.security.dsl.config.builders.server

import org.junit.Rule
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.dsl.config.builders.test.SpringTestRule
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import org.springframework.web.reactive.config.EnableWebFlux

/**
 * Tests for [ServerCorsDsl]
 *
 * @author Eleftheria Stein
 */
internal class ServerCorsDslTests {
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
    fun `request when CORS configured using bean then Access-Control-Allow-Origin header in response`() {
        this.spring.register(CorsBeanConfig::class.java).autowire()

        this.client.get()
                .uri("https://example.com")
                .header(HttpHeaders.ORIGIN, "https://origin.example.com")
                .exchange()
                .expectHeader().valueEquals("Access-Control-Allow-Origin", "*")
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class CorsBeanConfig {
        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                cors { }
            }
        }

        @Bean
        fun corsConfigurationSource(): CorsConfigurationSource {
            val source = UrlBasedCorsConfigurationSource()
            val corsConfiguration = CorsConfiguration()
            corsConfiguration.allowedOrigins = listOf("*")
            source.registerCorsConfiguration("/**", corsConfiguration)
            return source
        }
    }

    @Test
    fun `request when CORS configured using source then Access-Control-Allow-Origin header in response`() {
        this.spring.register(CorsSourceConfig::class.java).autowire()

        this.client.get()
                .uri("https://example.com")
                .header(HttpHeaders.ORIGIN, "https://origin.example.com")
                .exchange()
                .expectHeader().valueEquals("Access-Control-Allow-Origin", "*")
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class CorsSourceConfig {
        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            val source = UrlBasedCorsConfigurationSource()
            val corsConfiguration = CorsConfiguration()
            corsConfiguration.allowedOrigins = listOf("*")
            source.registerCorsConfiguration("/**", corsConfiguration)
            return http {
                cors {
                    configurationSource = source
                }
            }
        }
    }

    @Test
    fun `request when CORS disabled then no Access-Control-Allow-Origin header in response`() {
        this.spring.register(CorsDisabledConfig::class.java).autowire()

        this.client.get()
                .uri("https://example.com")
                .header(HttpHeaders.ORIGIN, "https://origin.example.com")
                .exchange()
                .expectHeader().doesNotExist("Access-Control-Allow-Origin")
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class CorsDisabledConfig {
        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                cors {
                    disable()
                }
            }
        }
    }
}