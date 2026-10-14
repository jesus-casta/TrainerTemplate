package es.trainertemplate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
  @Bean
  UserDetailsService users(
      @Value("${trainer.username:trainer}") String user,
      @Value("${trainer.password:}") String password) {
    if (password.isBlank())
      throw new IllegalStateException("Configura TRAINER_PASSWORD para iniciar la aplicación");
    return new InMemoryUserDetailsManager(
        User.withUsername(user)
            .password("{bcrypt}" + new BCryptPasswordEncoder().encode(password))
            .roles("USER")
            .build());
  }

  @Bean
  SecurityFilterChain security(HttpSecurity http) throws Exception {
    return http.authorizeHttpRequests(
            a ->
                a.requestMatchers("/", "/index.html", "/app.js", "/style.css")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .httpBasic(Customizer.withDefaults())
        .build();
  }
}
