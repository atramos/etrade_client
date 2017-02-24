package com.github.atramos.etrade_tools;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.views.Key;
import com.cloudant.client.api.views.Key.ComplexKey;
import com.cloudant.client.api.views.ViewResponse.Row;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;

public class DataAccess {

	private Map<String,String> docIndex = null;
	
	public DataAccess() throws JsonProcessingException, IOException {
		docIndex = db.getAllDocsRequestBuilder().build().getResponse().getIdsAndRevs();
	}
	
	private JsonNode config = new ObjectMapper().readTree(new File("src/main/resources/config.json")).get(getClass().getSimpleName());

	private final String CLOUDANT_PASSWORD = config.get("CLOUDANT_PASSWORD").asText();

	private final String CLOUDANT_USER = config.get("CLOUDANT_USER").asText();

	private final String CLOUDANT_ACCOUNT = config.get("CLOUDANT_ACCOUNT").asText();

	private final String CLOUDANT_DB = config.get("CLOUDANT_DB").asText();

	private CloudantClient client = ClientBuilder.account(CLOUDANT_ACCOUNT).username(CLOUDANT_USER)
			.password(CLOUDANT_PASSWORD).build();

	private Database db = client.database(CLOUDANT_DB, false);
	
	public void store(Object o, String id, boolean enableUpdates) throws JsonProcessingException {
		this.store(Arrays.asList(new Object[] { o }), any -> id, enableUpdates);
	}
	
	public <T> void store(List<T> list, Function<T,String> idMapper, boolean enableUpdates) throws JsonProcessingException {
		final ObjectMapper om = new ObjectMapper();
		db.bulk(list.stream().map(item -> {
			String jsonStr;
			try {
				jsonStr = om.writeValueAsString(item);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			JsonElement json = new JsonParser().parse(jsonStr);
			if(json.isJsonArray()) {
				JsonObject temp = new JsonObject();
				temp.add("array", json);
				json = temp;
			}
			JsonObject asJsonObject = json.getAsJsonObject();
			String id = idMapper.apply(item);
			asJsonObject.add("_id", new JsonPrimitive(id));
			if(enableUpdates && docIndex.containsKey(id)) {
				asJsonObject.add("_rev", new JsonPrimitive(docIndex.get(id)));
			}
			return asJsonObject;
		}).collect(Collectors.toList()));
	}

	public List<String> getTopByVolume(int limit) throws IOException {
		return db.getViewRequestBuilder("main", "volume")
				.newRequest(Key.Type.STRING, Integer.class)
				.descending(true).limit(limit)
				.build()
				.getResponse().getRows().stream().map(row -> row.getId()).collect(Collectors.toList());
	}

	public Map<String, String> getDocIndex() {
		return docIndex;
	}
	
	public static class OptionStruct {
		public String callSymbol;
		public String putSymbol;
		public double stockPrice;
		public double callStrike;
		public double putStrike;
	}
	
	public List<OptionStruct> getOptionQuoteQueue() throws IOException {
		List<Row<ComplexKey, JsonArray>> rows = db.getViewRequestBuilder("main", "strike")
				.newRequest(Key.Type.COMPLEX, JsonArray.class)
				.groupLevel(1)
				.build()
				.getResponse().getRows();
		return rows.stream().filter(row -> {
			try {
				JsonArray arr = row.getValue();
				JsonArray puts = arr.get(0).getAsJsonArray();
				JsonArray underlying = arr.get(1).getAsJsonArray();
				JsonArray calls = arr.get(2).getAsJsonArray();
				return !(puts.size() == 0 ||
				   underlying.size() == 0 ||
				   calls.size() == 0);
			}
			catch(JsonSyntaxException e) {
				return false;
			}
		}).map(row -> {
			JsonArray arr = row.getValue().getAsJsonArray();
			JsonArray puts = arr.get(0).getAsJsonArray();
			JsonArray underlying = arr.get(1).getAsJsonArray();
			JsonArray calls = arr.get(2).getAsJsonArray();
			OptionStruct os = new OptionStruct();
			os.stockPrice = underlying.get(0).getAsDouble();
			os.putSymbol = puts.get(0).getAsJsonObject().get("symbol").getAsString();
			os.putStrike = puts.get(0).getAsJsonObject().get("strikePrice").getAsDouble();
			if(calls.get(0).isJsonObject()) {
				os.callSymbol = calls.get(0).getAsJsonObject().get("symbol").getAsString();
				os.callStrike = calls.get(0).getAsJsonObject().get("strikePrice").getAsDouble();
			}
			return os;
		}).collect(Collectors.toList());
	}
}
