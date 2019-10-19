package com.capgemini.dnd.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.capgemini.dnd.customexceptions.ConnectionException;
import com.capgemini.dnd.customexceptions.DisplayException;
import com.capgemini.dnd.service.RawMaterialService;
import com.capgemini.dnd.service.RawMaterialServiceImpl;
import com.capgemini.dnd.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GetRawmaterialDetailsServlet extends HttpServlet {

	private static final long serialVersionUID = -2406191068089280898L;

    public GetRawmaterialDetailsServlet() {
        super();
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers" ,"Content-Type, Authorization, Content-Length, X-Requested-With");
		response.setHeader("Access-Control-Allow-Methods","GET, OPTIONS, HEAD, PUT, POST");
		
		RawMaterialService rawMaterialService = new RawMaterialServiceImpl();
		
		ArrayList<ArrayList> rawMaterialDetailsList = new ArrayList<ArrayList>();
		
		try {
				rawMaterialDetailsList.add(rawMaterialService.fetchRawMaterialNames());
				rawMaterialDetailsList.add(rawMaterialService.fetchSupplierIds());
				rawMaterialDetailsList.add(rawMaterialService.fetchWarehouseIds());
				
				response.getWriter().write(JsonUtil.convertJavaToJson1(rawMaterialDetailsList));
			
		} catch (DisplayException | ConnectionException exception) {
			String errorJsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
			response.getWriter().write(errorJsonMessage);
		}
	}

}
