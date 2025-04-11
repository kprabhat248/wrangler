/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package io.cdap.wrangler;

import org.junit.Assert;
import org.junit.Test;

import io.cdap.wrangler.api.parser.ByteSize;

public class ByteSizeTest {

  @Test
  public void testParsing() {
    Assert.assertEquals(10240, new ByteSize("10KB").getBytes());
    Assert.assertEquals(1572864, new ByteSize("1.5MB").getBytes());
    Assert.assertEquals(10737418240L, new ByteSize("10GB").getBytes());
    Assert.assertEquals(123, new ByteSize("123B").getBytes());
    Assert.assertEquals(2048, new ByteSize("2048").getBytes()); // Default to bytes
  }
}