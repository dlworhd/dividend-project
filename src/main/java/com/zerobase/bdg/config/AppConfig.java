package com.zerobase.bdg.config;


import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {


	//서비스 내에서 하나만 유지되어야 함(싱글톤)
	@Bean
	public Trie<String, String> trie(){
		return new PatriciaTrie<>();
	}
}
