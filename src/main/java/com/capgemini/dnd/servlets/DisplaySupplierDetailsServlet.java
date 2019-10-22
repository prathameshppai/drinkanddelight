package com.capgemini.dnd.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.capgemini.dnd.dto.RawMaterialOrder;
import com.capgemini.dnd.dto.Supplier;
import com.capgemini.dnd.service.RawMaterialService;
import com.capgemini.dnd.service.RawMaterialServiceImpl;
import com.capgemini.dnd.util.MappingUtil;

public class DisplaySupplierDetailsServlet extends HttpServlet {

	private static final long serialVersionUID = -2533108334915112229L;

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

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

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		doGet(req, res);
		String errorMessage="";
		String jsonMessage="";
		res.setContentType("application/json");
		res.setHeader("Access-Control-Allow-Origin", "*");
		res.setHeader("Access-Control-Allow-Headers" ,"Content-Type, Authorization, Content-Length, X-Requested-With");
		res.setHeader("Access-Control-Allow-Methods","GET, OPTIONS, HEAD, PUT, POST");
		
//		HttpSession session = req.getSession();
//		if(session.getAttribute("username") == null) {
//			RequestDispatcher rd = req.getRequestDispatcher("/loginpage.html");
//			rd.include(req, res);
//		}
		PrintWriter out = res.getWriter();
		RawMaterialService rawmaterialServiceObject = new RawMaterialServiceImpl();
		Supplier supplierDetails = new Supplier();
		Map<String,String> fieldValueMap = new HashMap<String, String>();
		fieldValueMap = MappingUtil.convertJsonObjectToFieldValueMap(req);
		String supplierId = fieldValueMap.get("supplierId");
		supplierDetails.setSupplierId(supplierId);
		try {
			 jsonMessage = rawmaterialServiceObject.fetchSupplierDetail(supplierDetails);
			
		}
		catch (Exception e) {

			e.printStackTrace();
		}
		if(errorMessage.isEmpty()) {
			out.write(jsonMessage);
		}
	}
}
		
		
		
		
		
