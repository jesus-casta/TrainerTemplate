package es.trainertemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.trainertemplate.Model.*;
import jakarta.validation.Validator;
import java.math.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class TrainerService {
  private final EntryRepository repo;
  private final ObjectMapper json;
  private final Validator validator;
  private final LoadCalculator calc;

  TrainerService(EntryRepository r, ObjectMapper j, Validator v, LoadCalculator c) {
    repo = r;
    json = j;
    validator = v;
    calc = c;
  }

  public List<Stored> list(String owner) {
    return repo.findByOwnerNameOrderByIdAsc(owner).stream().map(this::stored).toList();
  }

  public Entry find(String owner, String id) {
    return repo.findByIdAndOwnerName(id, owner)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro no encontrado"));
  }

  public Stored save(String owner, String id, Envelope request) {
    Entry e =
        id == null
            ? new Entry(UUID.randomUUID().toString(), owner, request.kind(), "")
            : find(owner, id);
    if (id != null && (request.version() == null || request.version() != e.version))
      throw new ResponseStatusException(
          HttpStatus.CONFLICT, "El registro ha cambiado. Recarga antes de editar.");
    if (!e.kind.equals(request.kind()))
      throw new IllegalArgumentException("No se puede cambiar el tipo de registro");
    Object typed =
        switch (request.kind()) {
          case "exercise" -> json.convertValue(request.data(), Exercise.class);
          case "program" -> json.convertValue(request.data(), Program.class);
          case "session" -> json.convertValue(request.data(), Session.class);
          default -> throw new IllegalArgumentException("Tipo desconocido");
        };
    var violations = validator.validate(typed);
    if (!violations.isEmpty())
      throw new IllegalArgumentException(
          violations.iterator().next().getPropertyPath()
              + ": "
              + violations.iterator().next().getMessage());
    if (typed instanceof Program p) {
      for (var d : p.days()) checkSets(owner, d.sets());
    }
    if (typed instanceof Session s) {
      if (!Set.of("PLANNED", "ACTIVE", "COMPLETED", "PARTIAL", "CANCELLED").contains(s.state()))
        throw new IllegalArgumentException("Estado no válido");
      program(owner, s.programId());
      checkSets(owner, s.plan());
      calc.calculate(s.plan(), s.actual(), null, BigDecimal.ZERO);
    }
    try {
      e.payload = json.writeValueAsString(typed);
    } catch (Exception ex) {
      throw new IllegalArgumentException("Contenido no válido");
    }
    repo.saveAndFlush(e);
    return stored(e);
  }

  private void checkSets(String owner, List<SetPlan> sets) {
    for (var s : sets)
      if (!find(owner, s.exerciseId()).kind.equals("exercise"))
        throw new IllegalArgumentException("Ejercicio inválido");
    calc.calculate(sets, List.of(), new BigDecimal("100"), BigDecimal.ZERO);
  }

  private Program program(String owner, String id) {
    var e = find(owner, id);
    if (!e.kind.equals("program")) throw new IllegalArgumentException("Programa inválido");
    try {
      return json.readValue(e.payload, Program.class);
    } catch (Exception ex) {
      throw new IllegalArgumentException("Programa no válido");
    }
  }

  public Stored schedule(String owner, Schedule request) {
    var p = program(owner, request.programId());
    if (request.dayIndex() >= p.days().size())
      throw new IllegalArgumentException("Sesión inexistente");
    long weeks = Math.max(0, ChronoUnit.DAYS.between(p.startDate(), request.date()) / 7);
    BigDecimal increase =
        p.increasePerRound()
            .multiply(BigDecimal.valueOf(request.round() - 1))
            .add(p.increasePerWeek().multiply(BigDecimal.valueOf(weeks)));
    var sets = p.days().get(request.dayIndex()).sets();
    var suggested = calc.calculate(sets, List.of(), p.referenceMax(), increase);
    List<SetPlan> snapshot = new ArrayList<>();
    for (int i = 0; i < sets.size(); i++) {
      var s = sets.get(i);
      var l = s.load();
      snapshot.add(
          new SetPlan(
              s.exerciseId(),
              s.reps(),
              s.targetRpe(),
              s.warmup(),
              (l.mode().equals("FIXED") || l.mode().equals("RM_PERCENT"))
                  ? new Load("FIXED", suggested.get(i), null, l.increment())
                  : l));
    }
    var session =
        new Session(
            request.programId(),
            request.dayIndex(),
            request.round(),
            request.date(),
            "PLANNED",
            snapshot,
            List.of(),
            "");
    return save(owner, null, new Envelope("session", json.convertValue(session, Map.class), null));
  }

  public List<BigDecimal> preview(String owner, String id) {
    try {
      Session s = json.readValue(find(owner, id).payload, Session.class);
      return calc.calculate(s.plan(), s.actual(), null, BigDecimal.ZERO);
    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
      throw new IllegalArgumentException("Sesión inválida");
    }
  }

  public Map<String, Object> metrics(String owner) {
    int sets = 0, reps = 0;
    BigDecimal volume = BigDecimal.ZERO, rpe = BigDecimal.ZERO;
    int ratings = 0;
    Map<String, Integer> patterns = new TreeMap<>();
    for (var e : list(owner))
      if (e.kind().equals("session")) {
        Session s = json.convertValue(e.data(), Session.class);
        for (var a : s.actual()) {
          if (s.plan().get(a.index()).warmup()) continue;
          sets++;
          reps += a.reps();
          volume = volume.add(a.weight().multiply(BigDecimal.valueOf(a.reps())));
          if (a.rpe() != null) {
            rpe = rpe.add(a.rpe());
            ratings++;
          }
          var exercise = find(owner, s.plan().get(a.index()).exerciseId());
          patterns.merge(stored(exercise).data().get("pattern").toString(), 1, Integer::sum);
        }
      }
    Map<String, Object> result = new LinkedHashMap<>();
    result.put("sets", sets);
    result.put("reps", reps);
    result.put("externalVolume", volume);
    result.put(
        "averageRpe",
        ratings == 0 ? null : rpe.divide(BigDecimal.valueOf(ratings), 1, RoundingMode.HALF_UP));
    result.put("patterns", patterns);
    return result;
  }

  private Stored stored(Entry e) {
    try {
      return new Stored(e.id, e.kind, e.version, json.readValue(e.payload, Map.class));
    } catch (Exception x) {
      throw new IllegalStateException(x);
    }
  }
}
