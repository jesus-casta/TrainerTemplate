package es.trainertemplate;
import es.trainertemplate.Model.*;import jakarta.validation.Valid;import java.security.Principal;import java.util.*;import org.springframework.web.bind.annotation.*;import org.springframework.security.web.csrf.CsrfToken;import org.springframework.http.*;import org.springframework.dao.OptimisticLockingFailureException;
@RestController @RequestMapping("/api/v1") public class ApiController {
 private final TrainerService service;ApiController(TrainerService s){service=s;}
 @GetMapping("/csrf") public Map<String,String> csrf(CsrfToken t,Principal p){return Map.of("token",t.getToken(),"header",t.getHeaderName(),"username",p.getName());}
 @GetMapping("/entries") public List<Stored> list(Principal p){return service.list(p.getName());}
 @PostMapping("/entries") @ResponseStatus(HttpStatus.CREATED) public Stored create(Principal p,@Valid @RequestBody Envelope body){return service.save(p.getName(),null,body);}
 @PutMapping("/entries/{id}") public Stored update(Principal p,@PathVariable String id,@Valid @RequestBody Envelope body){return service.save(p.getName(),id,body);}
 @PostMapping("/sessions") @ResponseStatus(HttpStatus.CREATED) public Stored schedule(Principal p,@Valid @RequestBody Schedule body){return service.schedule(p.getName(),body);}
 @GetMapping("/sessions/{id}/loads") public Object loads(Principal p,@PathVariable String id){return service.preview(p.getName(),id);}
 @GetMapping("/metrics") public Object metrics(Principal p){return service.metrics(p.getName());}
 @GetMapping(value="/export",produces="application/json") public ResponseEntity<?> export(Principal p){return ResponseEntity.ok().header("Content-Disposition","attachment; filename=trainer-export.json").body(Map.of("schemaVersion",1,"entries",service.list(p.getName())));}
 @ExceptionHandler(IllegalArgumentException.class) @ResponseStatus(HttpStatus.BAD_REQUEST) public Map<String,String> invalid(IllegalArgumentException e){return Map.of("message",e.getMessage()==null?"Datos no válidos":e.getMessage());}
 @ExceptionHandler(OptimisticLockingFailureException.class) @ResponseStatus(HttpStatus.CONFLICT) public Map<String,String> conflict(){return Map.of("message","Conflicto de edición. Recarga los datos.");}
}
