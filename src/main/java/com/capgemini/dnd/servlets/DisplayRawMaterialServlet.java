package com.capgemini.dnd.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.capgemini.dnd.dto.DisplayRawMaterialOrder;
import com.capgemini.dnd.dto.RawMaterialOrder;
import com.capgemini.dnd.service.RawMaterialService;
import com.capgemini.dnd.service.RawMaterialServiceImpl;
import com.capgemini.dnd.util.JsonUtil;
import com.capgemini.dnd.util.MappingUtil;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMessages;

/**
 * Servlet implementation class DisplayRawMaterialServlet
 */
public class DisplayRawMaterialServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
    public DisplayRawMaterialServlet() {
        super();
       
    }
    
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
    	
    	String requestOrigin = request.getHeader("Origin");
    	if(requestOrigin == null) {
    		requestOrigin = "*";
    	}
    	System.out.println("Request Origin = " + requestOrigin);
    	response.setHeader("Access-Control-Allow-Origin", requestOrigin);
		
		response.setHeader("Access-Control-Allow-Headers" ,"Content-Type, Authorization, Content-Length, X-Requested-With");
		response.setHeader("Access-Control-Allow-Methods","GET, OPTIONS, HEAD, PUT, POST");
		response.setHeader("Access-Control-Allow-Credentials", "true");
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
	
	}

	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		System.out.println("entered servlet");
		doGet(req, res);
		res.setContentType("application/json");
		res.setHeader("Access-Control-Allow-Origin", "*");
		res.setHeader("Access-Control-Allow-Headers" ,"Content-Type, Authorization, Content-Length, X-Requested-With");
		res.setHeader("Access-Control-Allow-Methods","GET, OPTIONS, HEAD, PUT, POST");
		PrintWriter out = res.getWriter();
		String jsonMessage="";
		String errorMessage = "";
		
		List<RawMaterialOrder> rmoList = new ArrayList<RawMaterialOrder>();
		RawMaterialService rawmaterialServiceObject = new RawMaterialServiceImpl();
		DisplayRawMaterialOrder displayRawMaterialOrderObject = new DisplayRawMaterialOrder();	
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");	
	 Map<String,String> fieldValueMap = new HashMap<String, String>();
	 fieldValueMap =MappingUtil.convertJsonObjectToFieldValueMap(req);
	
	        String DeliveryStatusVar= fieldValueMap.get("deliveryStatus");
	        String SupplierIDVar= fieldValueMap.get("supplierid");
	        String  date1Var= fieldValueMap.get("startdate");
	        String  date2Var= fieldValueMap.get("endDate");
//	        if( date1Var != null && date1Var != null )
//	        		{
//	        	
//	        	Calendar c = Calendar.getInstance();
//	        	try {
//					c.setTime(sdf.parse(date2Var));
//				} catch (ParseException e) {
//					
//					e.printStackTrace();
//				}
//	        	c.add(Calendar.DATE, 1);  // number of days to add
//	        	date2Var = sdf.format(c.getTime());  
//	        	System.out.println(date2Var);
//	        		}
	     
	    displayRawMaterialOrderObject.setDeliveryStatus(DeliveryStatusVar);
		displayRawMaterialOrderObject.setSupplierid(SupplierIDVar);
		displayRawMaterialOrderObject.setStartdate(date1Var);
		displayRawMaterialOrderObject.setEndDate(date2Var);

		try {
			jsonMessage = rawmaterialServiceObject.displayRawmaterialOrders(displayRawMaterialOrderObject);
			
		} catch (Exception e) {
			
			errorMessage = e.getMessage();
		}
			if(errorMessage.isEmpty()) {
			out.write(jsonMessage);
		}
//		else {
//			String errorJsonMessage = JsonUtil.convertJavaToJson1(errorMessage);
//			out.write(errorJsonMessage);
//
//		}
	}
}
			

	
	

	
	
	

