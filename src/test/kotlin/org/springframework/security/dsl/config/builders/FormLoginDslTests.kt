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
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import  org.springframework.security.dsl.test.SpringTestRule
import org.springframework.security.core.userdetails.User
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Controller
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.bind.annotation.GetMapping

/**
 * Tests for [FormLoginDsl]
 *
 * @author Eleftheria Stein
 */
class FormLoginDslTests {
    @Rule
    @JvmField
    val spring = SpringTestRule()

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun loginPageWhenFormLoginConfiguredThenDefaultLoginPageCreated() {
        this.spring.register(FormLoginConfig::class.java, UserConfig::class.java).autowire()

        this.mockMvc.get("/login")
                .andExpect {
                    status { isOk }
                }
    }

    @Test
    fun loginWhenSuccessThenRedirectsToHome() {
        this.spring.register(FormLoginConfig::class.java, UserConfig::class.java).autowire()

        this.mockMvc.perform(formLogin())
                .andExpect {
                    status().isFound
                    redirectedUrl("/")
                }
    }

    @Test
    fun loginWhenFailureThenRedirectsToLoginPageWithError() {
        this.spring.register(FormLoginConfig::class.java, UserConfig::class.java).autowire()

        this.mockMvc.perform(formLogin().password("invalid"))
                .andExpect {
                    status().isFound
                    redirectedUrl("/login?error")
                }
    }

    @EnableWebSecurity
    class FormLoginConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                formLogin {}
            }
        }
    }

    @Test
    fun requestWhenSecureThenRedirectsToDefaultLoginPage() {
        this.spring.register(AllSecuredConfig::class.java, UserConfig::class.java).autowire()

        this.mockMvc.get("/")
                .andExpect {
                    status { isFound }
                    redirectedUrl("http://localhost/login")
                }
    }

    @EnableWebSecurity
    class AllSecuredConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                formLogin {}
                authorizeRequests {
                    authorize(anyRequest, authenticated)
                }
            }
        }
    }

    @Test
    fun requestWhenSecureAndCustomLoginPageThenRedirectsToCustomLoginPage() {
        this.spring.register(LoginPageConfig::class.java, UserConfig::class.java).autowire()

        this.mockMvc.get("/")
                .andExpect {
                    status { isFound }
                    redirectedUrl("http://localhost/log-in")
                }
    }

    @EnableWebSecurity
    class LoginPageConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                formLogin {
                    loginPage = "/log-in"
                }
                authorizeRequests {
                    authorize(anyRequest, authenticated)
                }
            }
        }
    }

    @Test
    fun loginWhenCustomSuccessHandlerThenUsed() {
        this.spring.register(SuccessHandlerConfig::class.java, UserConfig::class.java).autowire()

        this.mockMvc.perform(formLogin())
                .andExpect {
                    status().isFound
                    redirectedUrl("/success")
                }
    }

    @EnableWebSecurity
    class SuccessHandlerConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                formLogin {
                    authenticationSuccessHandler = SimpleUrlAuthenticationSuccessHandler("/success")
                }
            }
        }
    }

    @Test
    fun loginWhenCustomFailureHandlerThenUsed() {
        this.spring.register(FailureHandlerConfig::class.java, UserConfig::class.java).autowire()

        this.mockMvc.perform(formLogin().password("invalid"))
                .andExpect {
                    status().isFound
                    redirectedUrl("/failure")
                }
    }

    @EnableWebSecurity
    class FailureHandlerConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                formLogin {
                    authenticationFailureHandler = SimpleUrlAuthenticationFailureHandler("/failure")
                }
            }
        }
    }

    @Test
    fun loginWhenCustomFailureUrlThenUsed() {
        this.spring.register(FailureHandlerConfig::class.java, UserConfig::class.java).autowire()

        this.mockMvc.perform(formLogin().password("invalid"))
                .andExpect {
                    status().isFound
                    redirectedUrl("/failure")
                }
    }

    @EnableWebSecurity
    class FailureUrlConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                formLogin {
                    failureUrl = "/failure"
                }
            }
        }
    }

    @Test
    fun loginWhenCustomLoginProcessingUrlThenUsed() {
        this.spring.register(LoginProcessingUrlConfig::class.java, UserConfig::class.java).autowire()

        this.mockMvc.perform(formLogin("/custom"))
                .andExpect {
                    status().isFound
                    redirectedUrl("/")
                }
    }

    @EnableWebSecurity
    class LoginProcessingUrlConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                formLogin {
                    loginProcessingUrl = "/custom"
                }
            }
        }
    }

    @Test
    fun loginWhenDefaultSuccessUrlThenRedirectedToUrl() {
        this.spring.register(DefaultSuccessUrlConfig::class.java, UserConfig::class.java).autowire()

        this.mockMvc.perform(formLogin())
                .andExpect {
                    status().isFound
                    redirectedUrl("/custom")
                }
    }

    @EnableWebSecurity
    class DefaultSuccessUrlConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                formLogin {
                    defaultSuccessUrl("/custom", true)
                }
            }
        }
    }

    @Test
    fun loginWhenPermitAllThenLoginPageNotProtected() {
        this.spring.register(PermitAllConfig::class.java, UserConfig::class.java).autowire()

        this.mockMvc.get("/custom/login")
                .andExpect {
                    status { isOk }
                }
    }

    @EnableWebSecurity
    class PermitAllConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                authorizeRequests {
                    authorize(anyRequest, authenticated)
                }
                formLogin {
                    loginPage = "/custom/login"
                    permitAll()
                }
            }
        }

        @Controller
        class LoginController {
            @GetMapping("/custom/login")
            fun loginPage() {}
        }
    }

    @Configuration
    class UserConfig {
        @Autowired
        fun configureGlobal(auth: AuthenticationManagerBuilder) {
            auth
                    .inMemoryAuthentication()
                    .withUser(User.withDefaultPasswordEncoder().username("user").password("password").roles("USER"))
        }
    }
}
