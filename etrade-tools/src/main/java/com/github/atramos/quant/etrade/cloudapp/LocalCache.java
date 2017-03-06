package com.github.atramos.quant.etrade.cloudapp;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LocalCache {
	
	private static ObjectMapper om = new ObjectMapper();
	
	public static <V> V get(String key, Supplier<V> callable) throws JsonProcessingException, IOException {
		File dir = new File(System.getProperty("java.io.tmpdir"));
		File cache = new File(dir, key);
		if(cache.exists()) {
			return om.readValue(cache, new TypeReference<V>(){});
		}
		else {
			V value = callable.get();
			om.writeValue(cache, value);
			return value;
		}
	}
}
