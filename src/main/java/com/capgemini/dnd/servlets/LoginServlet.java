package com.capgemini.dnd.servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.capgemini.dnd.dto.Employee;
import com.capgemini.dnd.service.EmployeeService;
import com.capgemini.dnd.service.EmployeeServiceImpl;
import com.capgemini.dnd.util.MappingUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getRootLogger();

	public LoginServlet() {
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

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers",
				"Content-Type, Authorization, Content-Length, X-Requested-With");
		response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST");

		Map<String, String> fieldValueMap = MappingUtil.convertJsonObjectToFieldValueMap(request);
		EmployeeService employeeService = new EmployeeServiceImpl();
		Employee employee = new Employee();
		employee.setUsername(fieldValueMap.get("username"));
		employee.setPassword(fieldValueMap.get("password"));

		ObjectMapper mapper = new ObjectMapper();
		JsonNode dataResponse = mapper.createObjectNode();
		try {
			if (employeeService.login(employee)) {
				((ObjectNode) dataResponse).put("message", ServletConstants.LOGIN_SUCCESSFUL_MESSAGE);
				((ObjectNode) dataResponse).put("username", employee.getUsername());
			}
		} catch (Exception exception) {
			((ObjectNode) dataResponse).put("message", exception.getMessage());
		} finally {
			response.getWriter().print(dataResponse);
		}
	}
}