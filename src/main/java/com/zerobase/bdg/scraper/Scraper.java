package com.zerobase.bdg.scraper;

import com.zerobase.bdg.model.Company;
import com.zerobase.bdg.model.ScrapedResult;

public interface Scraper {

	Company scrapCompanyByTicker(String ticker);
	ScrapedResult scrap(Company company);
}
