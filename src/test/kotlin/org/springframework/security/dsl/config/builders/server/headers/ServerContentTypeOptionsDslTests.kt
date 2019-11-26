package org.springframework.security.dsl.config.builders.server.headers

import org.junit.Rule
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.dsl.config.builders.server.invoke
import org.springframework.security.dsl.config.builders.test.SpringTestRule
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.header.ContentTypeOptionsServerHttpHeadersWriter
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.config.EnableWebFlux

/**
 * Tests for [ServerContentTypeOptionsDsl]
 *
 * @author Eleftheria Stein
 */
internal class ServerContentTypeOptionsDslTests {
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
    fun `request when content type options configured then header in response`() {
        this.spring.register(ContentTypeOptionsConfig::class.java).autowire()

        this.client.get()
                .uri("/")
                .exchange()
                .expectHeader().valueEquals(ContentTypeOptionsServerHttpHeadersWriter.X_CONTENT_OPTIONS, "nosniff")
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class ContentTypeOptionsConfig {
        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                headers {
                    contentTypeOptions { }
                }
            }
        }
    }

    @Test
    fun `request when content type options disabled then no content type options header in response`() {
        this.spring.register(ContentTypeOptionsDisabledConfig::class.java).autowire()

        this.client.get()
                .uri("/")
                .exchange()
                .expectHeader().doesNotExist(ContentTypeOptionsServerHttpHeadersWriter.X_CONTENT_OPTIONS)
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class ContentTypeOptionsDisabledConfig {
        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                headers {
                    contentTypeOptions {
                        disable()
                    }
                }
            }
        }
    }
}