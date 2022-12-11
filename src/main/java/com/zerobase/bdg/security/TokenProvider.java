package com.zerobase.bdg.security;

import com.zerobase.bdg.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class TokenProvider implements InitializingBean {

	private static final String KEY_ROLES = "roles";
	private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60; // 1 hour

	private final MemberService memberService;
	private final String secret;
	private Key key;

	public TokenProvider(@Value("${spring.jwt.secret}") String secret, MemberService memberService) {
		this.secret = secret;
		this.memberService = memberService;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		byte[] keyBytes = Decoders.BASE64.decode(secret);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	//토큰 생성
	public String genereateToken(String username, List<String> roles) {
		Claims claims = Jwts.claims().setSubject(username);
		claims.put(KEY_ROLES, roles);

		var now = new Date();
		var expiredDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

		return Jwts.builder()
					.setClaims(claims)
					.setIssuedAt(now)
					.setExpiration(expiredDate)
					.signWith(key,SignatureAlgorithm.HS512) // 사용할 암호화 알고리즘, 비밀키
					.compact();
	}

	// 유저를 토큰에서 찾음
	public String getUsername(String token){
		return this.pareseClaims(token).getSubject();
	}

	// 기한이 지난 토큰인지 유효성 확인
	public boolean validateToken(String token){
		if(!StringUtils.hasText(token)){
			return false;
		}

		var claims = this.pareseClaims(token);
		return !claims.getExpiration().before(new Date()); // 현재 시간보다 이전인지 아닌지 before()메서드로 확인 (boolean으로 반환)
	}

	private Claims pareseClaims(String token){
		return Jwts.parserBuilder().setSigningKey(key)
				.build().parseClaimsJws(token).getBody();
	}

	//jwt 토큰으로부터 인증 정보를 가져 오는 메서드
	public Authentication getAuthentication(String jwt){
		UserDetails userDetails = this.memberService.loadUserByUsername(this.getUsername(jwt));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

}
