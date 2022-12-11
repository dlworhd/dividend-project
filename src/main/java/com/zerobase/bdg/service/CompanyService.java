package com.zerobase.bdg.service;

import com.zerobase.bdg.exception.NoCompanyException;
import com.zerobase.bdg.model.Company;
import com.zerobase.bdg.model.ScrapedResult;
import com.zerobase.bdg.persist.CompanyRepository;
import com.zerobase.bdg.persist.DividendRepository;
import com.zerobase.bdg.persist.entity.CompanyEntity;
import com.zerobase.bdg.persist.entity.DividendEntity;
import com.zerobase.bdg.scraper.Scraper;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompanyService {

	private final Trie trie;
	private final Scraper yahooFinanceScraper;
	private final CompanyRepository companyRepository;
	private final DividendRepository dividendRepository;


	public Company save(String ticker){
		boolean exists = this.companyRepository.existsByTicker(ticker);
		if(exists){
			throw new RuntimeException("already exists ticker -> " + ticker);
		}
		return this.storeCompanyAndDividend(ticker);

	}
	public Page<CompanyEntity> getAllCompany(Pageable pageable){ //반환타입도 List -> Page
		return this.companyRepository.findAll(pageable);
	}

	private Company storeCompanyAndDividend(String ticker){
		Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);

		// 1. Ticker에 해당하는 회사가 있는지 검토
		if( ObjectUtils.isEmpty(company)){ //널 체크?
			throw new RuntimeException("failed to scrap ticker -> " + ticker);
		}
		// 2. 회사가 존재할 경우 회사의 배당금 정보를 스크래핑
		ScrapedResult scrapedResult = yahooFinanceScraper.scrap(company);

		// 3. 스크래핑 후에 결과
		CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));

		// 컬렉션의 element들을 다른 값으로 매핑하는 데 사용
		List<DividendEntity> dividendEntityList = scrapedResult.getDividends().stream()
				.map(e -> new DividendEntity(companyEntity.getId(), e))
				.collect(Collectors.toList());

		for (DividendEntity dividendEntity : dividendEntityList) {
			System.out.println(dividendEntity.toString());
		}


		this.dividendRepository.saveAll(dividendEntityList);
		return company;
	}

	public void addAutocompleteKeyword(String keyword){
		this.trie.put(keyword, null);
	}
//
//	public List<String> autocomplete(String keyword){
//		return (List<String>) this.trie.prefixMap(keyword).keySet()
//				.stream().limit(10).collect(Collectors.toList());
//	}


	public List<String> getCompanyNamesByKeyword(String keyword){
		Pageable limit = PageRequest.of(0, 10);
		Page<CompanyEntity> companyEntities = this.companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);
		return companyEntities.stream().map(e -> e.getName()).collect(Collectors.toList());
	}

	public void deleteAutocompleteKeyword(String keyword){
		this.trie.remove(keyword);
	}

	public String deleteCompany(String ticker){

		var company = companyRepository.findByTicker(ticker).orElseThrow(() -> new NoCompanyException());
		this.dividendRepository.deleteAllByCompanyId(company.getId());
		this.companyRepository.delete(company);
		this.deleteAutocompleteKeyword(company.getName());

		return company.getName();
	}

}
