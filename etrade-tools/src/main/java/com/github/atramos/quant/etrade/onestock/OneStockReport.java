package com.github.atramos.quant.etrade.onestock;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import com.etrade.etws.market.OptionExpireDateGetRequest;
import com.etrade.etws.market.OptionExpireDateGetResponse;
import com.etrade.etws.sdk.client.ClientRequest;
import com.etrade.etws.sdk.client.MarketClient;
import com.etrade.etws.sdk.common.ETWSException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.atramos.quant.etrade.client.EtradeApiClient;

public class OneStockReport {
	private ObjectMapper om = new ObjectMapper();

	private Logger logger = Logger.getLogger(getClass().getName());

	public static void main(String[] args) throws IOException, ETWSException, URISyntaxException {
		new OneStockReport().run(args);
	}

	private void run(String[] args) throws IOException, ETWSException, URISyntaxException {
		EtradeApiClient api = new EtradeApiClient();
		api.authorize();
		api.verify();
		String stock = args[0];
		ClientRequest request = api.newClientRequest();
		MarketClient client = new MarketClient(request);
		OptionExpireDateGetRequest req = new OptionExpireDateGetRequest();
		req.setUnderlier(stock);
		OptionExpireDateGetResponse response = client.getExpiryDates(req);
		
		logger.info(om.writerWithDefaultPrettyPrinter().writeValueAsString(response));
	}
}
