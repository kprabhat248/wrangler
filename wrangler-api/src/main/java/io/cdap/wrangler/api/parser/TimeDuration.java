/*
 * Copyright © 2024 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */



package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class TimeDuration implements Token {
    private final String value;
    private final double milliseconds;

    public TimeDuration(String value) {
        this.value = value;
        this.milliseconds = parse(value);
    }

    private double parse(String value) {
        value = value.toLowerCase().trim();
        if (value.endsWith("ms")) return Double.parseDouble(value.replace("ms", ""));
        if (value.endsWith("s")) return Double.parseDouble(value.replace("s", "")) * 1000;
        if (value.endsWith("m")) return Double.parseDouble(value.replace("m", "")) * 60 * 1000;
        if (value.endsWith("h")) return Double.parseDouble(value.replace("h", "")) * 60 * 60 * 1000;
        return Double.parseDouble(value);
    }

    public long getMilliseconds() {
        return (long) milliseconds;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(value);
    }

    @Override
    public TokenType type() {
        return TokenType.TIME_DURATION; // Make sure you have this in your TokenType enum
    }

    @Override
    public String toString() {
        return value;
    }
}
