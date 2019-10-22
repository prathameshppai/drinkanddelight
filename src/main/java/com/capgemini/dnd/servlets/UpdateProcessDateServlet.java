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
import com.capgemini.dnd.customexceptions.ProcessDateException;
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

 

public class UpdateProcessDateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

 

    public UpdateProcessDateServlet() {
        super();
    }

 

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
    }
    
   

 

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

 

        doGet(request, response);
//        res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
//		HttpSession session = req.getSession();
//		if(session.getAttribute("username") == null) {
//			RequestDispatcher rd = req.getRequestDispatcher("/loginpage.html");
//			rd.include(req, res);
//		}
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        RawMaterialService rawMaterialServiceObject = new RawMaterialServiceImpl();
        
//        String OrderId = req.getParameter("OrderId");
        Date processDate = null;
        response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		
		response.setHeader("Access-Control-Allow-Headers" ,"Content-Type, Authorization, Content-Length, X-Requested-With");
		response.setHeader("Access-Control-Allow-Methods","GET, OPTIONS, HEAD, PUT, POST");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		
		String errorMessage = "";
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
            if (rawMaterialServiceObject.doesRawMaterialOrderIdExist(id)) {
                try {
                    processDate = sdf.parse(myMap.get("ProcessDate"));
                    try {
                        if (rawMaterialServiceObject.processDateCheck(new RawMaterialStock(id, processDate))) {
                        	String processDateJsonMessage	 =rawMaterialServiceObject.updateProcessDateinStock(new RawMaterialStock(id, processDate));
                        	response.getWriter().write(processDateJsonMessage);
//                        	response.getWriter().write("<script> alert(\"" + "Process date updated" + "\")</script>");
//                            RequestDispatcher rd=request.getRequestDispatcher("/updateProcessDate.html");  
//                            rd.include(request, response);
                        }
                    } catch (ProcessDateException exception) {
                    	String errorJsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
                    	response.getWriter().write(errorJsonMessage);
//                    	response.getWriter().write("<script> alert(\"" + exception.getMessage() + "\")</script>");
//                        RequestDispatcher rd = request.getRequestDispatcher("/updateProcessDate.html");
//                        rd.include(request, response);
                    }
                } catch (ParseException exception) {
                	String errorJsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
                	response.getWriter().write(errorJsonMessage);
//                	response.getWriter().write("<script> alert(\"" + exception.getMessage() + "\")</script>");
//                    RequestDispatcher rd = request.getRequestDispatcher("/updateProcessDate.html");
//                    rd.include(request, response);
                }
            }
        } catch (RMOrderIDDoesNotExistException | ConnectionException | SQLException exception) {
        	String errorJsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
        	response.getWriter().write(errorJsonMessage);
//        	response.getWriter().write("<script> alert(\"" + exception.getMessage() + "\")</script>");
//			RequestDispatcher rd = request.getRequestDispatcher("/updateProcessDate.html");
//            rd.include(request, response);
        }
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
}