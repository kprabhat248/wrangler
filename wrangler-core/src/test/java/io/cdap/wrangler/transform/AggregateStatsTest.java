/*
 * Copyright © 2025 Cask Data, Inc.
 *
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

package io.cdap.wrangler.transform;

import io.cdap.wrangler.TestingRig;
import io.cdap.wrangler.api.Row;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for the AggregateStats directive.
 */
public class AggregateStatsTest {

  @Test
  public void testAggregateStatsTotalSizeAndTime() throws Exception {
    List<Row> input = Arrays.asList(
      new Row("data_transfer_size", "10KB").add("response_time", "100ms"),
      new Row("data_transfer_size", "1.5MB").add("response_time", "2s"),
      new Row("data_transfer_size", "500B").add("response_time", "300ms")
    );

    String[] recipe = new String[] {
      "aggregate-stats :data_transfer_size :response_time total_size_mb total_time_sec"
    };

    List<Row> results = TestingRig.execute(recipe, input);
    Assert.assertEquals(1, results.size());

    Row result = results.get(0);

    // Byte totals = 10KB + 1.5MB + 500B = (10240 + 1572864 + 500) = 1,584,604 bytes
    // In MB (using 1MB = 1024 * 1024)
    double expectedSizeMB = 1584604.0 / (1024 * 1024);

    // Time in milliseconds = 100 + 2000 + 300 = 2400 ms = 2.4 seconds
    double expectedTimeSec = 2.4;

    Assert.assertEquals(expectedSizeMB, (double) result.getValue("total_size_mb"), 0.001);
    Assert.assertEquals(expectedTimeSec, (double) result.getValue("total_time_sec"), 0.001);
  }

  @Test
  public void testAggregateStatsWithEmptyInput() throws Exception {
    List<Row> input = Arrays.asList();

    String[] recipe = new String[] {
      "aggregate-stats :data_transfer_size :response_time total_size_mb total_time_sec"
    };

    List<Row> results = TestingRig.execute(recipe, input);
    Assert.assertEquals(1, results.size());

    Row result = results.get(0);

    Assert.assertEquals(0.0, (double) result.getValue("total_size_mb"), 0.0001);
    Assert.assertEquals(0.0, (double) result.getValue("total_time_sec"), 0.0001);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAggregateStatsWithInvalidUnit() throws Exception {
    List<Row> input = Arrays.asList(
      new Row("data_transfer_size", "1xy").add("response_time", "2s")
    );

    String[] recipe = new String[] {
      "aggregate-stats :data_transfer_size :response_time total_size_mb total_time_sec"
    };

    // Should throw due to "1xy" being an invalid ByteSize
    TestingRig.execute(recipe, input);
  }
}
