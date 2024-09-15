package xuandong.ecommerce_ver_2.config;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;

import xuandong.ecommerce_ver_2.service.jwt.JwtService;

@Configuration
public class JwtConfiguration {
	@Value("${jwt.base64-secret}")
	private String jwtKey;

	@Bean
	public JwtEncoder jwtEncoder() {
		return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
	}

	public SecretKey getSecretKey() {
		byte[] keyBytes = Base64.encode(jwtKey).decode();
		return new SecretKeySpec(keyBytes, 0, keyBytes.length, JwtService.JWT_ALGORITHM.getName());
	}

	@Bean
	public JwtDecoder jwtDecoder() {
		NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey())
				.macAlgorithm(JwtService.JWT_ALGORITHM).build();
		return token -> {
			try {
				return jwtDecoder.decode(token);
			} catch (Exception e) {
				System.out.println("jwt error:" + e.getMessage());
				throw e;
			}
		};
	}

}
