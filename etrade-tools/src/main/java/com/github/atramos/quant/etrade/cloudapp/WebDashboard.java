package com.github.atramos.quant.etrade.cloudapp;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.atramos.quant.etrade.cloudapp.DashboardModel.Value;

public class WebDashboard extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		CloudantDAO dao = new CloudantDAO();
		List<Value> data = dao.getDashboard();
		request.setAttribute("data", data);
		request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
	}
}
