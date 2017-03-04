package com.github.atramos.quant.etrade.cloud_loader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.collections4.ListUtils;

import com.etrade.etws.account.Account;
import com.etrade.etws.account.AccountListResponse;
import com.etrade.etws.market.DetailFlag;
import com.etrade.etws.market.OptionChainPair;
import com.etrade.etws.market.OptionChainRequest;
import com.etrade.etws.market.OptionChainResponse;
import com.etrade.etws.market.QuoteResponse;
import com.etrade.etws.sdk.client.AccountsClient;
import com.etrade.etws.sdk.client.ClientRequest;
import com.etrade.etws.sdk.client.MarketClient;
import com.etrade.etws.sdk.common.ETWSException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.atramos.quant.etrade.client.EtradeApiClient;
import com.github.atramos.quant.universe.SP500;
import com.github.atramos.quant.universe.Universe;

/**
 * E-Trade API tutorial code, heavily debugged, refactored and expanded.
 * Original code from:
 * https://us.etrade.com/ctnt/dev-portal/getContent?contentUri=V0_Code-Tutorial
 * 
 * Sandbox tokens:
 * https://us.etrade.com/ctnt/dev-portal/getContent?contentUri=V0_Documentation-
 * DeveloperGuides-Authorization
 * 
 * @author atram
 *
 */
public class CloudLoader extends EtradeApiClient {
	
	private Logger logger = Logger.getLogger(getClass().getName());
	
	private CloudantDAO da = new CloudantDAO();

	public CloudLoader() throws JsonProcessingException, IOException {
	}

	public void listAccounts() {
		ClientRequest request = newClientRequest();
		try {
			AccountsClient account_client = new AccountsClient(request);
			AccountListResponse response = account_client.getAccountList();
			List<Account> alist = response.getResponse();
			Iterator<Account> al = alist.iterator();
			while (al.hasNext()) {
				Account a = al.next();
				System.out.println("===================");
				System.out.println("Account: " + a.getAccountId());
				System.out.println("===================");
			}
		} catch (Exception e) {
		}
	}

	private List<OptionChainPair> getOptionChain(String symbol, String month, String year) throws IOException, ETWSException {
		OptionChainRequest req = new OptionChainRequest();
		req.setExpirationMonth(month);
		req.setExpirationYear(year);
		req.setChainType("CALLPUT");
		req.setSkipAdjusted("TRUE");
		req.setUnderlier(symbol);
		ClientRequest request = newClientRequest();
		MarketClient client = new MarketClient(request);
		try {
			OptionChainResponse response = client.getOptionChain(req);
			return response.getOptionPairs();
		} catch (Exception e) {
			logger.fine("No quote: " + symbol + "." + month + "." + year);
			return new ArrayList<>();
		}
	}

	private void getQuotes(List<String> symbols) throws IOException, ETWSException {
		logger.info(symbols.size() + " Symbols: " + symbols);
		ClientRequest request = newClientRequest();
		MarketClient client = new MarketClient(request);
		for(List<String> part : ListUtils.partition(symbols, 25)) {
			logger.info(part.toString());
			QuoteResponse response = client.getQuote(new ArrayList<String>(part), Boolean.FALSE, DetailFlag.INTRADAY);
			da.store(response.getQuoteData(), quote -> quote.getProduct().getSymbol(), true);
		}
	}

	private void getOptionQuotes(List<String> symbols) throws IOException, ETWSException {
		ClientRequest request = newClientRequest();
		MarketClient client = new MarketClient(request);
		for(List<String> part : ListUtils.partition(symbols, 25)) {
			logger.info(part.toString());
			QuoteResponse response = client.getQuote(new ArrayList<String>(part), Boolean.FALSE, DetailFlag.INTRADAY);
			da.store(response.getQuoteData(), 
					quote -> quote.getProduct().getSymbol()
						+ ":" + quote.getProduct().getExpirationYear()
						+ ":" + quote.getProduct().getExpirationMonth()
						+ ":" + quote.getProduct().getExpirationDay()
						+ ":" + quote.getProduct().getOptionType()
						+ ":" + quote.getProduct().getStrikePrice(), true);
		}
	}

	public static void main(String[] args) throws Exception {
		CloudLoader t = new CloudLoader();
		t.authorize();
		t.verify();
		t.downloadAll();
	}
	
	private static Universe universe = new SP500(); 

	private void downloadAll() throws JsonProcessingException, IOException, ETWSException {
		
		da.deleteFromView("removal_q");
		
		List<String> stocks = universe.listStocks();
		Set<String> stopList = da.getDocIndex().keySet();
		
		stocks.removeAll(stopList);
		this.getQuotes(stocks);
		
		List<String> topByVolume = da.getTopByVolume(100);
		logger.info("Top Stocks = " + topByVolume);
		
		for(String exp: new String[] { "2017-03", "2017-04", "2017-05", 
				"2017-09", "2017-12", "2018-04"}) {
			
			for(String sym: topByVolume) {
				String[] year_month = exp.split("-");
				String key = sym + ".chain." + exp;

				if(stopList.contains(key)) continue;
				
				da.store(getOptionChain(sym, year_month[1], year_month[0]), key, false);
			}
		}
			
		List<String> queue = da.getOptionQuoteQueue();
		
		this.getOptionQuotes(queue);
	}

}
