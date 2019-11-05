package com.capgemini.dnd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capgemini.dnd.customexceptions.BackEndException;
import com.capgemini.dnd.customexceptions.InvalidPasswordException;
import com.capgemini.dnd.customexceptions.PasswordException;
import com.capgemini.dnd.customexceptions.RowNotAddedException;
import com.capgemini.dnd.customexceptions.RowNotFoundException;
import com.capgemini.dnd.customexceptions.UnregisteredEmployeeException;
import com.capgemini.dnd.customexceptions.WrongPasswordException;
import com.capgemini.dnd.customexceptions.WrongSecurityAnswerException;
import com.capgemini.dnd.dao.EmployeeDAO;
import com.capgemini.dnd.dao.EmployeeDAOImpl;
import com.capgemini.dnd.dto.Employee;

@Service
public class EmployeeServiceImpl implements EmployeeService {

	@Autowired
	EmployeeDAO employeeDAO;

	

	@Override
	public boolean login(Employee employee)
			throws UnregisteredEmployeeException, WrongPasswordException, BackEndException {
		System.out.println("In login service controller");
		return employeeDAO.login(employee);
	}

	@Override
	public boolean employeeExists(Employee employee) throws BackEndException, RowNotFoundException {
		return (employeeDAO.employeeExists(employee));
	}

	@Override
	public Employee fetchOneConfidentialDetail(Employee employee) throws BackEndException {
		return (employeeDAO.fetchOneConfidentialDetail(employee));
	}

	@Override
	public boolean changePassword(Employee idealEmployee, Employee actualEmployee)
			throws UnregisteredEmployeeException, WrongSecurityAnswerException, PasswordException, BackEndException,
			InvalidPasswordException, RowNotFoundException {
		return (employeeDAO.changePassword(idealEmployee, actualEmployee));
	}
}
