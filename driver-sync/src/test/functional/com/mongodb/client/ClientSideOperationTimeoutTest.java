/*
 * Copyright 2008-present MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mongodb.client;

import com.mongodb.client.unified.UnifiedSyncTest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Collection;

import static org.junit.jupiter.api.Assumptions.assumeFalse;


// See https://github.com/mongodb/specifications/tree/master/source/client-side-operation-timeout/tests
public class ClientSideOperationTimeoutTest extends UnifiedSyncTest {

    private static Collection<Arguments> data() {
        return getTestData("client-side-operations-timeout");
    }

    @Override
    protected void skips(final String fileDescription, final String testDescription) {
        skipOperationTimeoutTests(fileDescription, testDescription);

        /*
         * The test is occasionally racy. Sometimes multiple getMores can be triggered.
         */
        ignoreExtraCommandEvents(testDescription.contains("timeoutMS is refreshed for getMore if maxAwaitTimeMS is set"));
    }

    public static void skipOperationTimeoutTests(final String fileDescription, final String testDescription) {
        assumeFalse(testDescription.contains("maxTimeMS is ignored if timeoutMS is set - createIndex on collection"),
                "No maxTimeMS parameter for createIndex() method");
        assumeFalse(fileDescription.startsWith("runCursorCommand"), "No run cursor command");
        assumeFalse(testDescription.contains("runCommand on database"), "No special handling of runCommand");
        assumeFalse(testDescription.endsWith("count on collection"), "No count command helper");
        assumeFalse(fileDescription.equals("timeoutMS can be overridden for an operation"), "No operation based overrides");
        assumeFalse(testDescription.equals("timeoutMS can be overridden for commitTransaction")
                || testDescription.equals("timeoutMS applied to abortTransaction"),
                "No operation session based overrides");

        assumeFalse(fileDescription.equals("operations ignore deprecated timeout options if timeoutMS is set")
                && (testDescription.startsWith("abortTransaction ignores") || testDescription.startsWith("commitTransaction ignores")),
                "No operation session based overrides");

        assumeFalse(fileDescription.equals("timeoutMS behaves correctly when closing cursors")
                && testDescription.equals("timeoutMS can be overridden for close"), "No operation based overrides");
    }
}
