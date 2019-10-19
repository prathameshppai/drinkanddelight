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
import com.capgemini.dnd.customexceptions.ExpiryDateException;
import com.capgemini.dnd.customexceptions.ManufacturingDateException;
import com.capgemini.dnd.customexceptions.ProductOrderIDDoesNotExistException;
import com.capgemini.dnd.customexceptions.RMOrderIDDoesNotExistException;
import com.capgemini.dnd.dto.ProductStock;
import com.capgemini.dnd.dto.RawMaterialStock;
import com.capgemini.dnd.service.ProductService;
import com.capgemini.dnd.service.ProductServiceImpl;
import com.capgemini.dnd.service.RawMaterialService;
import com.capgemini.dnd.service.RawMaterialServiceImpl;
import com.capgemini.dnd.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;


public class UpdateProductStockServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public UpdateProductStockServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
//		  res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
//			HttpSession session = req.getSession();
//			if(session.getAttribute("username") == null) {
//				RequestDispatcher rd = req.getRequestDispatcher("/loginpage.html");
//				rd.include(req, res);
//			}
		
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		
		response.setHeader("Access-Control-Allow-Headers" ,"Content-Type, Authorization, Content-Length, X-Requested-With");
		response.setHeader("Access-Control-Allow-Methods","GET, OPTIONS, HEAD, PUT, POST");
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		ProductService productServiceObject = new ProductServiceImpl();
		String errorMessage = "";

//		String OrderId = req.getParameter("OrderId");
		Date manufacturingDate = null;
		Date expiryDate = null;
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
				try {
					manufacturingDate = sdf.parse(myMap.get("ManufacturingDate"));
					if(productServiceObject.validateManufacturingDate(manufacturingDate)) {
						try {
							expiryDate = sdf.parse(myMap.get("ExpiryDate"));
							if(productServiceObject.validateExpiryDate(manufacturingDate, expiryDate)) {
								String qaStatus = myMap.get("QAStatus");
								String message = productServiceObject.updateProductStock(new ProductStock(id, manufacturingDate, expiryDate, qaStatus));
								response.getWriter().write(message);
//								res.getWriter().write("<script> alert(\"" + "Product Stock details updated successfully!" + "\")</script>");
//								RequestDispatcher rd=req.getRequestDispatcher("/UpdateProductStock.html");  
//							    rd.include(req, res);
							    
							}
						} catch (ParseException | ExpiryDateException exception) {
							
							String errorJsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
				        	response.getWriter().write(errorJsonMessage);
							
//							res.getWriter().write(exception.getMessage());
//							
//							RequestDispatcher rd=req.getRequestDispatcher("/UpdateProductStock.html");  
//						    rd.include(req, res);
						}
					}
				} catch (ParseException | ManufacturingDateException exception) {
					
					String errorJsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
		        	response.getWriter().write(errorJsonMessage);
					
//					res.getWriter().write("<script> alert(\"" + exception.getMessage() + "\")</script>");
//					RequestDispatcher rd=req.getRequestDispatcher("/UpdateProductStock.html");  
//				    rd.include(req, res);
				}
			}
		} catch (ProductOrderIDDoesNotExistException | ConnectionException | SQLException exception) {
			
			String errorJsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
        	response.getWriter().write(errorJsonMessage);
			
//			res.getWriter().write(exception.getMessage());
//			RequestDispatcher rd=req.getRequestDispatcher("/UpdateRMStock.html");  
//		    rd.include(req, res);
		}
		

		
	}

}
