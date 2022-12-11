package com.zerobase.bdg.web.controller;


import com.zerobase.bdg.model.Auth;
import com.zerobase.bdg.persist.entity.MemberEntity;
import com.zerobase.bdg.security.TokenProvider;
import com.zerobase.bdg.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final MemberService memberService;
	private final TokenProvider tokenProvider;

	@PostMapping("/signup")
	public ResponseEntity<?> signup(@RequestBody Auth.SignUp request){
		var result = this.memberService.register(request);
		return ResponseEntity.ok(result);
	}

	@PostMapping("/signin")
	public ResponseEntity<?> signin(@RequestBody Auth.SignIn request){
		log.info("user login -> " + request.getUsername());
		var member = this.memberService.authenticate(request);
		var token = this.tokenProvider.genereateToken(member.getUsername(), member.getRoles());
		return ResponseEntity.ok(token);
	}
}
