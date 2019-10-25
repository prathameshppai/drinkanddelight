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
import org.hibernate.Transaction;
import org.hibernate.query.Query;

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

public class EmployeeDAOImpl implements EmployeeDAO {
	private Logger logger = Logger.getRootLogger();

	public EmployeeDAOImpl() {
		System.out.println("PWD = " + new File(".").getAbsolutePath());
		// PropertyConfigurator.configure("resources//log4j.properties");
		// Log4JManager.initProps();
	}

	public boolean addEmployee(Employee employee) throws BackEndException, RowNotAddedException {
		Connection connection;
		try {
			connection = DBUtil.getInstance().getConnection();
		} catch (Exception exception) {
			logger.error(Constants.EMPLOYEE_LOGGER_ERROR_DATABASE_NOTCONNECTED + exception.getMessage());
			throw new BackEndException(Constants.EMPLOYEE_LOGGER_ERROR_DATABASE_NOTCONNECTED + exception.getMessage());
		}
		boolean employeeRegistered = false;
		ResultSet resultSet = null;
		PreparedStatement preparedStatement1 = null;
		PreparedStatement preparedStatement2 = null;
		Statement statement = null;
		int employeeCount = 0;
		try {
			// System.out.println(employee);
			String salt = CryptoFunction.getNextSalt();
			String hash = CryptoFunction.hash(employee.getPassword(), salt);
			employee.setSalt(salt);
			employee.setHash(hash);

			preparedStatement1 = connection.prepareStatement(QueryMapper.INSERT_ONE_EMPLOYEE);
			preparedStatement1.setString(2, employee.getEmpName());
			preparedStatement1.setDate(3, employee.getDob());
			preparedStatement1.setString(4, employee.getEmailId());
			preparedStatement1.setString(5, "" + Character.toUpperCase(employee.getGender().charAt(0)));
			preparedStatement1.setString(6, employee.getDesignation());
			preparedStatement1.setString(7, employee.getPhoneNo());

			preparedStatement2 = connection.prepareStatement(QueryMapper.INSERT_ONE_EMPLOYEE_LOGIN_CREDENTIAL);
			preparedStatement2.setString(2, employee.getUsername());
			preparedStatement2.setString(3, employee.getSecurityQuestion());
			preparedStatement2.setString(4, employee.getSecurityAnswer());
			preparedStatement2.setBoolean(5, employee.isLoggedIn());
			preparedStatement2.setString(6, employee.getHash());
			preparedStatement2.setString(7, employee.getSalt());

			statement = connection.createStatement();
			resultSet = statement.executeQuery(QueryMapper.SELECT_ALL_EMPLOYEES);
			while (resultSet.next()) {
				employeeCount++;
			}
			employeeCount++;
			preparedStatement1.setString(1, "E" + Integer.toString(employeeCount));
			preparedStatement2.setString(1, "E" + Integer.toString(employeeCount));

			int status2 = preparedStatement2.executeUpdate();
			int status1 = preparedStatement1.executeUpdate();
			if (status1 == 1 && status2 == 1)
				employeeRegistered = true;
			else {
				logger.error(Constants.EMPLOYEE_LOGGER_ERROR_REGISTRATION_FAILED);
				throw new RowNotAddedException(Constants.EMPLOYEE_LOGGER_ERROR_REGISTRATION_FAILED);
			}
			return employeeRegistered;
		} catch (SQLException exception) {
			logger.error(Constants.EMPLOYEE_LOGGER_ERROR_ROW_ADDTION_FAILED + exception.getMessage());
			throw new RowNotAddedException(Constants.EMPLOYEE_LOGGER_ERROR_ROW_ADDTION_FAILED + exception.getMessage());
		} finally {
			try {
				connection.close();
			} catch (SQLException exception) {
				logger.error(exception.getMessage());
				throw new BackEndException(exception.getMessage());
			}
		}
	}

	public boolean register(Employee employee) throws BackEndException, RowNotAddedException {
		return addEmployee(employee);
	}

	// -----------------------------------------------------------------------------------------------------------------------------------

	public boolean employeeExists(Employee employee) throws BackEndException, RowNotFoundException {
		boolean result = false;
		Session session = null;
		Transaction transaction = null;
		try {
			session = HibernateUtil.getASession();
			transaction = session.beginTransaction();
			@SuppressWarnings("rawtypes")
			Query query = session.createNamedQuery("GetOneConfidentialDetail");
			query.setParameter("username", employee.getUsername());
			if (query.getResultList().size() == 1) {
				result = true;
				transaction.commit();
			} else {
				logger.error(Constants.EMPLOYEE_LOGGER_ERROR_NOT_A_REGISTRATED_USER);
				throw new RowNotFoundException(Constants.EMPLOYEE_LOGGER_ERROR_NOT_A_REGISTRATED_USER);
			}
		} catch (Exception exception) {
			logger.error(Constants.EMPLOYEE_LOGGER_ERROR_NOT_A_REGISTRATED_USER);
			throw new RowNotFoundException(Constants.EMPLOYEE_LOGGER_ERROR_NOT_A_REGISTRATED_USER);
		} finally {
			HibernateUtil.closeSession(session);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public boolean setLoggedIn(Employee employee) throws BackEndException, RowNotFoundException {
		boolean result = false;
		Session session = null;
		Transaction transaction = null;
		try {
			session = HibernateUtil.getASession();
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

			transaction.commit();
		} catch (Exception exception) {
			if (transaction != null)
				transaction.rollback();
			logger.error(Constants.EMPLOYEE_LOGGER_NAME_PASSWORD_NOTFOUND);
			throw new RowNotFoundException(Constants.EMPLOYEE_LOGGER_NAME_PASSWORD_NOTFOUND);
		} finally {
			HibernateUtil.closeSession(session);
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
			session = HibernateUtil.getASession();
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
			transaction.commit();
		} catch (Exception exception) {
			if (transaction != null)
				transaction.rollback();
			logger.error(exception.getMessage());
			throw new BackEndException(exception.getMessage());
		} finally {
			HibernateUtil.closeSession(session);
		}
		return idealEmployee;
	}

	public boolean setPassword(Employee employee) throws BackEndException, RowNotFoundException {
		boolean passwordChanged = false;
		Session session = null;
		Transaction transaction = null;
		try {
			session = HibernateUtil.getASession();
			transaction = session.beginTransaction();
			EmployeeDAO empDAO = new EmployeeDAOImpl();
			Employee tempEmployee=employee;
			tempEmployee=empDAO.fetchOneConfidentialDetail(tempEmployee);
			EmployeeCredentialEntity empCredentialEntity = session.load(EmployeeCredentialEntity.class, tempEmployee.getEmpId());
			empCredentialEntity.setSalt(CryptoFunction.getNextSalt());
			empCredentialEntity.setHash(CryptoFunction.hash(employee.getPassword(), empCredentialEntity.getSalt()));
			session.update(empCredentialEntity);
			transaction.commit();
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
			HibernateUtil.closeSession(session);
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
