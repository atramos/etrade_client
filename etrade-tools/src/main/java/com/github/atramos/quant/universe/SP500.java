package com.github.atramos.quant.universe;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class SP500 implements Universe {

	public static class Info {
		
		public Info(CSVRecord rec) {
			symbol = rec.get("Symbol");
			name = rec.get("Name");
			sector = rec.get("Sector");
		}
		
		String symbol;
		String name;
		String sector;

		public String getSymbol() {
			return symbol;
		}
		public String getName() {
			return name;
		}
		public String getSector() {
			return sector;
		}
	}
	
	public Stream<Info> listInfo() throws IOException {
		URL url = new URL("https://raw.githubusercontent.com/datasets/s-and-p-500-companies/master/data/constituents.csv");
		Reader reader = new InputStreamReader(url.openStream());
		CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL.withHeader());
		
		return parser.getRecords().stream().map(rec -> new Info(rec)); 
	}

	@Override
	public List<String> listStocks() throws IOException {
		return listInfo().map(Info::getSymbol).collect(Collectors.toList());
	}
	
	
}
