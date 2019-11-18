/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.security.dsl.config.builders.test

import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
import org.springframework.mock.web.MockServletConfig
import org.springframework.mock.web.MockServletContext
import org.springframework.security.config.BeanIds.SPRING_SECURITY_FILTER_CHAIN
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.RequestPostProcessor
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.MockMvcConfigurer
import org.springframework.web.context.ConfigurableWebApplicationContext
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext
import java.io.Closeable
import java.util.*
import javax.servlet.Filter

/**
 * @author Rob Winch
 * @since 5.0
 */
open class SpringTestContext : Closeable {
    private var test: Any? = null

    private var context: ConfigurableWebApplicationContext? = null

    private val filters = ArrayList<Filter>()

    fun setTest(test: Any) {
        this.test = test
    }

    override fun close() {
        try {
            this.context!!.close()
        } catch (e: Exception) {
        }

    }

    fun register(vararg classes: Class<*>): SpringTestContext {
        val applicationContext = AnnotationConfigWebApplicationContext()
        applicationContext.register(*classes)
        this.context = applicationContext
        return this
    }

    fun autowire() {
        this.context!!.servletContext = MockServletContext()
        this.context!!.servletConfig = MockServletConfig()
        this.context!!.refresh()

        if (this.context!!.containsBean(SPRING_SECURITY_FILTER_CHAIN)) {
            val mockMvc = MockMvcBuilders.webAppContextSetup(this.context!!)
                    .apply<DefaultMockMvcBuilder>(springSecurity())
                    .apply<DefaultMockMvcBuilder>(AddFilter()).build()
            this.context!!.beanFactory
                    .registerResolvableDependency(MockMvc::class.java, mockMvc)
        }

        val bpp = AutowiredAnnotationBeanPostProcessor()
        bpp.setBeanFactory(this.context!!.beanFactory)
        bpp.processInjection(this.test!!)
    }

    fun getContext(): ConfigurableWebApplicationContext {
        if (!this.context!!.isRunning()) {
            this.context!!.refresh()
        }
        return this.context!!
    }

    private inner class AddFilter : MockMvcConfigurer {
        override fun beforeMockMvcCreated(builder: ConfigurableMockMvcBuilder<*>, context: WebApplicationContext): RequestPostProcessor? {
            (builder as ConfigurableMockMvcBuilder<DefaultMockMvcBuilder>).addFilters<DefaultMockMvcBuilder>(*this@SpringTestContext.filters.toTypedArray())
            return null
        }
    }
}
