package com.capgemini.dnd.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
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

public class SecurityQuestionServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7888060854228943686L;

	public SecurityQuestionServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Map<String, String> fieldValueMap = new HashMap<String, String>();
		fieldValueMap = MappingUtil.convertJsonObjectToFieldValueMap(request);

		Employee idealEmployee = (Employee) getServletContext().getAttribute("passedEmployee");

		Employee actualEmployee = new Employee();
		actualEmployee.setUsername(idealEmployee.getUsername());
		actualEmployee.setSecurityAnswer(fieldValueMap.get("securityAnswer"));
		actualEmployee.setPassword(fieldValueMap.get("password"));
		actualEmployee.setConfirmPassword(fieldValueMap.get("confirmPassword"));

		EmployeeService employeeService = new EmployeeServiceImpl();

		try {
			if (employeeService.changePassword(idealEmployee, actualEmployee)) {
				response.getWriter().write(ServletConstants.PASSWORD_CHANGE_SUCCESS_PREFIX
						+ actualEmployee.getUsername() + ServletConstants.PASSWORD_CHANGE_SUCCESS_SUFFIX);
				RequestDispatcher rd = request.getRequestDispatcher("/loginpage.html");
				rd.include(request, response);
			}
		} catch (UnregisteredEmployeeException | WrongSecurityAnswerException | PasswordException | BackEndException
				| InvalidPasswordException | RowNotFoundException exception) {
			response.getWriter()
					.write(ServletConstants.ALERT_PREFIX + exception.getMessage() + ServletConstants.ALERT_SUFFIX);
			RequestDispatcher rd = request.getRequestDispatcher("/ForgotPassword.html");
			rd.include(request, response);
		}

	}

}
