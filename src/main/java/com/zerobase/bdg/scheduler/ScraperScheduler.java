package com.zerobase.bdg.scheduler;

import com.zerobase.bdg.model.Company;
import com.zerobase.bdg.model.ScrapedResult;
import com.zerobase.bdg.model.constants.CacheKey;
import com.zerobase.bdg.persist.CompanyRepository;
import com.zerobase.bdg.persist.DividendRepository;
import com.zerobase.bdg.persist.entity.CompanyEntity;
import com.zerobase.bdg.persist.entity.DividendEntity;
import com.zerobase.bdg.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@EnableCaching
@AllArgsConstructor
public class ScraperScheduler {

	private final CompanyRepository companyRepository;
	private final Scraper yahooFinanceScraper;
	private final DividendRepository dividendRepository;

	@CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true) // finance에 해당하면 다 지운다는 뜻 -> 스케쥴러가 돌 때 같이 캐시 비워짐
	@Scheduled(cron = "${scheduler.scrap.yahoo}")
	public void yahooFinanceScheduling() {
		log.info("Scraping scheduler is started");
		// 저장된 회사 목록 조회
		List<CompanyEntity> companies = this.companyRepository.findAll();
		// 회사마다 배당금 정보를 새로 스크래핑
		for (var company : companies) {
			ScrapedResult scrapedResult =  this.yahooFinanceScraper.scrap(new Company(company.getTicker(), company.getName()));

			scrapedResult.getDividends().stream()
					// Dividend Dto를 Dividend Entity로 맵핑
					.map(e -> new DividendEntity(company.getId(), e))
					// Element를 하나씩 DividendRespository에 삽입
					.forEach(e -> {
						boolean exist = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
						if(!exist){
							dividendRepository.save(e);
				}
			});

			// 연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시 정지
			try {
				Thread.sleep(3000); // 3초
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}

		}
		//스크래핑한
	}
}
