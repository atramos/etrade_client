package com.github.atramos.quant.etrade.cloudapp;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.atramos.quant.etrade.cloudapp.DashboardModel.Value;

public class DashboardTest {

	Logger logger = Logger.getLogger(getClass().getName());
	
	@Test
	public void testRead() throws JsonProcessingException, IOException {
		
		CloudantDAO dao = new CloudantDAO();
		List<Value> data = dao.getDashboard();
		
		logger.info(data.toString());
	}
}
