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

package org.springframework.security.dsl.test

import org.junit.rules.MethodRule
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.Statement
import org.springframework.security.test.context.TestSecurityContextHolder

/**
 * @author Rob Winch
 */
class SpringTestRule : SpringTestContext(), MethodRule {
    override fun apply(base: Statement, method: FrameworkMethod,
                       target: Any): Statement {
        return object : Statement() {
            override fun evaluate() {
                setTest(target)
                try {
                    base.evaluate()
                } finally {
                    TestSecurityContextHolder.clearContext()
                    close()
                }
            }
        }
    }
}
