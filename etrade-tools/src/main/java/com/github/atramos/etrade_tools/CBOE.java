package com.github.atramos.etrade_tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

/**
 * Access list of options from http://www.cboe.com/trading-resources/symbol-directory/equity-index-leaps-options
 * 
 * @author atram
 *
 */
public class CBOE {
	
	public static class Option {
		public String Company_Name;

		public String Stock_Symbol;

		public String DPM;

		public String Cycle;

		public String Traded_at_C2;

		public Map<Integer,String> LEAPS = new HashMap<>();

		public String Product_Types;

		public String Post_Station;
	}

	public static Stream<Option> getOptionables() throws IOException {
		File csv = new File("src/main/resources/cboesymboldir2.csv");
		BufferedReader reader = new BufferedReader(new FileReader(csv));
		reader.readLine(); // discard
		CSVParser parser = new CSVParser(reader, CSVFormat.EXCEL.withHeader());
		
		return parser.getRecords().stream().map(rec -> {
			Option opt = new Option();
			opt.Company_Name = rec.get("Company Name");
			opt.Stock_Symbol = rec.get("Stock Symbol");
			opt.DPM = rec.get("DPM");
			opt.Cycle = rec.get("Cycle");
			opt.Traded_at_C2 = rec.get("Traded at C2");
			opt.Product_Types = rec.get("Product Types");
			opt.Post_Station = rec.get(" Post/Station");
			rec.toMap().keySet().stream().filter(name -> name.startsWith("LEAPS")).forEach(name -> {
				String yearStr = name.split(" ")[1];
				opt.LEAPS.put(Integer.parseInt(yearStr), rec.get(name));
			});
			return opt;
		});
	}
}
