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

import com.google.gson.JsonPrimitive;

/**
 * Represents a ByteSize token like 10KB, 2MB, 5GB, etc.
 */
public class ByteSize implements Token {
  private final String value;
  private final long bytes;

  public ByteSize(String value) {
    this.value = value;
    this.bytes = parse(value);
  }

  private long parse(String value) {
    String trimmed = value.trim().toUpperCase();

    if (trimmed.endsWith("KB")) {
      return (long) (Double.parseDouble(trimmed.replace("KB", "")) * 1024);
    } else if (trimmed.endsWith("MB")) {
      return (long) (Double.parseDouble(trimmed.replace("MB", "")) * 1024 * 1024);
    } else if (trimmed.endsWith("GB")) {
      return (long) (Double.parseDouble(trimmed.replace("GB", "")) * 1024 * 1024 * 1024);
    } else if (trimmed.endsWith("TB")) {
      return (long) (Double.parseDouble(trimmed.replace("TB", "")) * 1024L * 1024 * 1024 * 1024);
    } else if (trimmed.endsWith("B")) {
      return (long) (Double.parseDouble(trimmed.replace("B", "")));
    } else {
      // Default fallback: assume value is in bytes
      return (long) (Double.parseDouble(trimmed));
    }
  }

  public long getBytes() {
    return bytes;
  }

  @Override
  public String value() {
    return value;
  }

  @Override
  public TokenType type() {
    return TokenType.BYTE_SIZE;
  }

  @Override
  public JsonPrimitive toJson() {
    return new JsonPrimitive(bytes);
  }

  @Override
  public String toString() {
    return value + " (" + bytes + " bytes)";
  }
}
