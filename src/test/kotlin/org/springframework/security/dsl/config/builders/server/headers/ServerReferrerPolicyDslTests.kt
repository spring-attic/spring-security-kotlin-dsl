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
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.header.ReferrerPolicyServerHttpHeadersWriter
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.config.EnableWebFlux

/**
 * Tests for [ServerReferrerPolicyDsl]
 *
 * @author Eleftheria Stein
 */
internal class ServerReferrerPolicyDslTests {
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
    fun `request when referrer policy configured then referrer policy header in response`() {
        this.spring.register(ReferrerPolicyConfig::class.java).autowire()

        this.client.get()
                .uri("/")
                .exchange()
                .expectHeader().valueEquals("Referrer-Policy", ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER.policy)
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class ReferrerPolicyConfig {
        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                headers {
                    referrerPolicy { }
                }
            }
        }
    }

    @Test
    fun `request when custom policy configured then custom policy in response header`() {
        this.spring.register(CustomPolicyConfig::class.java).autowire()

        this.client.get()
                .uri("/")
                .exchange()
                .expectHeader().valueEquals("Referrer-Policy", ReferrerPolicyServerHttpHeadersWriter.ReferrerPolicy.SAME_ORIGIN.policy)
    }

    @EnableWebFluxSecurity
    @EnableWebFlux
    class CustomPolicyConfig {
        @Bean
        fun springWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
            return http {
                headers {
                    referrerPolicy {
                        policy = ReferrerPolicyServerHttpHeadersWriter.ReferrerPolicy.SAME_ORIGIN
                    }
                }
            }
        }
    }
}