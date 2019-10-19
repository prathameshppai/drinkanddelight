package com.capgemini.dnd.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.capgemini.dnd.customexceptions.ExitDateException;
import com.capgemini.dnd.customexceptions.ProductOrderIDDoesNotExistException;
import com.capgemini.dnd.dto.ProductStock;
import com.capgemini.dnd.service.ProductService;
import com.capgemini.dnd.service.ProductServiceImpl;
import com.capgemini.dnd.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UpdateExitDateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public UpdateExitDateServlet() {
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
		ProductService productServiceObject = new ProductServiceImpl();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		
		response.setHeader("Access-Control-Allow-Headers" ,"Content-Type, Authorization, Content-Length, X-Requested-With");
		response.setHeader("Access-Control-Allow-Methods","GET, OPTIONS, HEAD, PUT, POST");
		
		String errorMessage = "";
		Date exitDate = null;
		
		StringBuffer jb = new StringBuffer();
		  String line = null;
		  try {
		    BufferedReader reader = request.getReader();
		    while ((line = reader.readLine()) != null)
		      jb.append(line);
		  } catch (Exception e) {  }
		Map<String,String> myMap = new HashMap<String, String>();

		ObjectMapper objectMapper = new ObjectMapper();
		
		myMap = objectMapper.readValue(jb.toString(), HashMap.class);
		
		String id = myMap.get("OrderId");
		
		try {
			productServiceObject.doesProductOrderIdExist(id);
			try {
				exitDate = sdf.parse(myMap.get("ExitDate"));
			} catch (ParseException exception) {
				errorMessage = exception.getMessage();
			}
		} catch (ProductOrderIDDoesNotExistException | ConnectionException | SQLException exception) {
			errorMessage = exception.getMessage();
		}

		try {
			if (errorMessage.isEmpty()) {
				if (productServiceObject.exitDateCheck(new ProductStock(id, exitDate))) {
					String exitDateJsonMessage = productServiceObject.updateExitDateinStock(new ProductStock(id, exitDate));
//					response.getWriter()
//							.write("<script> alert(\"" + "Exit date updated successfully!" + "\")</script>");
					
					response.getWriter().write(exitDateJsonMessage);

//					RequestDispatcher rd1 = request.getRequestDispatcher("/updateExitDate.html");
//					rd1.include(request, response);
				}
			} else {
				String errorJsonMessage = JsonUtil.convertJavaToJson(errorMessage);
//				response.getWriter().write("<script> alert(\"" + errorMessage + "\")</script>");
				
				response.getWriter().write(errorJsonMessage);
//				
				RequestDispatcher rd1 = request.getRequestDispatcher("/updateExitDate.html");
//				rd1.include(request, response);
			}
		} catch (ExitDateException | SQLException | ConnectionException exception) {
			errorMessage += exception.getMessage();
			String errorJsonMessage = JsonUtil.convertJavaToJson(errorMessage);
			
//			response.getWriter().write("<script> alert(\"" + errorMessage + "\")</script>");
			response.getWriter().write(errorJsonMessage);
			
			
//			RequestDispatcher rd1 = request.getRequestDispatcher("/updateExitDate.html");
//			rd1.include(request, response);
		}
	}
	}
