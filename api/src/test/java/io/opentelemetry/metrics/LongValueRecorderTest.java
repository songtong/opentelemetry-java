/*
 * Copyright 2019, OpenTelemetry Authors
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

package io.opentelemetry.metrics;

import static io.opentelemetry.metrics.DefaultMeter.ERROR_MESSAGE_INVALID_NAME;
import static java.util.Arrays.fill;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.common.Labels;
import io.opentelemetry.metrics.LongValueRecorder.BoundLongValueRecorder;
import org.junit.jupiter.api.Test;

/** Tests for {@link LongValueRecorder}. */
public final class LongValueRecorderTest {

  private static final String NAME = "name";
  private static final String DESCRIPTION = "description";
  private static final String UNIT = "1";
  private static final Labels CONSTANT_LABELS = Labels.of("key", "value");

  private final Meter meter = OpenTelemetry.getMeter("LongValueRecorderTest");

  @Test
  void preventNull_Name() {
    assertThrows(NullPointerException.class, () -> meter.longValueRecorderBuilder(null), "name");
  }

  @Test
  void preventEmpty_Name() {
    assertThrows(
        IllegalArgumentException.class,
        () -> meter.longValueRecorderBuilder("").build(),
        ERROR_MESSAGE_INVALID_NAME);
  }

  @Test
  void preventNonPrintableMeasureName() {
    assertThrows(
        IllegalArgumentException.class,
        () -> meter.longValueRecorderBuilder("\2").build(),
        ERROR_MESSAGE_INVALID_NAME);
  }

  @Test
  void preventTooLongName() {
    char[] chars = new char[256];
    fill(chars, 'a');
    String longName = String.valueOf(chars);
    assertThrows(
        IllegalArgumentException.class,
        () -> meter.longValueRecorderBuilder(longName).build(),
        ERROR_MESSAGE_INVALID_NAME);
  }

  @Test
  void preventNull_Description() {
    assertThrows(
        NullPointerException.class,
        () -> meter.longValueRecorderBuilder("metric").setDescription(null).build(),
        "description");
  }

  @Test
  void preventNull_Unit() {
    assertThrows(
        NullPointerException.class,
        () -> meter.longValueRecorderBuilder("metric").setUnit(null).build(),
        "unit");
  }

  @Test
  void preventNull_ConstantLabels() {
    assertThrows(
        NullPointerException.class,
        () -> meter.longValueRecorderBuilder("metric").setConstantLabels(null).build(),
        "constantLabels");
  }

  @Test
  void record_PreventNullLabels() {
    assertThrows(
        NullPointerException.class,
        () -> meter.longValueRecorderBuilder("metric").build().record(1, null),
        "labels");
  }

  @Test
  void record_DoesNotThrow() {
    LongValueRecorder longValueRecorder =
        meter
            .longValueRecorderBuilder(NAME)
            .setDescription(DESCRIPTION)
            .setUnit(UNIT)
            .setConstantLabels(CONSTANT_LABELS)
            .build();
    longValueRecorder.record(5, Labels.empty());
    longValueRecorder.record(-5, Labels.empty());
    longValueRecorder.record(5);
    longValueRecorder.record(-5);
  }

  @Test
  void bound_PreventNullLabels() {
    assertThrows(
        NullPointerException.class,
        () -> meter.longValueRecorderBuilder("metric").build().bind(null),
        "labels");
  }

  @Test
  void bound_DoesNotThrow() {
    LongValueRecorder longValueRecorder =
        meter
            .longValueRecorderBuilder(NAME)
            .setDescription(DESCRIPTION)
            .setUnit(UNIT)
            .setConstantLabels(CONSTANT_LABELS)
            .build();
    BoundLongValueRecorder bound = longValueRecorder.bind(Labels.empty());
    bound.record(5);
    bound.record(-5);
    bound.unbind();
  }
}
