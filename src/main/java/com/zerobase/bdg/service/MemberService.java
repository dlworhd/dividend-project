package com.zerobase.bdg.service;


import com.zerobase.bdg.exception.AlreadyExistUserException;
import com.zerobase.bdg.model.Auth;
import com.zerobase.bdg.persist.MemberRepository;
import com.zerobase.bdg.persist.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return this.memberRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("couldn't find user -> " + username ));
	}


	public MemberEntity register(Auth.SignUp member) {
		boolean exist = memberRepository.existsByUsername(member.getUsername());
		if(exist){
			throw new AlreadyExistUserException();
		}

		member.setPassword(passwordEncoder.encode(member.getPassword()));
		var result =  memberRepository.save(member.toEntity());

		return result;
	}


	// 로그인을 할 때 검증하기 위함
	public MemberEntity authenticate(Auth.SignIn member){

		var user = this.memberRepository.findByUsername(member.getUsername())
				.orElseThrow(() -> new RuntimeException("존재하지 않는 ID입니다."));
		if(!this.passwordEncoder.matches(member.getPassword(), user.getPassword())){
			throw new RuntimeException("비밀번호가 일치하지 않습니다.");
		}

		return user;
	}


}
