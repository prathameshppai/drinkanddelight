package com.capgemini.dnd.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.capgemini.dnd.customexceptions.BackEndException;
import com.capgemini.dnd.customexceptions.InvalidPasswordException;
import com.capgemini.dnd.customexceptions.PasswordException;
import com.capgemini.dnd.customexceptions.RowNotFoundException;
import com.capgemini.dnd.customexceptions.UnregisteredEmployeeException;
import com.capgemini.dnd.customexceptions.WrongPasswordException;
import com.capgemini.dnd.customexceptions.WrongSecurityAnswerException;
import com.capgemini.dnd.dto.Employee;
import com.capgemini.dnd.entity.EmployeeCredentialEntity;
import com.capgemini.dnd.util.CryptoFunction;

@Repository
public class EmployeeDAOImpl implements EmployeeDAO {
	private Logger logger = Logger.getRootLogger();

	@Autowired
	private SessionFactory sessionFactory;

	public EmployeeDAOImpl() {
//		System.out.println("PWD = " + new File(".").getAbsolutePath());
//		// PropertyConfigurator.configure("resources//log4j.properties");
//		// Log4JManager.initProps();
	}

	
	
	// -----------------------------------------------------------------------------------------------------------------------------------

	/*
	 * This method is used to check whether an employee with a given user name exists in the table of Employees.
	 * Return type - boolean 
	 */
	public boolean employeeExists(Employee employee) throws BackEndException, RowNotFoundException {
		boolean result = false;
		Session session = null;
		try {
			session = sessionFactory.openSession();
			session.beginTransaction();
			@SuppressWarnings("rawtypes")
			Query query = session.createNamedQuery("GetOneConfidentialDetail");
			query.setParameter("username", employee.getUsername());
			if (query.getResultList().size() == 1) {
				result = true;
			} else {
				logger.error(Constants.LOGGER_ERROR_MESSAGE_UNREGISTERED_USER);
				throw new RowNotFoundException(Constants.LOGGER_ERROR_MESSAGE_UNREGISTERED_USER);
			}
		} catch (Exception exception) {
			logger.error(Constants.LOGGER_ERROR_MESSAGE_UNREGISTERED_USER);
			throw new RowNotFoundException(Constants.LOGGER_ERROR_MESSAGE_UNREGISTERED_USER);
		} finally {
			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public boolean setLoggedIn(Employee employee) throws BackEndException, RowNotFoundException {
		boolean result = false;
		Session session = null;
		try {
			session = sessionFactory.openSession();
			session.beginTransaction();
			@SuppressWarnings("rawtypes")
			Query query = session.createNamedQuery("GetOneConfidentialDetail");
			query.setParameter("username", employee.getUsername());

			List<EmployeeCredentialEntity> empCredentialEntitylist = (List<EmployeeCredentialEntity>) query.list();

			if (CryptoFunction.isExpectedPassword(employee.getPassword(), empCredentialEntitylist.get(0))) {
				result = true;
			} else {
				logger.error(Constants.EMPLOYEE_LOGGER_NAME_PASSWORD_NOTFOUND);
				throw new RowNotFoundException(Constants.EMPLOYEE_LOGGER_NAME_PASSWORD_NOTFOUND);
			}

		} catch (Exception exception) {
			logger.error(Constants.EMPLOYEE_LOGGER_NAME_PASSWORD_NOTFOUND);
			throw new RowNotFoundException(Constants.EMPLOYEE_LOGGER_NAME_PASSWORD_NOTFOUND);
		} finally {
			session.close();
		}
		return result;
	}

	public boolean login(Employee employee)
			throws UnregisteredEmployeeException, WrongPasswordException, BackEndException {
		boolean result = false;
		try {
			if (employeeExists(employee)) {
				try {
					result = setLoggedIn(employee);
				} catch (RowNotFoundException exception) {
					logger.error(Constants.INCORRECT_PASSWORD_MESSAGE);
					throw new WrongPasswordException(Constants.INCORRECT_PASSWORD_MESSAGE);
				}
			}
		} catch (RowNotFoundException exception) {
			logger.error(Constants.LOGGER_ERROR_MESSAGE_UNREGISTERED_USER);
			throw new UnregisteredEmployeeException(Constants.LOGGER_ERROR_MESSAGE_UNREGISTERED_USER);
		}
		return result;
	}

	// ------------------------------------------------------------------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	public Employee fetchOneConfidentialDetail(Employee employee) throws BackEndException {
		Employee idealEmployee = null;
		Session session = null;
		
		try {
			session = sessionFactory.openSession();
			session.beginTransaction();
			@SuppressWarnings("rawtypes")
			Query query = session.createNamedQuery("GetOneConfidentialDetail");
			query.setParameter("username", employee.getUsername());
			List<EmployeeCredentialEntity> empCredentialEntitylist = (List<EmployeeCredentialEntity>) query.list();
			EmployeeCredentialEntity empCredentialEntity = empCredentialEntitylist.get(0);
			idealEmployee = new Employee();
			idealEmployee.setEmpId(empCredentialEntity.getEmpId());
			idealEmployee.setSecurityQuestion(empCredentialEntity.getSecurityQuestion());
			idealEmployee.setSecurityAnswer(empCredentialEntity.getSecurityAnswer());
			idealEmployee.setHash(empCredentialEntity.getHash());
			idealEmployee.setSalt(empCredentialEntity.getSalt());
		} catch (Exception exception) {
			logger.error(exception.getMessage());
			throw new BackEndException(exception.getMessage());
		} finally {
			session.close();
		}
		return idealEmployee;
	}

	public boolean setPassword(Employee employee) throws BackEndException, RowNotFoundException {
		boolean passwordChanged = false;
		Session session = null;
		Transaction transaction = null;
		try {
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();
			Employee tempEmployee = employee;
			tempEmployee = fetchOneConfidentialDetail(tempEmployee);
			EmployeeCredentialEntity empCredentialEntity = session.load(EmployeeCredentialEntity.class,
					tempEmployee.getEmpId());
			empCredentialEntity.setSalt(CryptoFunction.getNextSalt());
			empCredentialEntity.setHash(CryptoFunction.hash(employee.getPassword(), empCredentialEntity.getSalt()));
			session.update(empCredentialEntity);
			transaction.commit();
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			if (CryptoFunction.isExpectedPassword(employee.getPassword(), empCredentialEntity.getHash(),
					empCredentialEntity.getSalt())) {
				passwordChanged = true;
			} else {
				throw new BackEndException(Constants.SERVER_ERROR_MESSAGE);
			}
		} catch (Exception exception) {
			if (transaction != null)
				transaction.rollback();
			logger.error(exception.getMessage());
			throw new BackEndException(exception.getMessage());
		} finally {
			session.close();
		}
		return passwordChanged;
	}

	@Override
	public boolean changePassword(Employee idealEmployee, Employee actualEmployee)
			throws UnregisteredEmployeeException, WrongSecurityAnswerException, PasswordException, BackEndException,
			InvalidPasswordException, RowNotFoundException {
		boolean result = false;

		if (idealEmployee.getSecurityAnswer().equals(actualEmployee.getSecurityAnswer())) {
			if (!CryptoFunction.isExpectedPassword(actualEmployee.getPassword(), idealEmployee.getHash(),
					idealEmployee.getSalt())) {
				if (actualEmployee.getPassword().equals(actualEmployee.getConfirmPassword())) {
					try {
						result = setPassword(actualEmployee);
					} catch (BackEndException exception) {
						logger.error(Constants.LOGGER_ERROR_MESSAGE_BACKEND_ISSUE);
						throw new PasswordException(Constants.LOGGER_ERROR_MESSAGE_BACKEND_ISSUE);
					}
				} else {
					logger.error(Constants.LOGGER_ERROR_MESSAGE_PASSWORD_MISMATCH);
					throw new PasswordException(Constants.LOGGER_ERROR_MESSAGE_PASSWORD_MISMATCH);
				}
			} else {
				logger.error(Constants.LOGGER_ERROR_MESSAGE_PASSWORD_UNCHANGED);
				throw new PasswordException(Constants.LOGGER_ERROR_MESSAGE_PASSWORD_UNCHANGED);
			}
		} else {
			logger.error(Constants.LOGGER_ERROR_MESSAGE_WRONG_ANSWER);
			throw new WrongSecurityAnswerException(Constants.LOGGER_ERROR_MESSAGE_WRONG_ANSWER);
		}

		return result;
	}
}
