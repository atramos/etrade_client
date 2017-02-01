package com.github.atramos.etrade_tools;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.etrade.etws.account.Account;
import com.etrade.etws.account.AccountListResponse;
import com.etrade.etws.market.DetailFlag;
import com.etrade.etws.market.OptionChainPair;
import com.etrade.etws.market.OptionChainRequest;
import com.etrade.etws.market.OptionChainResponse;
import com.etrade.etws.market.QuoteData;
import com.etrade.etws.market.QuoteResponse;
import com.etrade.etws.oauth.sdk.client.IOAuthClient;
import com.etrade.etws.oauth.sdk.client.OAuthClientImpl;
import com.etrade.etws.oauth.sdk.common.Token;
import com.etrade.etws.sdk.client.AccountsClient;
import com.etrade.etws.sdk.client.ClientRequest;
import com.etrade.etws.sdk.client.Environment;
import com.etrade.etws.sdk.client.MarketClient;
import com.etrade.etws.sdk.common.ETWSException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

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
public class Tutorial {
	private IOAuthClient oauth_client = OAuthClientImpl.getInstance();

	private String oauth_consumer_key = System.getenv("CONSUMER_KEY");

	private String oauth_consumer_secret = System.getenv("CONSUMER_SECRET");

	private String oauth_access_token = null;

	private String oauth_access_token_secret = null;

	private String oauth_request_token;

	private String oauth_request_token_secret;

	private void authorize() throws IOException, ETWSException, URISyntaxException {
		ClientRequest request = new ClientRequest();
		request.setEnv(Environment.SANDBOX);
		request.setConsumerKey(oauth_consumer_key);
		request.setConsumerSecret(oauth_consumer_secret);
		Token token = oauth_client.getRequestToken(request);
		oauth_request_token = token.getToken();
		oauth_request_token_secret = token.getSecret();
		request.setToken(oauth_request_token);
		request.setTokenSecret(oauth_request_token_secret);
		String authorizeURL = oauth_client.getAuthorizeUrl(request);
		URI uri = new java.net.URI(authorizeURL);
		Desktop desktop = Desktop.getDesktop();
		desktop.browse(uri);
	}

	private void verify() throws IOException, ETWSException {
		System.out.print("ENTER THE CODE AND PRESS ENTER: ");
		System.out.flush();
		String oauth_verify_code = new BufferedReader(new InputStreamReader(System.in)).readLine();
		ClientRequest request = newClientRequest();
		request.setToken(oauth_request_token);
		request.setTokenSecret(oauth_request_token_secret);
		request.setVerifierCode(oauth_verify_code); // Set verification code
		// Get access token
		Token token = oauth_client.getAccessToken(request); // Get access-token
															// object
		oauth_access_token = token.getToken(); // Access token string
		oauth_access_token_secret = token.getSecret(); // Access token secret
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

	private ClientRequest newClientRequest() {
		ClientRequest request = new ClientRequest();
		// Prepare request
		request.setEnv(Environment.SANDBOX);
		request.setConsumerKey(oauth_consumer_key);
		request.setConsumerSecret(oauth_consumer_secret);
		request.setToken(oauth_access_token);
		request.setTokenSecret(oauth_access_token_secret);
		return request;
	}

	private List<OptionChainPair> getOptionChain(String symbol) throws IOException, ETWSException {
		OptionChainRequest req = new OptionChainRequest();
		req.setExpirationMonth("1"); // example values
		req.setExpirationYear("2018");
		req.setChainType("CALL"); // example values
		req.setSkipAdjusted("FALSE");
		req.setUnderlier("GOOG");
		
		ClientRequest request = newClientRequest();
		MarketClient client = new MarketClient(request);
		OptionChainResponse response = client.getOptionChain(req);
		return response.getOptionPairs();
	}

	private List<QuoteData> getQuote(String symbol) throws IOException, ETWSException {
		ClientRequest request = newClientRequest();
		ArrayList<String> list = new ArrayList<String>();
		MarketClient client = new MarketClient(request);
		list.add("CSCO");
		list.add("AAPL");
		QuoteResponse response = client.getQuote(list, Boolean.FALSE, DetailFlag.ALL);
		return response.getQuoteData();
	}

	public static void main(String[] args) throws IOException, ETWSException, URISyntaxException {
		Tutorial t = new Tutorial();
		t.authorize();
		t.verify();
		t.listAccounts();
		String[] symbols = { "MSFT" };
		for (String symbol : symbols) {
			ObjectMapper om = new ObjectMapper();
			ObjectWriter ow = om.writerWithDefaultPrettyPrinter();
			System.out.println(t.getQuote(symbol).toString());
			System.out.println(ow.writeValueAsString(t.getOptionChain(symbol)));
		}
	}
}
