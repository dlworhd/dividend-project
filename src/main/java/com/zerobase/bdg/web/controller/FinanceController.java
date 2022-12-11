package com.zerobase.bdg.web.controller;

import com.zerobase.bdg.service.FinanaceService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/finance")
@AllArgsConstructor
public class FinanceController {

	private final FinanaceService finanaceService;

	// @PathVariable을 하고 String같은 타입이 아니라 객체가 오면 어떻게 받는지?
	@GetMapping("/dividend/{companyName}")
	public ResponseEntity<?> searchFinance(@PathVariable String companyName){
		var result = this.finanaceService.getDividendByCompanyName(companyName);
		return ResponseEntity.ok(result);
	}
}
