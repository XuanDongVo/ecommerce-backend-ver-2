package xuandong.ecommerce_ver_2.service.jwt;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import xuandong.ecommerce_ver_2.config.JwtConfiguration;
import xuandong.ecommerce_ver_2.entity.User;
import xuandong.ecommerce_ver_2.repository.UserRespository;

@Service
public class JwtService {
	public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS256;
	public static final String AUTHORITY = "AUTHORITY";
	@Value("${jwt.access-token-seconds}")
	private Long accessTokenExpiration;
	@Value("${jwt.refresh-token-seconds}")
	private Long refreshTokenExpiration;
	@Autowired
	private JwtEncoder jwtEncoder;
	@Autowired
	private JwtConfiguration jwtConfiguration;

	public String createAccessToken(Authentication authentication) {
		Instant now = Instant.now();
		Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);

		String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.joining(","));

		JwtClaimsSet claimsSet = JwtClaimsSet.builder().issuedAt(now).expiresAt(validity)
				.subject(authentication.getName()).claim(AUTHORITY, authorities).build();

		JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();

		String token = this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claimsSet)).getTokenValue();
		return token;
	}

	public String createRefreshToken(Authentication authentication) {
		Instant now = Instant.now();
		Instant validity = now.plus(this.refreshTokenExpiration, ChronoUnit.SECONDS);

		JwtClaimsSet claimsSet = JwtClaimsSet.builder().issuedAt(now).expiresAt(validity)
				.subject(authentication.getName()).build();

		JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();

		String token = this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claimsSet)).getTokenValue();
		return token;
	}

	public Jwt checkValidToken(String token) {
		Jwt jwt = jwtConfiguration.jwtDecoder().decode(token);
		return jwt;
	}

	public Long getAccessTokenExpiration() {
		return accessTokenExpiration;
	}

	public void setAccessTokenExpiration(Long accessTokenExpiration) {
		this.accessTokenExpiration = accessTokenExpiration;
	}

	public Long getRefreshTokenExpiration() {
		return refreshTokenExpiration;
	}

	public void setRefreshTokenExpiration(Long refreshTokenExpiration) {
		this.refreshTokenExpiration = refreshTokenExpiration;
	}

}
