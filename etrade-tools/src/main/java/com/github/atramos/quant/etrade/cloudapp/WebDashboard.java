package com.github.atramos.quant.etrade.cloudapp;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.atramos.quant.etrade.cloudapp.DashboardModel.Option;
import com.github.atramos.quant.etrade.cloudapp.DashboardModel.Value;

public class WebDashboard extends HttpServlet {
	public static class Flattened {
		String companyName;

		String symbol;

		int totalVolume;

		double lastTrade;

		double chgClosePrcn;

		String expiration;

		double strikePrice;

		String optionType;

		double optionBid;

		double optionAsk;

		int optionVolume;

		double spreadPct;

		double yield;

		double aYield;

		double chgClose;

		public String getCompanyName() {
			return companyName;
		}

		public String getSymbol() {
			return symbol;
		}

		public int getTotalVolume() {
			return totalVolume;
		}

		public double getLastTrade() {
			return lastTrade;
		}

		public double getChgClosePrcn() {
			return chgClosePrcn;
		}

		public String getExpiration() {
			return expiration;
		}

		public double getStrikePrice() {
			return strikePrice;
		}

		public String getOptionType() {
			return optionType;
		}

		public double getOptionBid() {
			return optionBid;
		}

		public double getOptionAsk() {
			return optionAsk;
		}

		public int getOptionVolume() {
			return optionVolume;
		}

		public double getSpreadPct() {
			return spreadPct;
		}

		public double getaYield() {
			return aYield;
		}

		public double getYield() {
			return yield;
		}

		public double getChgClose() {
			return chgClose;
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		CloudantDAO dao = new CloudantDAO();
		List<Value> data = dao.getDashboard();
		List<Flattened> flatList = new ArrayList<>();
		
		for (Value item : data) {
			for (Option opt : item.getOptions()) {
				if (opt != null && opt.getProduct() != null) {
					Flattened flat = new Flattened();
					flat.companyName = item.getStock().getIntraday().getCompanyName();
					flat.symbol = item.getStock().getProduct().getSymbol();
					flat.totalVolume = item.getStock().getIntraday().getTotalVolume();
					flat.lastTrade = item.getStock().getIntraday().getLastTrade();
					flat.chgClose = item.getStock().getIntraday().getChgClose();
					flat.chgClosePrcn = item.getStock().getIntraday().getChgClosePrcn();
					flat.expiration = String.format("%04d-%02d-%02d", opt.getProduct().getExpirationYear(),
							opt.getProduct().getExpirationMonth(), opt.getProduct().getExpirationDay());
					flat.strikePrice = opt.getProduct().getStrikePrice();
					flat.optionType = opt.getProduct().getOptionType();
					flat.optionBid = opt.getIntraday().getBid();
					flat.optionAsk = opt.getIntraday().getAsk();
					flat.optionVolume = opt.getIntraday().getTotalVolume();
					flat.spreadPct = (opt.getIntraday().getAsk() - opt.getIntraday().getBid())
							/ item.getStock().getIntraday().getLastTrade() * 100.0;
					flat.yield = 100.0 * opt.getIntraday().getBid() / item.getStock().getIntraday().getLastTrade();
					flat.aYield = annualized(item, opt);
					flatList.add(flat);
				}
			}
		}
		
		flatList.sort((a,b) -> -Double.compare(a.getaYield(), b.getaYield()));
		
		if(request.getParameter("json") != null) {
			response.getWriter().print(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(flatList));
		}
		else {
			request.setAttribute("data", flatList);
			request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
		}
	}

	public static double annualized(Value value, Option opt) {
		LocalDate exp = LocalDate.of(opt.getProduct().getExpirationYear(), opt.getProduct().getExpirationMonth(),
				opt.getProduct().getExpirationDay());
		LocalDate now = LocalDate.now();
		long days = ChronoUnit.DAYS.between(now, exp);
		double yield = opt.getIntraday().getBid() / value.getStock().getIntraday().getLastTrade();
		return 100.0 * (Math.pow(1 + yield, 365.0 / days) - 1);
	}
}
