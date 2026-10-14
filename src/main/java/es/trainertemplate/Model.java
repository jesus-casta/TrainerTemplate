package es.trainertemplate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

public final class Model {
  public record Exercise(
      @NotBlank @Size(max = 120) String name,
      @NotBlank String pattern,
      @NotBlank String equipment,
      @NotBlank String loadConvention,
      List<String> muscles,
      String notes) {}

  public record Load(
      @NotBlank String mode,
      BigDecimal value,
      Integer reference,
      @Positive BigDecimal increment,
      @Positive BigDecimal referenceMax) {
    public Load(String mode, BigDecimal value, Integer reference, BigDecimal increment) {
      this(mode, value, reference, increment, null);
    }
  }

  public record SetPlan(
      @NotBlank String exerciseId,
      @Min(1) int reps,
      @DecimalMin("0") @DecimalMax("10") BigDecimal targetRpe,
      boolean warmup,
      @NotNull @Valid Load load) {}

  public record Day(@NotBlank String name, @NotEmpty List<@Valid SetPlan> sets) {}

  public record Program(
      @NotBlank String name,
      @NotEmpty List<@Valid Day> days,
      @PositiveOrZero BigDecimal referenceMax,
      @NotNull @PositiveOrZero BigDecimal increasePerRound,
      @NotNull @PositiveOrZero BigDecimal increasePerWeek,
      @NotNull LocalDate startDate) {}

  public record Session(
      @NotBlank String programId,
      @Min(0) int dayIndex,
      @Min(1) int round,
      @NotNull LocalDate date,
      @NotBlank String state,
      @NotEmpty List<@Valid SetPlan> plan,
      @NotNull List<@Valid Actual> actual,
      String notes) {}

  public record Actual(
      @Min(0) int index,
      @Min(0) int reps,
      @NotNull @PositiveOrZero BigDecimal weight,
      @DecimalMin("0") @DecimalMax("10") BigDecimal rpe) {}

  public record Envelope(@NotBlank String kind, @NotNull Map<String, Object> data, Long version) {}

  public record Stored(String id, String kind, long version, Map<String, Object> data) {}

  public record Schedule(
      @NotBlank String programId,
      @Min(0) int dayIndex,
      @Min(1) int round,
      @NotNull LocalDate date) {}

  private Model() {}
}
