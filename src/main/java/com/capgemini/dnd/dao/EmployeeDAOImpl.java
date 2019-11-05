package com.capgemini.dnd.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import com.capgemini.dnd.customexceptions.RowNotAddedException;
import com.capgemini.dnd.customexceptions.RowNotFoundException;
import com.capgemini.dnd.customexceptions.UnregisteredEmployeeException;
import com.capgemini.dnd.customexceptions.WrongPasswordException;
import com.capgemini.dnd.customexceptions.WrongSecurityAnswerException;
import com.capgemini.dnd.dto.Employee;
import com.capgemini.dnd.entity.EmployeeCredentialEntity;
import com.capgemini.dnd.util.CryptoFunction;
import com.capgemini.dnd.util.DBUtil;
import com.capgemini.dnd.util.HibernateUtil;
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

	public boolean employeeExists(Employee employee) throws BackEndException, RowNotFoundException {
		System.out.println("In login exists controller");
		boolean result = false;
		//Session session = null;
		Transaction transaction = null;
		try {
			System.out.println("before sesion");
			Session session = sessionFactory.openSession();
			System.out.println("after session");
			transaction = session.beginTransaction();
			@SuppressWarnings("rawtypes")
			Query query = session.createNamedQuery("GetOneConfidentialDetail");
			query.setParameter("username", employee.getUsername());
			if (query.getResultList().size() == 1) {
				result = true;
//				transaction.commit();
//				if (session.getTransaction() != null && session.getTransaction().isActive()) {
//					 session.getTransaction().rollback();
//				}
			}
			
			else {
				logger.error(Constants.EMPLOYEE_LOGGER_ERROR_NOT_A_REGISTRATED_USER);
				throw new RowNotFoundException(Constants.EMPLOYEE_LOGGER_ERROR_NOT_A_REGISTRATED_USER);
			}
			
			session.close();
		} catch (Exception exception) {
			logger.error(Constants.EMPLOYEE_LOGGER_ERROR_NOT_A_REGISTRATED_USER);
			exception.printStackTrace();
			throw new RowNotFoundException(Constants.EMPLOYEE_LOGGER_ERROR_NOT_A_REGISTRATED_USER);
		} finally {
//			HibernateUtil.closeSession(session);
//			session.close();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public boolean setLoggedIn(Employee employee) throws BackEndException, RowNotFoundException {
		boolean result = false;
		Session session = null;
		Transaction transaction = null;
		try {
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();
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

//			transaction.commit();
		} catch (Exception exception) {
			if (transaction != null)
				transaction.rollback();
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
		System.out.println("In login dao controller");
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
			logger.error(Constants.EMPLOYEE_LOGGER_ERROR_NOT_A_REGISTRATED_USER);
			throw new UnregisteredEmployeeException(Constants.EMPLOYEE_LOGGER_ERROR_NOT_A_REGISTRATED_USER);
		}
		return result;
	}

	// ------------------------------------------------------------------------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	public Employee fetchOneConfidentialDetail(Employee employee) throws BackEndException {
		Employee idealEmployee = null;
		Session session = null;
		Transaction transaction = null;
		try {
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();
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
//			transaction.commit();
		} catch (Exception exception) {
			if (transaction != null)
				transaction.rollback();
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
			EmployeeDAO empDAO = new EmployeeDAOImpl();
			Employee tempEmployee=employee;
			tempEmployee=empDAO.fetchOneConfidentialDetail(tempEmployee);
			EmployeeCredentialEntity empCredentialEntity = session.load(EmployeeCredentialEntity.class, tempEmployee.getEmpId());
			empCredentialEntity.setSalt(CryptoFunction.getNextSalt());
			empCredentialEntity.setHash(CryptoFunction.hash(employee.getPassword(), empCredentialEntity.getSalt()));
			session.update(empCredentialEntity);
			transaction.commit();
			if (session.getTransaction() != null && session.getTransaction().isActive()) {
				 session.getTransaction().rollback();
			}
			if (CryptoFunction.isExpectedPassword(employee.getPassword(), empCredentialEntity.getHash(), empCredentialEntity.getSalt())) {
				passwordChanged = true;
			} else {
				throw new BackEndException("Server error!!!"); 
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
						logger.error("Password could not be updated!!!");
						throw new PasswordException("Password could not be updated!!!");
					}
				} else {
					logger.error("Passwords do not match!!!");
					throw new PasswordException("Passwords do not match!!!");
				}
			} else {
				logger.error("New password matches with old password!!!");
				throw new PasswordException("New password matches with old password!!!");
			}
		} else {
			logger.error("Answer to security question is wrong!!!");
			throw new WrongSecurityAnswerException("Answer to security question is wrong!!!");
		}

		return result;
	}
}
