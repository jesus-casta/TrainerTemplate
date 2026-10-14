package es.trainertemplate;

import static org.junit.jupiter.api.Assertions.*;

import es.trainertemplate.Model.*;
import java.math.*;
import java.util.*;
import org.junit.jupiter.api.Test;

class LoadCalculatorTest {
  final LoadCalculator calc = new LoadCalculator();

  SetPlan set(String mode, String value, Integer reference) {
    return new SetPlan(
        "bench",
        5,
        null,
        false,
        new Load(mode, new BigDecimal(value), reference, new BigDecimal("2.5")));
  }

  @Test
  void backoffsUseActualTopSetAndRound() {
    var sets = List.of(set("FIXED", "100", null), set("SET_PERCENT", "90", 0));
    var values =
        calc.calculate(
            sets,
            List.of(new Actual(0, 5, new BigDecimal("102.5"), new BigDecimal("8"))),
            null,
            BigDecimal.ZERO);
    assertEquals(0, new BigDecimal("92.5").compareTo(values.get(1)));
  }

  @Test
  void rmAndRoundProgression() {
    var values =
        calc.calculate(
            List.of(set("RM_PERCENT", "85", null)),
            List.of(),
            new BigDecimal("200"),
            new BigDecimal("10"));
    assertEquals(0, new BigDecimal("177.5").compareTo(values.get(0)));
  }

  @Test
  void preservesRecordedBackoff() {
    var sets = List.of(set("FIXED", "100", null), set("SET_PERCENT", "90", 0));
    var result =
        calc.calculate(
            sets,
            List.of(new Actual(1, 5, new BigDecimal("87.5"), null)),
            null,
            new BigDecimal("5"));
    assertEquals(0, new BigDecimal("87.5").compareTo(result.get(1)));
  }

  @Test
  void rejectsCircularRules() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            calc.calculate(
                List.of(set("SET_PERCENT", "90", 1), set("SET_PERCENT", "90", 0)),
                List.of(),
                null,
                BigDecimal.ZERO));
  }

  @Test
  void supportsOffsets() {
    assertEquals(
        0,
        new BigDecimal("95")
            .compareTo(
                calc.calculate(
                        List.of(set("FIXED", "100", null), set("SET_OFFSET", "-5", 0)),
                        List.of(),
                        null,
                        BigDecimal.ZERO)
                    .get(1)));
  }

  @Test
  void supportsPerExerciseMax() {
    var plan =
        new SetPlan(
            "squat",
            5,
            null,
            false,
            new Load(
                "RM_PERCENT",
                new BigDecimal("85"),
                null,
                new BigDecimal("2.5"),
                new BigDecimal("200")));
    assertEquals(
        0,
        new BigDecimal("170")
            .compareTo(
                calc.calculate(List.of(plan), List.of(), new BigDecimal("100"), BigDecimal.ZERO)
                    .get(0)));
  }

  @Test
  void rejectsDuplicateActual() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            calc.calculate(
                List.of(set("FIXED", "100", null)),
                List.of(
                    new Actual(0, 5, BigDecimal.TEN, null), new Actual(0, 5, BigDecimal.TEN, null)),
                null,
                BigDecimal.ZERO));
  }
}
