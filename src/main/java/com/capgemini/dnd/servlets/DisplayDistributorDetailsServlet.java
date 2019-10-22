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

import com.capgemini.dnd.dto.Distributor;
import com.capgemini.dnd.dto.RawMaterialOrder;
import com.capgemini.dnd.dto.Supplier;
import com.capgemini.dnd.service.ProductService;
import com.capgemini.dnd.service.ProductServiceImpl;
import com.capgemini.dnd.service.RawMaterialService;
import com.capgemini.dnd.service.RawMaterialServiceImpl;
import com.capgemini.dnd.util.MappingUtil;

public class DisplayDistributorDetailsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

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
		PrintWriter out = res.getWriter();
        ProductService productServiceObject = new ProductServiceImpl();
		Distributor distributorDetails = new Distributor();
		Map<String,String> fieldValueMap = new HashMap<String, String>();
		fieldValueMap = MappingUtil.convertJsonObjectToFieldValueMap(req);
		String DistributorID = fieldValueMap.get("distributorId");
		int addressId = Integer.parseInt(fieldValueMap.get("addressId"));
		distributorDetails.setDistributorId(DistributorID);
		distributorDetails.setAddressId(addressId);
		try {
	 jsonMessage = productServiceObject.fetchCompleteDistributorDetail(distributorDetails);
	
		}
		catch (Exception e) {

	e.printStackTrace();
		}
		if(errorMessage.isEmpty()) {
	out.write(jsonMessage);
		}
	}

	}
	
