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

	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Map<String, String> fieldValueMap = new HashMap<String, String>();
		fieldValueMap = MappingUtil.convertJsonObjectToFieldValueMap(request);
		EmployeeService employeeService = new EmployeeServiceImpl();
		Employee employee = new Employee();
		Employee idealEmployee = new Employee();
		employee.setUsername(fieldValueMap.get("username"));

		try {
			employeeService.doesEmployeeExist(employee);
			idealEmployee = employeeService.fetchOneConfidentialDetail(employee); // complete login credentials
			/*
			 * String upperhtml = "<!DOCTYPE html>\r\n" + "<html>\r\n" + "    \r\n" +
			 * "<head>\r\n" + "<meta charset=\"ISO-8859-1\">\r\n" +
			 * "	<title>Security Question - Drink and Delight</title>\r\n" + "\r\n" +
			 * "	\r\n" + "\r\n" +
			 * "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\" integrity=\"sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm\" crossorigin=\"anonymous\">\r\n"
			 * +
			 * "<script src=\"https://code.jquery.com/jquery-3.2.1.slim.min.js\" integrity=\"sha384-KJ3o2DKtIkvYIK3UENzmM7KCkRr/rE9/Qpg6aAZGJwFDMVNA/GpGFF93hXpG5KkN\" crossorigin=\"anonymous\"></script>\r\n"
			 * +
			 * "<script src=\"https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js\" integrity=\"sha384-ApNbgh9B+Y1QKtv3Rn7W3mgPxhU9K/ScQsAP7hUibX39j7fakFPskvXusvfa0b4Q\" crossorigin=\"anonymous\"></script>\r\n"
			 * +
			 * "<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js\" integrity=\"sha384-JZR6Spejh4U02d8jOt6vLEHfe/JQGiRRSQQxSfFWpi1MquVdAyjUar5+76PVCmYl\" crossorigin=\"anonymous\"></script>\r\n"
			 * +
			 * "<link rel=\"stylesheet\" type=\"text/css\" href=\"C:\\Users\\dgupta23\\eclipse-workspace\\MyBiodata\\WebContent\\customstylelogin.css\">\r\n"
			 * + "</head>\r\n" + "\r\n" + "<body>\r\n" +
			 * "	<div class=\"container h-100\">\r\n" +
			 * "		<div class=\"d-flex justify-content-center h-100\">\r\n" +
			 * "			<div class=\"user_card\">\r\n" +
			 * "				<div class=\"d-flex justify-content-center\">\r\n" +
			 * "					<div class=\"brand_logo_container\">\r\n" +
			 * "					<img src=\"images\\logo.png\" class=\"brand_logo img-circle\" alt=\"Logo\">\r\n"
			 * + "					</div>\r\n" + "				</div>\r\n" +
			 * "				<div class=\"d-flex justify-content-center form_container\">\r\n"
			 * +
			 * "					<form action=\"SecurityQuestionServlet\" method=\"post\">\r\n"
			 * +
			 * "						<div class=\"container\" class=\"form-check\" class=\"was-validated\">\r\n"
			 * + "						<div class=\"input-group mb-3\">\r\n" +
			 * "							<div class=\"input-group-append\">\r\n" +
			 * "								<b>"; upperhtml += securityQuestion;
			 * upperhtml += "</b>\r\n" + "							</div>\r\n" +
			 * "							\r\n" + "						</div>\r\n" +
			 * "						\r\n" + "						\r\n" +
			 * "						<div class=\"input-group mb-3\">\r\n" +
			 * "							<div class=\"input-group-append\">\r\n" +
			 * "								\r\n" +
			 * "							</div>\r\n" +
			 * "							<input type=\"text\" name=\"securityAnswer\" class=\"form-control input_user\" value=\"\" placeholder=\"Answer security question\" required>\r\n"
			 * + "						</div>\r\n" + "						\r\n" +
			 * "						<div class=\"input-group mb-3\">\r\n" +
			 * "							<div class=\"input-group-append\">\r\n" +
			 * "								\r\n" +
			 * "							</div>\r\n" +
			 * "							<input type=\"password\" name=\"password\" class=\"form-control input_user\" value=\"\" placeholder=\"New Password\" required>\r\n"
			 * + "						</div>\r\n" + "						\r\n" +
			 * "						<div class=\"input-group mb-3\">\r\n" +
			 * "							<div class=\"input-group-append\">\r\n" +
			 * "								\r\n" +
			 * "							</div>\r\n" +
			 * "							<input type=\"password\" name=\"confirmPassword\" class=\"form-control input_user\" value=\"\" placeholder=\"Confirm New Password\" required>\r\n"
			 * + "						</div>\r\n" +
			 * "						<div class=\"d-flex justify-content-center mt-3 login_container\">\r\n"
			 * +
			 * "					<button type=\"submit\" name=\"button\" class=\"btn login_btn\">Submit</button>\r\n"
			 * + "				</div>\r\n" + "				</div>\r\n" +
			 * "				</form>\r\n" + "				</div>\r\n" +
			 * "				</div>\r\n" + "				</div>\r\n" +
			 * "				</div>\r\n" + "				</body>\r\n" +
			 * "				</html>\r\n" + "				\r\n" + "";
			 * 
			 * 
			 * response.getWriter().write(upperhtml);
			 */
			getServletContext().setAttribute("passedEmployee", idealEmployee);
			RequestDispatcher rd = request.getRequestDispatcher("/SecurityQuestion.html");
			rd.include(request, response);

		} catch (BackEndException | RowNotFoundException exception) {
			String jsonMessage = JsonUtil.convertJavaToJson(exception.getMessage());
			response.getWriter().write(ServletConstants.ALERT_PREFIX + jsonMessage + ServletConstants.ALERT_SUFFIX);
			RequestDispatcher rd = request.getRequestDispatcher("/ForgotPassword.html");
			rd.include(request, response);
		}

	}

}
