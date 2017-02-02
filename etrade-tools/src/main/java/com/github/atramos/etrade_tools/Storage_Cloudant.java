package com.github.atramos.etrade_tools;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.cloudant.client.api.model.Response;
import com.cloudant.client.api.views.Key;
import com.cloudant.client.api.views.ViewResponse;
import com.cloudant.client.api.views.ViewResponse.Row;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Storage_Cloudant implements Storage {
	
	private static final DateTimeFormatter UTC_DATE = DateTimeFormatter.ISO_LOCAL_DATE.withZone(ZoneId.of("UTC"));

	private static final String CLOUDANT_PASSWORD = "cf16e002cac28e9dec416f97b2fde335e6be2312";

	private static final String CLOUDANT_USER = "whintedstaingerstriandly";

	private static final String CLOUDANT_ACCOUNT = "ctcrets";

	private static final String CLOUDANT_DB = "ctcrets";

	private CloudantClient client = ClientBuilder.account(CLOUDANT_ACCOUNT).username(CLOUDANT_USER)
			.password(CLOUDANT_PASSWORD).build();

	private Database db = client.database(CLOUDANT_DB, false);

	/* (non-Javadoc)
	 * @see com.ctc.rets.sync.Storage#putRecords(java.util.List)
	 */
	@Override
	public void putRecords(List<JsonObject> data, Instant syncTime) {
		for (JsonObject item : data) {
			item.add("_id",
					new JsonPrimitive(item.get("LN").getAsString() + " " + item.get("RECORDMODDATE").getAsString()));
			item.add("$ctc_origin_timestamp", new JsonPrimitive(Instant.now().toString()));
		}
		List<Response> respList = db.bulk(data);
		for(Response resp : respList) {
			if(resp.getStatusCode() != 201 || resp.getError() != null) {
				throw new RuntimeException(
						String.format("Error: StatusCode=%d, Reason=[%s], Error=[%s], ID=[%s]",
								resp.getStatusCode(), resp.getReason(), resp.getError(), resp.getId()));
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.ctc.rets.sync.Storage#getKillSet()
	 */
	@Override
	public Set<Storage.PrimaryKey> getKeys(ZonedDateTime lookback, Instant syncTime) throws IOException {
		String startkey = UTC_DATE.format(lookback);
		ViewResponse<String, String> response = db.getViewRequestBuilder("main", "recordmoddate")
				.newRequest(Key.Type.STRING, String.class).startKey(startkey ).build().getResponse();
		Set<Storage.PrimaryKey> out = new TreeSet<>();
		for (Row<String, String> row : response.getRows()) {
			out.add(new Storage.PrimaryKey(row.getValue(), row.getKey()));
		}
		return out;
	}

	/* (non-Javadoc)
	 * @see com.ctc.rets.sync.Storage#onceADay(java.util.Set)
	 */
	@Override
	public void putKeys(Set<Storage.PrimaryKey> syncMap, Instant syncTime) {
		String indexDocID = "ctc index " + UTC_DATE.format(ZonedDateTime.now());
		if(!db.contains(indexDocID)) {
			JsonObject jo = new JsonObject();
			JsonArray ja = new JsonArray();
			for(Storage.PrimaryKey pk : syncMap) ja.add(pk.getLN());
			jo.add("$ctc_index", ja);
			jo.add("$ctc_index_timestamp", new JsonPrimitive(Instant.now().toString()));
			jo.add("_id", new JsonPrimitive(indexDocID));
			db.save(jo);
		}
	}
}
