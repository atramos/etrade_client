package com.github.atramos.etrade_tools;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import com.etrade.etws.account.Account;
import com.etrade.etws.account.AccountListResponse;
import com.etrade.etws.oauth.sdk.client.IOAuthClient;
import com.etrade.etws.oauth.sdk.client.OAuthClientImpl;
import com.etrade.etws.oauth.sdk.common.Token;
import com.etrade.etws.sdk.client.AccountsClient;
import com.etrade.etws.sdk.client.ClientRequest;
import com.etrade.etws.sdk.client.Environment;
import com.etrade.etws.sdk.common.ETWSException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Tutorial code copied from https://us.etrade.com/ctnt/dev-portal/getContent?contentUri=V0_Code-Tutorial
 * 
 * TODO:
 * - Configure sandbox tokens
 * - Test and debug the code
 * - Implement the getQuote() and getOptionChain() methods.
 * 
 * Sandbox tokens are available here:
 * https://us.etrade.com/ctnt/dev-portal/getContent?contentUri=V0_Documentation-DeveloperGuides-Authorization
 * 
 * @author atram
 *
 */
public class Tutorial {
	// Variables
	public IOAuthClient client = null;

	public ClientRequest request = null;

	public Token token = null;

	public String oauth_consumer_key = null; // Your consumer key

	public String oauth_consumer_secret = null; // Your consumer secret

	public String oauth_request_token = null; // Request token

	public String oauth_request_token_secret = null; // Request token secret

	public String oauth_access_token = null; // Variable to store access token

	public String oauth_access_token_secret = null; // Variable to store access
													// token secret

	public String oauth_verify_code = "Your verification_code"; // Should
																// contain the
																// Verification
																// Code received
																// from the
																// authorization
																// step

	public void init() throws IOException, ETWSException, URISyntaxException {
		client = OAuthClientImpl.getInstance(); // Instantiate IOAUthClient
		request = new ClientRequest(); // Instantiate ClientRequest
		request.setEnv(Environment.SANDBOX); // Use sandbox environment
		request.setConsumerKey(oauth_consumer_key); // Set consumer key
		request.setConsumerSecret(oauth_consumer_secret); // Set consumer secret
		token = client.getRequestToken(request); // Get request-token object
		oauth_request_token = token.getToken(); // Get token string
		oauth_request_token_secret = token.getSecret(); // Get token secret
		String authorizeURL = null;
		authorizeURL = client.getAuthorizeUrl(request); // E*TRADE authorization
														// URL
		URI uri = new java.net.URI(authorizeURL);
		Desktop desktop = Desktop.getDesktop();
		desktop.browse(uri);
		request = new ClientRequest(); // Instantiate ClientRequest
		request.setEnv(Environment.SANDBOX); // Use sandbox environment
		// Prepare request
		request.setConsumerKey(oauth_consumer_key); // Set consumer key
		request.setConsumerSecret(oauth_consumer_secret); // Set consumer secret
		request.setToken(oauth_request_token); // Set request token
		request.setTokenSecret(oauth_request_token_secret); // Set request-token
															// secret
		request.setVerifierCode(oauth_verify_code); // Set verification code
		// Get access token
		token = client.getAccessToken(request); // Get access-token object
		oauth_access_token = token.getToken(); // Access token string
		oauth_access_token_secret = token.getSecret(); // Access token secret
	}

	public void listAccounts() {
		request = new ClientRequest(); // Instantiate ClientRequest
		// Prepare request
		request.setEnv(Environment.SANDBOX);
		request.setConsumerKey(oauth_consumer_key);
		request.setConsumerSecret(oauth_consumer_secret);
		request.setToken(oauth_access_token);
		request.setTokenSecret(oauth_access_token_secret);
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

	private static JsonNode getOptionChain(String symbol) {
		/**
		 * Sample Request
GET https://etws.etrade.com/market/rest/optionchains?expirationMonth=04&expirationYear=2011&chainType=PUT&skipAdjusted=true&underlier=GOOG

		 * Documentation: https://us.etrade.com/ctnt/dev-portal/getDetail?contentUri=V0_Documentation-MarketAPI-GetOptionChains	
		 */
		throw new UnsupportedOperationException("unfinished code");
	}

	private static JsonNode getQuote(String symbol) {
		/**
		 * // https://us.etrade.com/ctnt/dev-portal/getDetail?contentUri=V0_Documentation-MarketAPI-GetQuotes
		 * // URL https://etws.etrade.com/market/rest/quote/{symbol, symbol...}
		 */
		throw new UnsupportedOperationException("unfinished code");
	}
	
	
	public static void main(String[] args) throws IOException, ETWSException, URISyntaxException {
		Tutorial t = new Tutorial();
		t.init();
		t.listAccounts();
		
		String[] symbols = { "AAPL", "MSFT", "IBM" };
		
		for(String symbol : symbols) {
			ObjectMapper om = new ObjectMapper();
			
			System.out.println(om.writeValueAsString(getQuote(symbol)));
			System.out.println(om.writeValueAsString(getOptionChain(symbol)));
		}
	}

}
