package com.capgemini.dnd.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.capgemini.dnd.customexceptions.ConnectionException;
import com.capgemini.dnd.customexceptions.DisplayException;
import com.capgemini.dnd.customexceptions.RMOrderNotAddedException;
import com.capgemini.dnd.dto.RawMaterialOrder;
import com.capgemini.dnd.service.RawMaterialService;
import com.capgemini.dnd.service.RawMaterialServiceImpl;
import com.capgemini.dnd.util.JsonUtil;
import com.capgemini.dnd.util.MappingUtil;

public class PlaceRMOrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public PlaceRMOrderServlet() {
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers",
				"Content-Type, Authorization, Content-Length, X-Requested-With");
		response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST");

		RawMaterialService rawMaterialService = new RawMaterialServiceImpl();

		Map<String, String> myMap = MappingUtil.convertJsonObjectToFieldValueMap(request);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		RawMaterialOrder rawMaterialOrder = new RawMaterialOrder();
		try {
			rawMaterialOrder = new RawMaterialOrder(myMap.get("name"), myMap.get("supplierId"),
					Double.parseDouble(myMap.get("quantityValue")), myMap.get("quantityUnit"),
					sdf.parse(myMap.get("dateOfDelivery")), Double.parseDouble(myMap.get("pricePerUnit")),
					myMap.get("warehouseId"));
		} catch (NumberFormatException | ParseException exception) {
			response.getWriter().write(JsonUtil.convertJavaToJson(exception.getMessage()));
		}

		Date today = new Date();
		rawMaterialOrder.setDateOfOrder(today);
		rawMaterialOrder.setDeliveryStatus("Pending");

		try {

			String jsonMessage = rawMaterialService.placeRawMaterialOrder(rawMaterialOrder);
			response.getWriter().write(jsonMessage);

		} catch (RMOrderNotAddedException | ConnectionException | SQLException | DisplayException exception) {
			String errorJsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
			response.getWriter().write(errorJsonMessage);

		}
	}
}
