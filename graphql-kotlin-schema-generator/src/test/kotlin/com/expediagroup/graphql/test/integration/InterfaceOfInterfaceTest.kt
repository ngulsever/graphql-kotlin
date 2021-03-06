/*
 * Copyright 2019 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.test.integration

import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.testSchemaConfig
import com.expediagroup.graphql.toSchema
import graphql.schema.GraphQLInterfaceType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class InterfaceOfInterfaceTest {

    @Test
    fun `interface of interface`() {
        val queries = listOf(TopLevelObject(InterfaceOfInterfaceQuery()))
        val schema = toSchema(queries = queries, config = testSchemaConfig)
        assertEquals(expected = 1, actual = schema.queryType.fieldDefinitions.size)

        val implementation = schema.getObjectType("MyClass")
        assertNotNull(implementation)
        val interfaces = implementation.interfaces
        assertEquals(2, interfaces.size)
        assertNotNull(interfaces.firstOrNull { it.name == "FirstLevel" })
        assertNotNull(interfaces.firstOrNull { it.name == "SecondLevel" })

        val secondLevelInterface = schema.getType("SecondLevel") as? GraphQLInterfaceType
        assertNotNull(secondLevelInterface)
        val interfaceOfInterface = secondLevelInterface.interfaces
        assertEquals(1, interfaceOfInterface.size)
        assertNotNull(interfaceOfInterface.firstOrNull { it.name == "FirstLevel" })

        val firstLevelInterface = schema.getType("FirstLevel") as? GraphQLInterfaceType
        assertNotNull(firstLevelInterface)
    }

    interface FirstLevel {
        val id: String
    }

    interface SecondLevel : FirstLevel {
        val name: String
    }

    class MyClass(override val id: String, override val name: String) : SecondLevel

    class InterfaceOfInterfaceQuery {
        fun getClass() = MyClass(id = "1", name = "fooBar")
    }
}
