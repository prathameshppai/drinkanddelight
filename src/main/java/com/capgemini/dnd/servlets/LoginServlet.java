package com.capgemini.dnd.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.capgemini.dnd.dto.Employee;
import com.capgemini.dnd.service.EmployeeService;
import com.capgemini.dnd.service.EmployeeServiceImpl;
import com.capgemini.dnd.util.JsonUtil;
import com.capgemini.dnd.util.MappingUtil;

public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getRootLogger();

	public LoginServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
		Map<String, String> fieldValueMap = new HashMap<String, String>();
		fieldValueMap = MappingUtil.convertJsonObjectToFieldValueMap(request);

		EmployeeService employeeService = new EmployeeServiceImpl();
		Employee employee = new Employee();
		employee.setUsername(fieldValueMap.get("username"));
		employee.setPassword(fieldValueMap.get("password"));

		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers",
				"Content-Type, Authorization, Content-Length, X-Requested-With");
		response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST");

		try {
			if (employeeService.login(employee)) {
				HttpSession session = request.getSession();
				session.setAttribute("username", request.getParameter("username"));
				RequestDispatcher rd = request.getRequestDispatcher("/homepage.html");
				rd.forward(request, response);
			}
		} catch (Exception exception) {
			String jsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
			response.getWriter().write(ServletConstants.ALERT_PREFIX + jsonMessage + ServletConstants.ALERT_SUFFIX);
			RequestDispatcher rd = request.getRequestDispatcher("/loginpage.html");
			rd.include(request, response);
		}
	}
}