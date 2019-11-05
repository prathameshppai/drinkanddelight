package com.capgemini.dnd.controller;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.capgemini.dnd.customexceptions.BackEndException;
import com.capgemini.dnd.dto.Employee;
import com.capgemini.dnd.service.EmployeeService;
import com.capgemini.dnd.service.EmployeeServiceImpl;
import com.capgemini.dnd.servlets.ServletConstants;
import com.capgemini.dnd.util.MappingUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
@CrossOrigin(origins = "*")
@RequestMapping("/")
public class LoginController {
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private Employee employee;
	
	@RequestMapping(method = RequestMethod.POST)
	public void login(HttpServletRequest request, HttpServletResponse response)
			throws BackEndException, JsonParseException, JsonMappingException, IOException {
		System.out.println("In login controller");
		Map<String, String> fieldValueMap = MappingUtil.convertJsonObjectToFieldValueMap(request);
		// employeeService = new EmployeeServiceImpl();
		employee = new Employee();
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
			System.out.println(exception.getMessage());
			((ObjectNode) dataResponse).put("message", exception.getMessage());
		} 
		response.getWriter().print(dataResponse);
	}
}