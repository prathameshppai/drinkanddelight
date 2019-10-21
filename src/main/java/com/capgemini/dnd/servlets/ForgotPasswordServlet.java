package com.capgemini.dnd.servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.capgemini.dnd.customexceptions.BackEndException;
import com.capgemini.dnd.customexceptions.RowNotFoundException;
import com.capgemini.dnd.dto.Employee;
import com.capgemini.dnd.service.EmployeeService;
import com.capgemini.dnd.service.EmployeeServiceImpl;
import com.capgemini.dnd.util.JsonUtil;
import com.capgemini.dnd.util.MappingUtil;

/**
 * Servlet implementation class ForgotPasswordServlet
 */
public class ForgotPasswordServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6504779941597146715L;

	public ForgotPasswordServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);

		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers",
				"Content-Type, Authorization, Content-Length, X-Requested-With");
		response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST");
		Map<String, String> fieldValueMap = MappingUtil.convertJsonObjectToFieldValueMap(request);
		EmployeeService employeeService = new EmployeeServiceImpl();

		Employee employee = new Employee();
		employee.setUsername(fieldValueMap.get("username"));

		try {
			if (employeeService.doesEmployeeExist(employee) == 1) {
				String jsonMessage = JsonUtil.convertJavaToJson("Username exists.");
				response.getWriter().write(jsonMessage);
			}
		} catch (BackEndException | RowNotFoundException exception) {
			String errorJsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
			response.getWriter().write(errorJsonMessage);
		}
	}

}
