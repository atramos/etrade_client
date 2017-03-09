package com.github.atramos.quant.etrade.cloudapp;

import java.io.IOException;
import java.util.ArrayList;
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
import com.github.atramos.quant.etrade.cloudapp.DashboardModel.Value;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class CloudantDAO {

	private Map<String,String> docIndex = null;
	
	public CloudantDAO() throws JsonProcessingException, IOException {
		docIndex = db.getAllDocsRequestBuilder().build().getResponse().getIdsAndRevs();
	}
	
	private JsonNode config = new ObjectMapper().readTree(getClass().getClassLoader().getResourceAsStream("config.json")).get(getClass().getSimpleName());

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
	
	public List<String> getOptionQuoteQueue() throws IOException {
		
		List<Row<ComplexKey, QueueStock>> rows = db.getViewRequestBuilder("main", "strike")
				.newRequest(Key.Type.COMPLEX, QueueStock.class)
				.groupLevel(1)
				.build()
				.getResponse().getRows();
		
		List<String> out = new ArrayList<>();
		
		for(Row<ComplexKey, QueueStock> row: rows) {
			if(row.getValue().getSymbol() == null) continue;
			String underlying = row.getValue().getSymbol().toUpperCase();
			
			if(row.getValue().getChains().size() > 0) {
				for(QueueChain chain: row.getValue().getChains()) {
					for(Double call: chain.getCalls())
						out.add(underlying + ":" + chain.getExp() + ":CALL:" + call.toString());

					for(Double put: chain.getPuts())
						out.add(underlying + ":" + chain.getExp() + ":PUT:" + put.toString());
				}
			}
		}
		return out;
	}

	public void deleteFromView(String view) throws IOException {
		db.bulk(db.getViewRequestBuilder("main", view)
					.newRequest(Key.Type.STRING, String.class).build()
					.getResponse().getRows()
					.stream().map(row -> {
						JsonObject jo = new JsonObject();
						jo.add("_id", new JsonPrimitive(row.getId()));
						jo.add("_rev", new JsonPrimitive(row.getKey()));
						jo.add("_deleted", new JsonPrimitive(true));
						return jo;
					}).collect(Collectors.toList()));
	}
	
	public List<Value> getDashboard() throws IOException {
		List<Row<ComplexKey, DashboardModel.Value>> rows = db.getViewRequestBuilder("main", "dashboard")
				.newRequest(Key.Type.COMPLEX, DashboardModel.Value.class)
				.groupLevel(1)
				.build()
				.getResponse().getRows();
		return rows.stream()
				.map(row -> row.getValue())
				.collect(Collectors.toList());
	}
}
