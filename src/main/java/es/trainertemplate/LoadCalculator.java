package es.trainertemplate;
import java.math.*;import java.util.*;
import org.springframework.stereotype.Service;
import es.trainertemplate.Model.*;
@Service public class LoadCalculator {
 public List<BigDecimal> calculate(List<SetPlan> sets,List<Actual> actual,BigDecimal max,BigDecimal progression){
  Map<Integer,Actual> done=new HashMap<>();for(var a:actual){if(a.index()>=sets.size()||done.put(a.index(),a)!=null)throw new IllegalArgumentException("Índice de serie inválido o duplicado");}
  List<BigDecimal> out=new ArrayList<>(Collections.nCopies(sets.size(),null));for(int i=0;i<sets.size();i++)resolve(i,sets,done,max,progression,out,new HashSet<>());return out;
 }
 private BigDecimal resolve(int i,List<SetPlan> sets,Map<Integer,Actual> done,BigDecimal max,BigDecimal progression,List<BigDecimal> out,Set<Integer> visiting){
  if(out.get(i)!=null)return out.get(i);if(!visiting.add(i))throw new IllegalArgumentException("Dependencia circular entre series");
  var rule=sets.get(i).load();BigDecimal result;
  if(done.containsKey(i))result=done.get(i).weight();
  else result=switch(rule.mode()){
   case "FIXED" -> required(rule.value()).add(progression);
   case "RM_PERCENT" -> required(rule.referenceMax()!=null?rule.referenceMax():max).add(progression).multiply(required(rule.value())).divide(new BigDecimal("100"));
   case "SET_PERCENT","SET_OFFSET" -> {Integer ref=rule.reference();if(ref==null||ref<0||ref>=sets.size()||ref==i)throw new IllegalArgumentException("Referencia de serie inválida");
    if(!sets.get(i).exerciseId().equals(sets.get(ref).exerciseId()))throw new IllegalArgumentException("Las series vinculadas deben pertenecer al mismo ejercicio");
    BigDecimal source=resolve(ref,sets,done,max,progression,out,visiting);yield rule.mode().equals("SET_PERCENT")?source.multiply(required(rule.value())).divide(new BigDecimal("100")):source.add(required(rule.value()));}
   default -> throw new IllegalArgumentException("Modo de carga desconocido");};
  if(result.signum()<0)throw new IllegalArgumentException("La carga calculada es negativa");
  if(!done.containsKey(i)&&rule.increment()!=null)result=result.divide(rule.increment(),0,RoundingMode.HALF_UP).multiply(rule.increment());
  visiting.remove(i);out.set(i,result);return result;
 }
 private BigDecimal required(BigDecimal n){if(n==null)throw new IllegalArgumentException("Falta un valor de referencia");return n;}
}
