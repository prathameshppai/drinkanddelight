package com.capgemini.dnd.dao;

import com.capgemini.dnd.customexceptions.RowNotAddedException;

import java.util.Scanner;

import com.capgemini.dnd.customexceptions.BackEndException;
import com.capgemini.dnd.customexceptions.InvalidPasswordException;
import com.capgemini.dnd.customexceptions.PasswordException;
import com.capgemini.dnd.customexceptions.RowNotFoundException;
import com.capgemini.dnd.customexceptions.UnregisteredEmployeeException;
import com.capgemini.dnd.customexceptions.WrongPasswordException;
import com.capgemini.dnd.customexceptions.WrongSecurityAnswerException;
import com.capgemini.dnd.dto.Employee;

public interface EmployeeDAO {
	public boolean addEmployee(Employee employee) throws BackEndException, RowNotAddedException;

	public boolean register(Employee employee) throws BackEndException, RowNotAddedException;

	public int doesEmployeeExist(Employee employee) throws BackEndException, RowNotFoundException;

	public boolean setLoggedIn(Employee employee) throws BackEndException, RowNotFoundException;

	public boolean login(Employee employee)
			throws BackEndException, UnregisteredEmployeeException, WrongPasswordException;

	public Employee fetchOneConfidentialDetail(Employee employee) throws BackEndException;

	public boolean setPassword(Employee employee) throws BackEndException, RowNotFoundException;

	public boolean changePassword(Employee employee, Scanner scanner)
			throws BackEndException, UnregisteredEmployeeException, WrongSecurityAnswerException, PasswordException;
	
	boolean changePassword(Employee idealEmployee, Employee actualEmployee) throws UnregisteredEmployeeException,
			WrongSecurityAnswerException, PasswordException, BackEndException, InvalidPasswordException, RowNotFoundException;
}
