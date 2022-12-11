package com.zerobase.bdg.scraper;

import com.zerobase.bdg.model.Company;
import com.zerobase.bdg.model.Dividend;
import com.zerobase.bdg.model.ScrapedResult;
import com.zerobase.bdg.model.constants.Month;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class YahooFinanceScraper implements Scraper{
	private static final long START_TIME = 86400; //시작 날짜는 건들 필요가 없어서 상수로
	private static final String STATISTICS_URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";
	private static final String SUMMARY_URL = "https://finance.yahoo.com/quote/%s?p=%s";

	@Override
	public ScrapedResult scrap(Company company) {
		var scrapResult = new ScrapedResult();
		scrapResult.setCompany(company);
		try {
			long now = System.currentTimeMillis() / 1000; // 밀리 단위 -> 초 단위
			String url = String.format(STATISTICS_URL, company.getTicker(), START_TIME, now);

			Connection connection = Jsoup.connect(url);

			Document document = connection.get();
			// data-test라는 value에 의해서 historical-prices라는 elements들을 가져옴
			Elements parsingDivs = document.getElementsByAttributeValue("data-test", "historical-prices");
			Element tableEle = parsingDivs.get(0); //table 전체

			Element tbody = tableEle.children().get(1);

			List<Dividend> dividends = new ArrayList<>();
			for (Element e : tbody.children()) {
				String txt = e.text();
				if (!txt.endsWith("Dividend")) {
					continue;
				}

				String[] splits = txt.split(" ");
				int month = Month.strToNumber(splits[0]);
				int day = Integer.valueOf(splits[1].replace(",", ""));
				int year = Integer.valueOf(splits[2]);
				String dividend = splits[3];

				if (month < 0) {
					throw new RuntimeException("Unexpected Month enum value ->" + splits[0]);
				}

				dividends.add(new Dividend(LocalDateTime.of(year, month, day, 0, 0), dividend));
			}
			scrapResult.setDividends(dividends);
		} catch (IOException e) {
			//TODO
			e.printStackTrace();
		}
		return scrapResult;
	}


	@Override
	public Company scrapCompanyByTicker(String ticker) {
		String url = String.format(SUMMARY_URL, ticker, ticker);
		try {
			Document document = Jsoup.connect(url).get();
			log.info(document.toString());
			Element titleEle = document.getElementsByTag("h1").get(0);
			String title = titleEle.text().split(" - ")[0].trim(); // 데이터의 특성에 따라 이렇게도 할 수 있는 것
			return new Company(ticker, title);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
