/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.security.dsl.config.builders

import org.junit.Rule
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider
import  org.springframework.security.dsl.test.SpringTestRule
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Tests for [OAuth2LoginDsl]
 *
 * @author Eleftheria Stein
 */
class OAuth2LoginDslTests {
    @Rule
    @JvmField
    val spring = SpringTestRule()

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun oauth2LoginWhenCustomClientRegistrationRepositoryThenBeanIsNotRequired() {
        this.spring.register(ClientRepoConfig::class.java).autowire()
    }

    @EnableWebSecurity
    class ClientRepoConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                oauth2Login {
                    clientRegistrationRepository = InMemoryClientRegistrationRepository(
                            CommonOAuth2Provider.GOOGLE
                                    .getBuilder("google").clientId("clientId").clientSecret("clientSecret")
                                    .build()
                    )
                }
            }
        }
    }

    @Test
    fun loginPageWhenOAuth2LoginConfiguredThenDefaultLoginPageCreated() {
        this.spring.register(OAuth2LoginConfig::class.java, ClientConfig::class.java).autowire()

        this.mockMvc.get("/login")
                .andExpect {
                    status { isOk }
                }
    }

    @EnableWebSecurity
    class OAuth2LoginConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                oauth2Login { }
            }
        }
    }

    @Test
    fun loginPageWhenCustomLoginPageThenRedirectedToCustomPage() {
        this.spring.register(LoginPageConfig::class.java, ClientConfig::class.java).autowire()

        this.mockMvc.get("/custom-login")
                .andExpect {
                    status { isOk }
                }
    }

    @EnableWebSecurity
    class LoginPageConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                oauth2Login {
                    loginPage = "/custom-login"
                }
            }
        }

        @RestController
        class LoginController {
            @GetMapping("/custom-login")
            fun loginPage() { }
        }
    }

    @Configuration
    class ClientConfig {
        @Bean
        fun clientRegistrationRepository(): ClientRegistrationRepository {
            return InMemoryClientRegistrationRepository(
                    CommonOAuth2Provider.GOOGLE
                            .getBuilder("google").clientId("clientId").clientSecret("clientSecret")
                            .build()
            )
        }
    }
}
