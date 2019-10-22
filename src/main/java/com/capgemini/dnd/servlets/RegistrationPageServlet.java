package com.capgemini.dnd.servlets;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.capgemini.dnd.customexceptions.FullNameException;
import com.capgemini.dnd.customexceptions.InvalidEmailIdException;
import com.capgemini.dnd.customexceptions.PasswordException;
import com.capgemini.dnd.customexceptions.PhoneNoException;
import com.capgemini.dnd.customexceptions.UserNameException;
import com.capgemini.dnd.dao.Constants;
import com.capgemini.dnd.dto.Employee;
import com.capgemini.dnd.service.EmployeeService;
import com.capgemini.dnd.service.EmployeeServiceImpl;
import com.capgemini.dnd.util.DBUtil;
import com.capgemini.dnd.util.InputValidator;

public class RegistrationPageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public RegistrationPageServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		EmployeeService employeeService = new EmployeeServiceImpl();
		Employee employee = new Employee();
		String errorMessage = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String employeeName=null;
		try {
			employeeName=request.getParameter("EmployeeName");
			InputValidator.fullNameValidator(employeeName);
			employee.setEmpName(employeeName);
		} catch (FullNameException exception) {
			errorMessage+="<br> " + exception.getMessage();
		}
		
		employee.setDesignation(request.getParameter("Designation"));
		
		String emailId=null;
		try {
			emailId=request.getParameter("EmailID");
			InputValidator.emailIdValidator(emailId);
			employee.setEmailId(emailId);
		} catch (InvalidEmailIdException exception) {
			errorMessage+="<br> " + exception.getMessage();
		}
			
		String phoneNumber=null;
		try {
			phoneNumber=request.getParameter("PhoneNumber");
			InputValidator.phoneNoValidator(phoneNumber);
			employee.setPhoneNo(phoneNumber);
		} catch (PhoneNoException exception) {
			errorMessage+="<br> " + exception.getMessage();
		}
		
		Date dateOfBirth = null;
		try {
			dateOfBirth = sdf.parse(request.getParameter("DOB"));
			Date minDate = sdf.parse("2000-01-01");
			if(dateOfBirth.before(minDate ))
				employee.setDob(DBUtil.stringtoDate(dateOfBirth));
			else
				errorMessage += "<br>Please enter valid date of birth";
		} catch (ParseException e) {
			errorMessage += "<br> " + Constants.PARSE_EXCEPTION_INVALID_FORMAT;
		}
		
		employee.setGender(request.getParameter("gender"));
		
		String userName=null;
		try {
			userName=request.getParameter("UserName");
			InputValidator.userNameValidator(userName);
			employee.setUsername(userName);
		} catch (UserNameException exception) {
			errorMessage+="<br> " + exception.getMessage();
		}
		
		employee.setSecurityQuestion(request.getParameter("SecurityQues"));
		
		employee.setSecurityAnswer(request.getParameter("SecurityAns"));
		
		String password=null;
		try {
			password=request.getParameter("Password");
			if(!password.equals(request.getParameter("ConfirmPwd")))
				throw new PasswordException("Passwords do not match!!!");
			employee.setPassword(password);
		} catch (PasswordException exception) {
			errorMessage+="<br> " + exception.getMessage();
		}
			
		
		try {
			if (errorMessage.isEmpty()) {
				employeeService.register(employee);
				response.getWriter().write("<script> alert(\"Employee registered succesfully\")</script>");
				RequestDispatcher rd=request.getRequestDispatcher("/loginpage.html");
				rd.include(request, response);
			}else
				throw new Exception("Employee registration failed!!!");
		}catch(Exception exception) {
			response.getWriter().write("<script> alert(\"" + errorMessage + "\")</script>");
			RequestDispatcher rd=request.getRequestDispatcher("/registrationPage.html");
			rd.include(request, response);
		}
	}
}
