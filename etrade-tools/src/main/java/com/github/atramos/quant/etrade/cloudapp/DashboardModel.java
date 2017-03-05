package com.github.atramos.quant.etrade.cloudapp;

import java.util.ArrayList;

public class DashboardModel {
	public static class Intraday {
		private int ask;

		public int getAsk() {
			return this.ask;
		}

		public void setAsk(int ask) {
			this.ask = ask;
		}

		private double bid;

		public double getBid() {
			return this.bid;
		}

		public void setBid(double bid) {
			this.bid = bid;
		}

		private double chgClose;

		public double getChgClose() {
			return this.chgClose;
		}

		public void setChgClose(double chgClose) {
			this.chgClose = chgClose;
		}

		private double chgClosePrcn;

		public double getChgClosePrcn() {
			return this.chgClosePrcn;
		}

		public void setChgClosePrcn(double chgClosePrcn) {
			this.chgClosePrcn = chgClosePrcn;
		}

		private String companyName;

		public String getCompanyName() {
			return this.companyName;
		}

		public void setCompanyName(String companyName) {
			this.companyName = companyName;
		}

		private double high;

		public double getHigh() {
			return this.high;
		}

		public void setHigh(double high) {
			this.high = high;
		}

		private double lastTrade;

		public double getLastTrade() {
			return this.lastTrade;
		}

		public void setLastTrade(double lastTrade) {
			this.lastTrade = lastTrade;
		}

		private double low;

		public double getLow() {
			return this.low;
		}

		public void setLow(double low) {
			this.low = low;
		}

		private int totalVolume;

		public int getTotalVolume() {
			return this.totalVolume;
		}

		public void setTotalVolume(int totalVolume) {
			this.totalVolume = totalVolume;
		}
	}

	public static class Product {
		private String symbol;

		public String getSymbol() {
			return this.symbol;
		}

		public void setSymbol(String symbol) {
			this.symbol = symbol;
		}

		private String type;

		public String getType() {
			return this.type;
		}

		public void setType(String type) {
			this.type = type;
		}

		private String exchange;

		public String getExchange() {
			return this.exchange;
		}

		public void setExchange(String exchange) {
			this.exchange = exchange;
		}
	}

	public static class Stock {
		private String _id;

		public String getId() {
			return this._id;
		}

		public void setId(String _id) {
			this._id = _id;
		}

		private String _rev;

		public String getRev() {
			return this._rev;
		}

		public void setRev(String _rev) {
			this._rev = _rev;
		}

		private String dateTime;

		public String getDateTime() {
			return this.dateTime;
		}

		public void setDateTime(String dateTime) {
			this.dateTime = dateTime;
		}

		private Intraday intraday;

		public Intraday getIntraday() {
			return this.intraday;
		}

		public void setIntraday(Intraday intraday) {
			this.intraday = intraday;
		}

		private Product product;

		public Product getProduct() {
			return this.product;
		}

		public void setProduct(Product product) {
			this.product = product;
		}
	}

	public static class Intraday2 {
		private double ask;

		public double getAsk() {
			return this.ask;
		}

		public void setAsk(double ask) {
			this.ask = ask;
		}

		private double bid;

		public double getBid() {
			return this.bid;
		}

		public void setBid(double bid) {
			this.bid = bid;
		}

		private double chgClose;

		public double getChgClose() {
			return this.chgClose;
		}

		public void setChgClose(double chgClose) {
			this.chgClose = chgClose;
		}

		private double chgClosePrcn;

		public double getChgClosePrcn() {
			return this.chgClosePrcn;
		}

		public void setChgClosePrcn(double chgClosePrcn) {
			this.chgClosePrcn = chgClosePrcn;
		}

		private String companyName;

		public String getCompanyName() {
			return this.companyName;
		}

		public void setCompanyName(String companyName) {
			this.companyName = companyName;
		}

		private double high;

		public double getHigh() {
			return this.high;
		}

		public void setHigh(double high) {
			this.high = high;
		}

		private double lastTrade;

		public double getLastTrade() {
			return this.lastTrade;
		}

		public void setLastTrade(double lastTrade) {
			this.lastTrade = lastTrade;
		}

		private double low;

		public double getLow() {
			return this.low;
		}

		public void setLow(double low) {
			this.low = low;
		}

		private int totalVolume;

		public int getTotalVolume() {
			return this.totalVolume;
		}

		public void setTotalVolume(int totalVolume) {
			this.totalVolume = totalVolume;
		}
	}

	public static class Product2 {
		private String symbol;

		public String getSymbol() {
			return this.symbol;
		}

		public void setSymbol(String symbol) {
			this.symbol = symbol;
		}

		private String type;

		public String getType() {
			return this.type;
		}

		public void setType(String type) {
			this.type = type;
		}

		private String exchange;

		public String getExchange() {
			return this.exchange;
		}

		public void setExchange(String exchange) {
			this.exchange = exchange;
		}

		private String optionType;

		public String getOptionType() {
			return this.optionType;
		}

		public void setOptionType(String optionType) {
			this.optionType = optionType;
		}

		private double strikePrice;

		public double getStrikePrice() {
			return this.strikePrice;
		}

		public void setStrikePrice(double strikePrice) {
			this.strikePrice = strikePrice;
		}

		private int expirationYear;

		public int getExpirationYear() {
			return this.expirationYear;
		}

		public void setExpirationYear(int expirationYear) {
			this.expirationYear = expirationYear;
		}

		private int expirationMonth;

		public int getExpirationMonth() {
			return this.expirationMonth;
		}

		public void setExpirationMonth(int expirationMonth) {
			this.expirationMonth = expirationMonth;
		}

		private int expirationDay;

		public int getExpirationDay() {
			return this.expirationDay;
		}

		public void setExpirationDay(int expirationDay) {
			this.expirationDay = expirationDay;
		}
	}

	public static class Option {
		private String _id;

		public String getId() {
			return this._id;
		}

		public void setId(String _id) {
			this._id = _id;
		}

		private String _rev;

		public String getRev() {
			return this._rev;
		}

		public void setRev(String _rev) {
			this._rev = _rev;
		}

		private String dateTime;

		public String getDateTime() {
			return this.dateTime;
		}

		public void setDateTime(String dateTime) {
			this.dateTime = dateTime;
		}

		private Intraday2 intraday;

		public Intraday2 getIntraday() {
			return this.intraday;
		}

		public void setIntraday(Intraday2 intraday) {
			this.intraday = intraday;
		}

		private Product2 product;

		public Product2 getProduct() {
			return this.product;
		}

		public void setProduct(Product2 product) {
			this.product = product;
		}
	}

	public static class Value {
		private Stock stock;

		public Stock getStock() {
			return this.stock;
		}

		public void setStock(Stock stock) {
			this.stock = stock;
		}

		private ArrayList<Option> options;

		public ArrayList<Option> getOptions() {
			return this.options;
		}

		public void setOptions(ArrayList<Option> options) {
			this.options = options;
		}
	}

	public static class RootObject {
		private Value value;

		public Value getValue() {
			return this.value;
		}

		public void setValue(Value value) {
			this.value = value;
		}
	}
}
