package com.capgemini.dnd.dao;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.capgemini.dnd.customexceptions.BackEndException;
import com.capgemini.dnd.customexceptions.RowNotFoundException;
import com.capgemini.dnd.customexceptions.UnregisteredEmployeeException;
import com.capgemini.dnd.customexceptions.WrongPasswordException;
import com.capgemini.dnd.dto.Employee;


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

		assertThrows(BackEndException.class, () -> {
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
		assertThrows(BackEndException.class, () -> {
			employeeDAO.login(employee);
		});
	}

	
}