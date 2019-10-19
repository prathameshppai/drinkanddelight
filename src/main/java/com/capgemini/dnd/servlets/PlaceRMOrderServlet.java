package com.capgemini.dnd.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.capgemini.dnd.customexceptions.ConnectionException;
import com.capgemini.dnd.customexceptions.DateValueException;
import com.capgemini.dnd.customexceptions.DisplayException;
import com.capgemini.dnd.customexceptions.RMOrderNotAddedException;
import com.capgemini.dnd.dao.Constants;
import com.capgemini.dnd.dto.RawMaterialOrder;
import com.capgemini.dnd.service.RawMaterialService;
import com.capgemini.dnd.service.RawMaterialServiceImpl;
import com.capgemini.dnd.util.JsonUtil;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PlaceRMOrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public PlaceRMOrderServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
		
//		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
//		HttpSession session = request.getSession();
//		if(session.getAttribute("username") == null) {
//			RequestDispatcher rd = request.getRequestDispatcher("/loginpage.html");
//			rd.include(request, response);
//		}
		
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers" ,"Content-Type, Authorization, Content-Length, X-Requested-With");
		response.setHeader("Access-Control-Allow-Methods","GET, OPTIONS, HEAD, PUT, POST");
		
		RawMaterialService rawMaterialService = new RawMaterialServiceImpl();
//		RawMaterialOrder rawMaterialOrder = new RawMaterialOrder();
//		String errorMessage = "";
		
		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
		  BufferedReader reader = request.getReader();
		  while ((line = reader.readLine()) != null)
			  jb.append(line);
		} catch (Exception e) {  }
		
		ObjectMapper objectMapper = new ObjectMapper();
//		objectMapper.configure(Feature.AUTO_CLOSE_SOURCE, true);


//		JsonNode carJsonNode = objectMapper.readTree(jb.toString());
//		rawMaterialOrder = objectMapper.treeToValue(carJsonNode, RawMaterialOrder.class);
		
		Map<String,String> myMap = new HashMap<String, String>();
		myMap = objectMapper.readValue(jb.toString(), HashMap.class);
			
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd") ;
		//		rawMaterialService.placeRawMaterialOrder(newRawMaterialOrder)
		RawMaterialOrder rawMaterialOrder = new RawMaterialOrder();
		try {
			rawMaterialOrder = new RawMaterialOrder(myMap.get("name"),myMap.get("supplierId"),Double.parseDouble(myMap.get("quantityValue")),myMap.get("quantityUnit"),sdf.parse(myMap.get("dateOfDelivery")),Double.parseDouble(myMap.get("pricePerUnit")),myMap.get("warehouseId"));
		} catch (NumberFormatException | ParseException exception) {
			response.getWriter().write(JsonUtil.convertJavaToJson(exception.getMessage()));
		}

		Date today = new Date();
		rawMaterialOrder.setDateOfOrder(today);
		rawMaterialOrder.setDeliveryStatus("Pending");
		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//		rawMaterialOrder.setName(request.getParameter("RMName"));
//		rawMaterialOrder.setSupplierId(request.getParameter("SUPID"));
//
//		double quantity = 0;
//		try {
//			quantity = Double.parseDouble(request.getParameter("Quantity"));
//			rawMaterialOrder.setQuantityValue(quantity);
//		} catch (NumberFormatException exception) {
//			errorMessage += "\\nEnter quantity in decimal";
//		}
//
//		rawMaterialOrder.setQuantityUnit(request.getParameter("QuantityUnit"));
//		LocalDate localDate = LocalDate.now();
//		
//		
//		Date expectedDateofDelivery = null;
//		try {
//			expectedDateofDelivery = sdf.parse(request.getParameter("expectedDateofDelivery"));
//			if(today.compareTo(expectedDateofDelivery) >= 0)
//				throw new DateValueException("Delivery date cannot be earlier or today's date");
//			rawMaterialOrder.setDateOfDelivery(expectedDateofDelivery);
//		} catch (ParseException  | DateValueException exception) {
//			errorMessage += "\\n" + Constants.PARSE_EXCEPTION_INVALID_FORMAT;
//		} 
//
//		double price_per_unit = 0;
//		try {
//			price_per_unit = Double.parseDouble(request.getParameter("price_per_unit"));
//			rawMaterialOrder.setPricePerUnit(price_per_unit);
//		} catch (NumberFormatException exception) {
//			errorMessage += "\\nEnter Price per Unit in decimal";
//		}
//		
		
//		rawMaterialOrder.setWarehouseId(request.getParameter("warehouseId"));
		
		try {
//			if (errorMessage.isEmpty()) {
				String jsonMessage = rawMaterialService.placeRawMaterialOrder(rawMaterialOrder);
				response.getWriter().write(jsonMessage);
//				String message = "<script> alert(\"Order placed successfully\")</script>";
//				response.getWriter().write(message);
//				RequestDispatcher rd = request.getRequestDispatcher("/PlaceRMOrder.html");
//				rd.include(request, response);
//			}
//			else {
				
//				String message = "<script> alert(\"" + errorMessage + "\")</script>";
//				response.getWriter().write(message);
//				RequestDispatcher rd = request.getRequestDispatcher("/PlaceRMOrder.html");
//				rd.include(request, response);
//			}
			
		} catch (RMOrderNotAddedException | ConnectionException | SQLException | DisplayException exception) {
			String errorJsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
			response.getWriter().write(errorJsonMessage);
			
//			errorMessage += exception.getMessage();
//			String message = "<script> alert(\"" + errorMessage + "\")</script>";
//			response.getWriter().write(message);
//			RequestDispatcher rd = request.getRequestDispatcher("/PlaceRMOrder.html");
//			rd.include(request, response);	
		}
	}	
}
