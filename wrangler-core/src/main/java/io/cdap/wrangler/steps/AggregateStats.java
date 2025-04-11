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


package io.cdap.wrangler.steps;

import io.cdap.wrangler.api.Arguments;
import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.parser.UsageDefinition;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.TokenType;
import io.cdap.wrangler.api.parser.TimeDuration;


import java.util.ArrayList;
import java.util.List;

/**
 * Aggregates byte sizes and time durations across rows.
 */
public class AggregateStats implements Directive {

  private String sizeColumn;
  private String timeColumn;
  private String outputSizeColumn;
  private String outputTimeColumn;

  private long totalBytes = 0L;
  private long totalMillis = 0L;

  private boolean isFinalized = false;

  @Override
  public UsageDefinition define() {
    UsageDefinition.Builder builder = UsageDefinition.builder("aggregate-stats");
    builder.define("size_column", TokenType.COLUMN_NAME);
    builder.define("time_column", TokenType.COLUMN_NAME);
    builder.define("output_size_column", TokenType.COLUMN_NAME);
    builder.define("output_time_column", TokenType.COLUMN_NAME);
    return builder.build();
  }



  @Override
  public void initialize(Arguments args) {
    sizeColumn = args.value("size_column");
    timeColumn = args.value("time_column");
    outputSizeColumn = args.value("output_size_column");
    outputTimeColumn = args.value("output_time_column");
  }

  @Override
  public List<Row> execute(List<Row> rows, ExecutorContext context) {
    if (!isFinalized) {
      for (Row row : rows) {
        Object sizeVal = row.getValue(sizeColumn);
        Object timeVal = row.getValue(timeColumn);

        if (sizeVal != null && timeVal != null) {
          try {
            ByteSize byteSize = new ByteSize(sizeVal.toString());
            TimeDuration timeDuration = new TimeDuration(timeVal.toString());

            totalBytes += byteSize.getBytes();
            totalMillis += timeDuration.getMilliseconds();
          } catch (Exception e) {
            // Optionally log and skip invalid values
          }
        }
      }
      isFinalized = true;
    }

    List<Row> result = new ArrayList<>();
    Row output = new Row();

    double totalMB = totalBytes / (1024.0 * 1024.0);
    double totalSeconds = totalMillis / 1000.0;

    output.add(outputSizeColumn, totalMB);
    output.add(outputTimeColumn, totalSeconds);
    result.add(output);

    return result;
  }

  @Override
  public void destroy() {
    // Nothing to clean up
  }
}
