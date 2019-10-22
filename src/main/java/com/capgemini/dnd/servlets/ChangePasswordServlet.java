package com.capgemini.dnd.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.capgemini.dnd.customexceptions.BackEndException;
import com.capgemini.dnd.customexceptions.InvalidPasswordException;
import com.capgemini.dnd.customexceptions.PasswordException;
import com.capgemini.dnd.customexceptions.RowNotFoundException;
import com.capgemini.dnd.customexceptions.UnregisteredEmployeeException;
import com.capgemini.dnd.customexceptions.WrongSecurityAnswerException;
import com.capgemini.dnd.dto.Employee;
import com.capgemini.dnd.service.EmployeeService;
import com.capgemini.dnd.service.EmployeeServiceImpl;
import com.capgemini.dnd.util.MappingUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ChangePasswordServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7888060854228943686L;

	public ChangePasswordServlet() {
		super();
	}

	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers",
				"Content-Type, Authorization, Content-Length, X-Requested-With");
		response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST");
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers",
				"Content-Type, Authorization, Content-Length, X-Requested-With");
		response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST");

		Map<String, String> fieldValueMap = MappingUtil.convertJsonObjectToFieldValueMap(request);
		EmployeeService employeeService = new EmployeeServiceImpl();
		Employee tempEmployee=new Employee();
		tempEmployee.setUsername(fieldValueMap.get("username"));
		Employee idealEmployee = null;
		try {
			idealEmployee = employeeService.fetchOneConfidentialDetail(tempEmployee);
		} catch (BackEndException e) {
		}
		
		Employee actualEmployee = new Employee();
		actualEmployee.setUsername(fieldValueMap.get("username"));
		actualEmployee.setSecurityAnswer(fieldValueMap.get("answer"));
		actualEmployee.setPassword(fieldValueMap.get("newPassword"));
		actualEmployee.setConfirmPassword(fieldValueMap.get("confirmPassword"));
		System.out.println(actualEmployee.getUsername()+" "+actualEmployee.getSecurityAnswer()+" "+actualEmployee.getPassword()+" "+actualEmployee.getConfirmPassword());
		ObjectMapper mapper = new ObjectMapper();
		JsonNode dataResponse = mapper.createObjectNode();
		
		try {
			if (employeeService.changePassword(idealEmployee, actualEmployee)) {
				((ObjectNode) dataResponse).put("success", true);
				((ObjectNode) dataResponse).put("message", ServletConstants.PASSWORD_CHANGE_SUCCESSFUL_MESSAGE);
				((ObjectNode) dataResponse).put("username", actualEmployee.getUsername());
			}
		} catch (UnregisteredEmployeeException | WrongSecurityAnswerException | PasswordException | BackEndException
				| InvalidPasswordException | RowNotFoundException exception) {
			((ObjectNode) dataResponse).put("success", false);
			((ObjectNode) dataResponse).put("message", exception.getMessage());
		} finally {
			out.print(dataResponse);
		}

	}

}
