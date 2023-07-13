/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.paimon.flink.action;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.MultipleParameterTool;

import java.util.Map;
import java.util.Optional;

/** Factory to create {@link ExpireAction}. */
public class ExpireActionFactory implements ActionFactory {

    public static final String IDENTIFIER = "expire";

    @Override
    public String identifier() {
        return IDENTIFIER;
    }

    @Override
    public Optional<Action> create(MultipleParameterTool params) {
        checkRequiredArgument(params, "num-retained-min");
        checkRequiredArgument(params, "num-retained-max");
        checkRequiredArgument(params, "millis-retained");

        Tuple3<String, String, String> tablePath = getTablePath(params);
        Map<String, String> catalogConfig = optionalConfigMap(params, "catalog-conf");
        int retainedMin = Integer.parseInt(params.get("num-retained-min"));
        int retainedMax = Integer.parseInt(params.get("num-retained-max"));
        long millis = Long.parseLong(params.get("millis-retained"));

        ExpireAction action =
                new ExpireAction(
                        tablePath.f0,
                        tablePath.f1,
                        tablePath.f2,
                        retainedMin,
                        retainedMax,
                        millis,
                        catalogConfig);

        return Optional.of(action);
    }

    @Override
    public void printHelp() {
        System.out.println(
                "Action \"expire\" keep at least one snapshot so that users will not accidentally clear all snapshots.");
        System.out.println();

        System.out.println("Syntax:");
        System.out.println(
                "  expire --warehouse <warehouse-path> --database <database-name> "
                        + "--table <table-name> --num-retained-min <num-retained-min>"
                        + "--num-retained-max <num-retained-max> --millis-retained <millis-retained>");
        System.out.println();
    }
}
