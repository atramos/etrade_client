package com.github.atramos.quant.etrade.client;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.etrade.etws.oauth.sdk.client.IOAuthClient;
import com.etrade.etws.oauth.sdk.client.OAuthClientImpl;
import com.etrade.etws.oauth.sdk.common.Token;
import com.etrade.etws.sdk.client.ClientRequest;
import com.etrade.etws.sdk.client.Environment;
import com.etrade.etws.sdk.common.ETWSException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EtradeApiClient {
	private IOAuthClient oauth_client = OAuthClientImpl.getInstance();

	final private JsonNode config;

	final private String oauth_consumer_key;

	final private String oauth_consumer_secret;

	final private Environment env;

	private String oauth_access_token = null;

	private String oauth_access_token_secret = null;

	private String oauth_request_token;

	private String oauth_request_token_secret;

	private File cookieFile = new File("cookie.txt");

	public EtradeApiClient() throws JsonProcessingException, IOException {
		config = new ObjectMapper().readTree(new File("src/main/resources/config.json"))
				.get(getClass().getSimpleName());
		oauth_consumer_key = config.get("oauth_consumer_key").asText();
		oauth_consumer_secret = config.get("oauth_consumer_secret").asText();
		env = Environment.valueOf(config.get("environment").asText());
	}

	public void authorize() throws IOException, ETWSException, URISyntaxException {
		if (cookieFile.exists())
			return;
		ClientRequest request = new ClientRequest();
		request.setEnv(env);
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

	public void verify() throws IOException, ETWSException {
		if (!cookieFile.exists()) {
			System.out.print("ENTER THE CODE AND PRESS ENTER: ");
			System.out.flush();
			String oauth_verify_code = new BufferedReader(new InputStreamReader(System.in)).readLine();
			ClientRequest request = newClientRequest();
			request.setToken(oauth_request_token);
			request.setTokenSecret(oauth_request_token_secret);
			request.setVerifierCode(oauth_verify_code); // Set verification code
			// Get access token
			Token token = oauth_client.getAccessToken(request); // Get
																// access-token
			// object
			oauth_access_token = token.getToken(); // Access token string
			oauth_access_token_secret = token.getSecret(); // Access token
															// secret
			FileUtils.writeLines(cookieFile,
					Arrays.asList(new String[] { oauth_access_token, oauth_access_token_secret }));
		} else {
			System.out.println("using cookie file " + cookieFile.getAbsolutePath());
			List<String> lines = FileUtils.readLines(cookieFile);
			oauth_access_token = lines.get(0);
			oauth_access_token_secret = lines.get(1);
		}
	}

	public ClientRequest newClientRequest() {
		ClientRequest request = new ClientRequest();
		// Prepare request
		request.setEnv(env);
		request.setConsumerKey(oauth_consumer_key);
		request.setConsumerSecret(oauth_consumer_secret);
		request.setToken(oauth_access_token);
		request.setTokenSecret(oauth_access_token_secret);
		return request;
	}
}
