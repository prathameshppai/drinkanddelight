package com.capgemini.dnd.service;

import java.util.Scanner;

import com.capgemini.dnd.customexceptions.RowNotAddedException;
import com.capgemini.dnd.customexceptions.RowNotFoundException;
import com.capgemini.dnd.customexceptions.BackEndException;
import com.capgemini.dnd.customexceptions.InvalidPasswordException;
import com.capgemini.dnd.customexceptions.PasswordException;
import com.capgemini.dnd.customexceptions.UnregisteredEmployeeException;
import com.capgemini.dnd.customexceptions.WrongPasswordException;
import com.capgemini.dnd.customexceptions.WrongSecurityAnswerException;
import com.capgemini.dnd.dao.EmployeeDAO;
import com.capgemini.dnd.dao.EmployeeDAOImpl;
import com.capgemini.dnd.dto.Employee;
import com.capgemini.dnd.service.EmployeeService;


public class EmployeeServiceImpl implements EmployeeService {
	
	EmployeeDAO employeeDAO = new EmployeeDAOImpl();
	
	@Override
	public boolean register(Employee employee) throws RowNotAddedException, BackEndException {
		return employeeDAO.addEmployee(employee); 
	}
	
	@Override
	public boolean login(Employee employee) throws UnregisteredEmployeeException, WrongPasswordException, BackEndException {
		return employeeDAO.login(employee);
	}

	@Override
	public boolean changePassword(Employee employee, Scanner scanner) throws UnregisteredEmployeeException, WrongSecurityAnswerException, PasswordException, BackEndException{
		return employeeDAO.changePassword(employee, scanner);
	}
	
	@Override
	public int doesEmployeeExist(Employee employee) throws BackEndException, RowNotFoundException{
		return (employeeDAO.doesEmployeeExist(employee));
	}
	
	@Override
	public Employee fetchOneConfidentialDetail(Employee employee) throws BackEndException {
		return(employeeDAO.fetchOneConfidentialDetail(employee));
	}

	@Override
	public boolean changePassword(Employee idealEmployee, Employee actualEmployee) throws UnregisteredEmployeeException, WrongSecurityAnswerException, PasswordException, BackEndException, InvalidPasswordException, RowNotFoundException {
		return(employeeDAO.changePassword(idealEmployee, actualEmployee));
	}
	
}
