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
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import  org.springframework.security.dsl.test.SpringTestRule
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin
import org.springframework.security.web.savedrequest.NullRequestCache
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl

/**
 * Tests for [RequestCacheDsl]
 *
 * @author Eleftheria Stein
 */
class RequestCacheDslTests {
    @Rule
    @JvmField
    val spring = SpringTestRule()

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun getWhenRequestCacheEnabledThenRedirectedToCachedPage() {
        this.spring.register(RequestCacheConfig::class.java).autowire()

        this.mockMvc.get("/test")

        this.mockMvc.perform(formLogin())
                .andExpect {
                    redirectedUrl("http://localhost/test")
                }
    }

    @EnableWebSecurity
    class RequestCacheConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                requestCache { }
                formLogin { }
            }
        }
    }

    @Test
    fun getWhenCustomRequestCacheThenCustomRequestCacheUsed() {
        this.spring.register(CustomRequestCacheConfig::class.java).autowire()

        this.mockMvc.get("/test")

        this.mockMvc.perform(formLogin())
                .andExpect {
                    redirectedUrl("/")
                }
    }

    @EnableWebSecurity
    class CustomRequestCacheConfig : WebSecurityConfigurerAdapter() {
        override fun configure(http: HttpSecurity) {
            http {
                requestCache {
                    requestCache = NullRequestCache()
                }
                formLogin { }
            }
        }
    }
}
