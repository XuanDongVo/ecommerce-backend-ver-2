package xuandong.ecommerce_ver_2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import xuandong.ecommerce_ver_2.exception.accessDeniedHandler.CustomAccessDeniedHandler;

@Configuration
public class SecurityConfiguration {

	@Autowired
	private JwtAuthenticationConfiguration jwtAuthenticationConfiguration;
	@Autowired
	private CustomAccessDeniedHandler customAccessDeniedHandler;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity res) throws Exception {
		res.cors(cors -> cors.disable()).csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(
						authz -> authz.requestMatchers("/", "/auth/**" , "/storage/**").permitAll().anyRequest().permitAll()
									)
				.exceptionHandling(exception -> exception.accessDeniedHandler(customAccessDeniedHandler)) 
				.oauth2ResourceServer(oauth2 -> oauth2
						.jwt(jwt -> jwt.jwtAuthenticationConverter(
								jwtAuthenticationConfiguration.jwtAuthenticationConverter()))
						.jwt(Customizer.withDefaults()))

				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.formLogin(login -> login.disable());

		return res.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

}
