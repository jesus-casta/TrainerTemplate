package es.trainertemplate;
import org.junit.jupiter.api.Test;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;import org.springframework.boot.test.context.SpringBootTest;import org.springframework.test.web.servlet.MockMvc;import com.fasterxml.jackson.databind.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
@SpringBootTest(properties={"trainer.password=test-password","spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1"}) @AutoConfigureMockMvc class ApiTest {
 @Autowired MockMvc mvc;@Autowired ObjectMapper json;
 @Test void requiresAuthentication()throws Exception{mvc.perform(get("/api/v1/entries")).andExpect(status().isUnauthorized());}
 @Test void loginWorksAndCsrfRequired()throws Exception{mvc.perform(get("/api/v1/csrf").with(httpBasic("trainer","test-password"))).andExpect(status().isOk()).andExpect(jsonPath("$.token").isNotEmpty());mvc.perform(post("/api/v1/entries").with(httpBasic("trainer","test-password")).contentType("application/json").content("{}")).andExpect(status().isForbidden());}
 @Test void catalogIsSeeded()throws Exception{mvc.perform(get("/api/v1/entries").with(user("trainer"))).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(12));}
 @Test void ownershipAndVersionAreEnforced()throws Exception{
 String body="{\"kind\":\"exercise\",\"data\":{\"name\":\"Test\",\"pattern\":\"Push\",\"equipment\":\"Bar\",\"loadConvention\":\"TOTAL_KG\"}}";
 var result=mvc.perform(post("/api/v1/entries").with(user("isolated")).with(csrf()).contentType("application/json").content(body)).andExpect(status().isCreated()).andReturn();var saved=json.readTree(result.getResponse().getContentAsString());String id=saved.get("id").asText();
 mvc.perform(put("/api/v1/entries/"+id).with(user("other")).with(csrf()).contentType("application/json").content(body)).andExpect(status().isNotFound());
 mvc.perform(put("/api/v1/entries/"+id).with(user("isolated")).with(csrf()).contentType("application/json").content(body)).andExpect(status().isConflict());
 }
 @Test void scheduleRecordAndRecalculate()throws Exception{
 String exercise="{\"kind\":\"exercise\",\"data\":{\"name\":\"Bench\",\"pattern\":\"Push\",\"equipment\":\"Bar\",\"loadConvention\":\"TOTAL_KG\"}}";
 String ex=json.readTree(mvc.perform(post("/api/v1/entries").with(user("flow")).with(csrf()).contentType("application/json").content(exercise)).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()).get("id").asText();
 String sets="[{\"exerciseId\":\""+ex+"\",\"reps\":5,\"warmup\":false,\"load\":{\"mode\":\"FIXED\",\"value\":100,\"increment\":2.5}},{\"exerciseId\":\""+ex+"\",\"reps\":5,\"warmup\":false,\"load\":{\"mode\":\"SET_PERCENT\",\"value\":90,\"reference\":0,\"increment\":2.5}}]";
 String program="{\"kind\":\"program\",\"data\":{\"name\":\"Test\",\"referenceMax\":150,\"increasePerRound\":2.5,\"increasePerWeek\":0,\"startDate\":\"2026-10-14\",\"days\":[{\"name\":\"A\",\"sets\":"+sets+"}]}}";
 String pid=json.readTree(mvc.perform(post("/api/v1/entries").with(user("flow")).with(csrf()).contentType("application/json").content(program)).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()).get("id").asText();
 var session=json.readTree(mvc.perform(post("/api/v1/sessions").with(user("flow")).with(csrf()).contentType("application/json").content("{\"programId\":\""+pid+"\",\"dayIndex\":0,\"round\":2,\"date\":\"2026-10-16\"}")).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());
 String sid=session.get("id").asText();mvc.perform(get("/api/v1/sessions/"+sid+"/loads").with(user("flow"))).andExpect(status().isOk()).andExpect(jsonPath("$[1]").value(92.5));
 ((com.fasterxml.jackson.databind.node.ObjectNode)session.get("data")).set("actual",json.readTree("[{\"index\":0,\"reps\":5,\"weight\":105,\"rpe\":8}]"));
 mvc.perform(put("/api/v1/entries/"+sid).with(user("flow")).with(csrf()).contentType("application/json").content(session.toString())).andExpect(status().isOk());
 mvc.perform(get("/api/v1/sessions/"+sid+"/loads").with(user("flow"))).andExpect(jsonPath("$[1]").value(95.0));
 mvc.perform(get("/api/v1/metrics").with(user("flow"))).andExpect(jsonPath("$.sets").value(1)).andExpect(jsonPath("$.reps").value(5));
 }
}
