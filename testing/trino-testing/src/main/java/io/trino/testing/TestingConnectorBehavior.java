/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.trino.testing;

import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

public enum TestingConnectorBehavior
{
    SUPPORTS_PREDICATE_PUSHDOWN,
    SUPPORTS_PREDICATE_PUSHDOWN_WITH_VARCHAR_EQUALITY(SUPPORTS_PREDICATE_PUSHDOWN),
    SUPPORTS_PREDICATE_PUSHDOWN_WITH_VARCHAR_INEQUALITY(SUPPORTS_PREDICATE_PUSHDOWN),

    SUPPORTS_LIMIT_PUSHDOWN,

    SUPPORTS_TOPN_PUSHDOWN,

    SUPPORTS_AGGREGATION_PUSHDOWN,

    SUPPORTS_JOIN_PUSHDOWN(
            // Currently no connector supports Join pushdown by default. JDBC connectors may support Join pushdown and BaseJdbcConnectorTest
            // verifies truthfulness of SUPPORTS_JOIN_PUSHDOWN declaration, so it is a safe default.
            false),
    SUPPORTS_JOIN_PUSHDOWN_WITH_FULL_JOIN(SUPPORTS_JOIN_PUSHDOWN),
    SUPPORTS_JOIN_PUSHDOWN_WITH_DISTINCT_FROM(SUPPORTS_JOIN_PUSHDOWN),

    SUPPORTS_CREATE_TABLE,
    SUPPORTS_CREATE_TABLE_WITH_DATA(SUPPORTS_CREATE_TABLE),

    SUPPORTS_INSERT,

    SUPPORTS_DELETE(false),
    SUPPORTS_ROW_LEVEL_DELETE(SUPPORTS_DELETE),

    SUPPORTS_CREATE_SCHEMA,

    /**/;

    private final Predicate<Predicate<TestingConnectorBehavior>> hasBehaviorByDefault;

    TestingConnectorBehavior()
    {
        this(true);
    }

    TestingConnectorBehavior(boolean hasBehaviorByDefault)
    {
        this(fallback -> hasBehaviorByDefault);
    }

    TestingConnectorBehavior(TestingConnectorBehavior defaultBehaviorSource)
    {
        this(fallback -> fallback.test(defaultBehaviorSource));
    }

    TestingConnectorBehavior(Predicate<Predicate<TestingConnectorBehavior>> hasBehaviorByDefault)
    {
        this.hasBehaviorByDefault = requireNonNull(hasBehaviorByDefault, "hasBehaviorByDefault is null");
    }

    /**
     * Determines whether a connector should be assumed to have this behavior.
     *
     * @param fallback callback to be used to inspect connector behaviors, if this behavior is assumed to be had conditionally
     */
    // Intentionally package private. This is just a convenience method for sharing behavior between reusable test base classes.
    // If it was to be public, a different API would need to be provided.
    boolean hasBehaviorByDefault(Predicate<TestingConnectorBehavior> fallback)
    {
        return hasBehaviorByDefault.test(fallback);
    }
}
