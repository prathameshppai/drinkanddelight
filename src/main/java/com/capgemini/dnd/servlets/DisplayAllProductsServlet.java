package com.capgemini.dnd.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.capgemini.dnd.dto.DisplayProductOrder;
import com.capgemini.dnd.service.ProductService;
import com.capgemini.dnd.service.ProductServiceImpl;
import com.capgemini.dnd.util.MappingUtil;

public class DisplayAllProductsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public DisplayAllProductsServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) {

		String requestOrigin = request.getHeader("Origin");
		if (requestOrigin == null) {
			requestOrigin = "*";
		}
		System.out.println("Request Origin = " + requestOrigin);
		response.setHeader("Access-Control-Allow-Origin", requestOrigin);

		response.setHeader("Access-Control-Allow-Headers",
				"Content-Type, Authorization, Content-Length, X-Requested-With");
		response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST");
		response.setHeader("Access-Control-Allow-Credentials", "true");
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		res.setContentType("application/json");
		res.setHeader("Access-Control-Allow-Origin", "*");
		res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Content-Length, X-Requested-With");
		res.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST");
		PrintWriter out = res.getWriter();
		String jsonMessage = "";
		String errorMessage = "";

		ProductService productServiceObject = new ProductServiceImpl();
		DisplayProductOrder displayProductOrderObject = new DisplayProductOrder();

		Map<String, String> fieldValueMap = new HashMap<String, String>();
		fieldValueMap = MappingUtil.convertJsonObjectToFieldValueMap(req);

		String DeliveryStatusVar = fieldValueMap.get("deliveryStatus");
		String DistributorIDVar = fieldValueMap.get("distributorid");
		String date1Var = fieldValueMap.get("startdate");
		String date2Var = fieldValueMap.get("endDate");

		displayProductOrderObject.setDeliveryStatus(DeliveryStatusVar);
		displayProductOrderObject.setDistributorid(DistributorIDVar);
		displayProductOrderObject.setStartdate(date1Var);
		displayProductOrderObject.setEndDate(date2Var);

		try {
			jsonMessage = productServiceObject.displayProductOrders(displayProductOrderObject);

		} catch (Exception e) {

			errorMessage = e.getMessage();
		}
		if (errorMessage.isEmpty()) {
			out.write(jsonMessage);
		}

	}
}
