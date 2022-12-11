package com.zerobase.bdg.service;

import com.zerobase.bdg.model.Company;
import com.zerobase.bdg.model.Dividend;
import com.zerobase.bdg.model.ScrapedResult;
import com.zerobase.bdg.model.constants.CacheKey;
import com.zerobase.bdg.persist.CompanyRepository;
import com.zerobase.bdg.persist.DividendRepository;
import com.zerobase.bdg.persist.entity.CompanyEntity;
import com.zerobase.bdg.persist.entity.DividendEntity;
import com.zerobase.bdg.scraper.YahooFinanceScraper;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FinanaceService {

	private final CompanyRepository companyRepository;
	private final CompanyService companyService;
	private final DividendRepository dividendRepository;
	private final YahooFinanceScraper yahooFinanceScraper;

	// 요청이 자주 들어오는가? -> 자주 들어오면 캐시에 저장하는 게 좋음
	// 자주 변경되는 데이터인가? -> 업데이트 때마다 캐시에 있는 데이터도 업데이트 해야 함 (배당금은 잘 안 바뀜)
	@Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE) // key와 value는 redis 서버의 key value 랑 다름
	public ScrapedResult getDividendByCompanyName(String companyName){
		CompanyEntity company = companyRepository.findByName(companyName).orElseThrow(() -> new RuntimeException("회사가 존재하지 않습니다."));
		List<DividendEntity> dividendEntities = dividendRepository.findAllByCompanyId(company.getId());

		List<Dividend> dividendList = dividendEntities.stream().map(
				e -> new Dividend(e.getDate(), e.getDividend())).collect(Collectors.toList());


		return new ScrapedResult(new Company(company.getTicker(), company.getName()),dividendList);
	}

}
