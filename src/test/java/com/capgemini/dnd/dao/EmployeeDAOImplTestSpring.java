package com.capgemini.dnd.dao;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.capgemini.dnd.customexceptions.BackEndException;
import com.capgemini.dnd.customexceptions.PasswordException;
import com.capgemini.dnd.customexceptions.ProductOrderIDDoesNotExistException;
import com.capgemini.dnd.customexceptions.RowNotFoundException;
import com.capgemini.dnd.customexceptions.UnregisteredEmployeeException;
import com.capgemini.dnd.customexceptions.WrongPasswordException;
import com.capgemini.dnd.dto.Employee;
import com.capgemini.dnd.dto.ProductStock;

@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/dispatcher-servlet.xml",
		"file:src/main/webapp/WEB-INF/applicationContext.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class EmployeeDAOImplTestSpring {

	@Autowired
	private EmployeeDAO employeeDAO;

	@Test
	@Transactional
	@Rollback(true)
	public void testemployeeExists1() throws BackEndException, RowNotFoundException {
		Employee employee = new Employee();
		employee.setUsername("nitu_40");

		assertTrue(employeeDAO.employeeExists(employee));
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testemployeeExists2() {

		Employee employee = new Employee();
		employee.setUsername("kaushik_nafeez");

		assertThrows(RowNotFoundException.class, () -> {
			employeeDAO.employeeExists(employee);
		});

	}

	@Test
	@Transactional
	@Rollback(true)
	public void testlogin1() throws UnregisteredEmployeeException, WrongPasswordException, BackEndException {
		Employee employee = new Employee();
		employee.setUsername("nitu_40");
		employee.setPassword("abcd");

		assertTrue(employeeDAO.login(employee));
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testlogin2() {

		Employee employee = new Employee();
		employee.setUsername("nitu_40");
		employee.setPassword("bye");
		assertThrows(WrongPasswordException.class, () -> {
			employeeDAO.login(employee);
		});
	}
	@Test
	@Transactional
	@Rollback(true)
	public void testlogin3() {

		Employee employee = new Employee();
		employee.setUsername("kavfuxvx_40");
		employee.setPassword("bye");
		assertThrows(UnregisteredEmployeeException.class, () -> {
			employeeDAO.login(employee);
		});
	}

	@Test
	@Transactional
	@Rollback(true)
	public void testchangePassword() throws BackEndException {

		EmployeeDAOImpl es = new EmployeeDAOImpl();
		
		Employee employee = new Employee();
		employee.setUsername("ankit_40");
		employee.setPassword("pass");
		
		Employee idealEmployee = new Employee();
		idealEmployee.setUsername("ankit_40");
		
		Employee testEmployee = es.fetchOneConfidentialDetail(idealEmployee);
		System.out.println(testEmployee);
		Throwable exception = assertThrows(NullPointerException.class, () -> {
			es.changePassword(testEmployee,employee);
		});
		assertEquals("New password matches with old password!!!", exception.getMessage());

	}
}