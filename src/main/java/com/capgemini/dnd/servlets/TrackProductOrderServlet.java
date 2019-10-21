package com.capgemini.dnd.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.capgemini.dnd.customexceptions.ConnectionException;
import com.capgemini.dnd.customexceptions.ProductOrderIDDoesNotExistException;
import com.capgemini.dnd.customexceptions.RMOrderIDDoesNotExistException;
import com.capgemini.dnd.dto.ProductStock;
import com.capgemini.dnd.service.ProductService;
import com.capgemini.dnd.service.ProductServiceImpl;
import com.capgemini.dnd.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TrackProductOrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public TrackProductOrderServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
//		String id = request.getParameter("OrderId");
		ProductService productServiceObject = new ProductServiceImpl();
		
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
			if(productServiceObject.doesProductOrderIdExist(id)) {
				String jsonMessage = productServiceObject.trackProductOrder(new ProductStock(id));
				
//				response.getWriter().write("<script> alert(\"" + jsonMessage + "\")</script>");
				response.getWriter().write(jsonMessage);
//				RequestDispatcher reqd=request.getRequestDispatcher("/trackProductOrder.html");  
//			    reqd.include(request, response);
//			    response.getWriter().write("<script> document.getElementById(\"trackbox\").innerHTML =  \"" +productServiceObject.trackProductOrder(new ProductStock(id))+ "\"</script>");
			}	
		} catch (ConnectionException | SQLException | ProductOrderIDDoesNotExistException exception) {
//			response.getWriter().write("<script> alert(\"" + exception.getMessage() + "\")</script>");
//			RequestDispatcher rd1=request.getRequestDispatcher("/trackProductOrder.html");
//			rd1.include(request, response);
			String errorJsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
			response.getWriter().write(errorJsonMessage);
		}
	}
	}

