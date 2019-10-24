package com.capgemini.dnd.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
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
	private EmployeeDAO employeeDAO;
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
		employeeDAO = new EmployeeDAOImpl();
		return employeeDAO.addEmployee(employee);
	}

	// -----------------------------------------------------------------------------------------------------------------------------------

	public int doesEmployeeExist(Employee employee) throws BackEndException, RowNotFoundException {
		int employeeCount = 0;
		Connection connection;
		try {
			connection = DBUtil.getInstance().getConnection();
		} catch (Exception exception) {
			logger.error(Constants.EMPLOYEE_LOGGER_ERROR_DATABASE_NOTCONNECTED + exception.getMessage());
			throw new BackEndException(Constants.EMPLOYEE_LOGGER_ERROR_DATABASE_NOTCONNECTED + exception.getMessage());
		}

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			preparedStatement = connection.prepareStatement(QueryMapper.SELECT_ONE_EMPLOYEE_LOGIN_CREDENTIAL);
			preparedStatement.setString(1, employee.getUsername());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				employeeCount++;
			}

			if (employeeCount == 0) {
				logger.error(Constants.EMPLOYEE_LOGGER_ERROR_RECORD_NOT_FOUND);
				throw new RowNotFoundException(Constants.LOGGER_ERROR_MESSAGE_UNREGISTERED_USER);
			}
			return employeeCount;
		} catch (SQLException exception) {
			logger.error(Constants.EMPLOYEE_LOGGER_ERROR_FETCHING_FAILED + exception.getMessage());
			throw new BackEndException(Constants.EMPLOYEE_LOGGER_ERROR_FETCHING_FAILED + exception.getMessage());
		} finally {
			try {
				connection.close();
			} catch (SQLException exception) {
				logger.error(exception.getMessage());
				throw new BackEndException(exception.getMessage());
			}
		}
	}

	public boolean setLoggedIn(Employee employee) throws BackEndException, RowNotFoundException {
		Connection connection;
		try {
			connection = DBUtil.getInstance().getConnection();
		} catch (Exception exception) {
			logger.error(Constants.EMPLOYEE_LOGGER_ERROR_DATABASE_NOTCONNECTED + exception.getMessage());
			throw new BackEndException(Constants.EMPLOYEE_LOGGER_ERROR_DATABASE_NOTCONNECTED + exception.getMessage());
		}
		PreparedStatement preparedStatement1 = null;
		ResultSet resultSet = null;
		boolean result = false;
		try {
			preparedStatement1 = connection.prepareStatement(QueryMapper.FETCH_CREDENTIALS);
			preparedStatement1.setString(1, employee.getUsername());
			resultSet = preparedStatement1.executeQuery();

			String hash = null;
			String salt = null;
			while (resultSet.next()) {
				hash = resultSet.getString("Hash");
				salt = resultSet.getString("Salt");
			}

			if (CryptoFunction.isExpectedPassword(employee.getPassword(), salt, hash)) {
				result = true;
			} else {
				logger.error(Constants.EMPLOYEE_LOGGER_NAME_PASSWORD_NOTFOUND);
				throw new RowNotFoundException(Constants.EMPLOYEE_LOGGER_NAME_PASSWORD_NOTFOUND);
			}
		} catch (SQLException exception) {
			logger.error(Constants.EMPLOYEE_LOGGER_ERROR_FETCHING_FAILED + exception.getMessage());
			throw new BackEndException(Constants.EMPLOYEE_LOGGER_ERROR_FETCHING_FAILED + exception.getMessage());
		} finally {
			try {
				preparedStatement1.close();
				connection.close();
			} catch (SQLException exception) {
				logger.error(exception.getMessage());
				throw new BackEndException(exception.getMessage());
			}
		}
		return result;
	}

	/*
	 * public List <SupplierEntity> getSuppliers() { try (Session session =
	 * HibernateUtil.getSessionFactory().openSession()) { return
	 * session.createQuery("from SupplierEntity", SupplierEntity.class).list(); } }
	 */

	/*
	 * public boolean setLoggedIn(Employee employee) throws BackEndException,
	 * RowNotFoundException { boolean result = false; Session session = null;
	 * Transaction transaction = null; try {
	 * System.out.println("hey inside daotry"); session =
	 * HibernateUtil.getSessionFactory().openSession();
	 * System.out.println("hi are you outside?"); transaction =
	 * session.beginTransaction();
	 * 
	 * @SuppressWarnings("rawtypes") Query query = session.
	 * createQuery("SELECT Salt, Hash FROM EmployeeCredentials WHERE Username:=username"
	 * ); query.setParameter("username", employee.getUsername()); List<String> list
	 * = query.list(); System.out.println("hi are you inside?"); if
	 * (CryptoFunction.isExpectedPassword(employee.getPassword(), list.get(0),
	 * list.get(1))) { result = true; System.out.println(result); } else {
	 * logger.error(Constants.EMPLOYEE_LOGGER_NAME_PASSWORD_NOTFOUND); throw new
	 * RowNotFoundException(Constants.EMPLOYEE_LOGGER_NAME_PASSWORD_NOTFOUND); }
	 * transaction.commit(); } catch (HibernateException exception) { if
	 * (transaction != null) transaction.rollback();
	 * logger.error(Constants.EMPLOYEE_LOGGER_ERROR_FETCHING_FAILED +
	 * exception.getMessage()); throw new
	 * BackEndException(Constants.EMPLOYEE_LOGGER_ERROR_FETCHING_FAILED +
	 * exception.getMessage()); } finally { try { session.close(); } catch
	 * (Exception exception) { logger.error(exception.getMessage()); throw new
	 * BackEndException(exception.getMessage()); } } return result; }
	 */

	public boolean login(Employee employee)
			throws UnregisteredEmployeeException, WrongPasswordException, BackEndException {
		employeeDAO = new EmployeeDAOImpl();
		boolean result = false;
		try {
			if (employeeDAO.doesEmployeeExist(employee) == 1) {
				try {
					result = employeeDAO.setLoggedIn(employee);
				} catch (RowNotFoundException e) {
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

	/*
	 * public Employee fetchOneConfidentialDetail(Employee employee) throws
	 * BackEndException { Connection connection; try { connection =
	 * DBUtil.getInstance().getConnection(); } catch (Exception exception) {
	 * logger.error(Constants.EMPLOYEE_LOGGER_ERROR_DATABASE_NOTCONNECTED +
	 * exception.getMessage()); throw new
	 * BackEndException(Constants.EMPLOYEE_LOGGER_ERROR_DATABASE_NOTCONNECTED +
	 * exception.getMessage()); } ResultSet resultSet = null; PreparedStatement
	 * preparedStatement = null; try { preparedStatement =
	 * connection.prepareStatement(QueryMapper.SELECT_ONE_EMPLOYEE_LOGIN_CREDENTIAL)
	 * ; preparedStatement.setString(1, employee.getUsername()); resultSet =
	 * preparedStatement.executeQuery(); while (resultSet.next()) {
	 * employee.setEmpId(resultSet.getString(1));
	 * employee.setSecurityQuestion(resultSet.getString(3));
	 * employee.setSecurityAnswer(resultSet.getString(4));
	 * employee.setLoggedIn(resultSet.getBoolean(5));
	 * employee.setHash(resultSet.getString(6));
	 * employee.setSalt(resultSet.getString(7)); }
	 * 
	 * } catch (SQLException exception) {
	 * logger.error(Constants.EMPLOYEE_LOGGER_ERROR_FETCHING_FAILED +
	 * exception.getMessage()); throw new
	 * BackEndException(Constants.EMPLOYEE_LOGGER_ERROR_FETCHING_FAILED +
	 * exception.getMessage()); } finally { try { connection.close(); } catch
	 * (SQLException exception) { logger.error(exception.getMessage()); throw new
	 * BackEndException(exception.getMessage()); } } return employee; }
	 */

	public Employee fetchOneConfidentialDetail(Employee employee) throws BackEndException {

		EmployeeCredentialEntity employeeCredentialEntity = new EmployeeCredentialEntity();
		Connection connection;
		try {
			connection = DBUtil.getInstance().getConnection();
		} catch (Exception exception) {
			logger.error(Constants.EMPLOYEE_LOGGER_ERROR_DATABASE_NOTCONNECTED + exception.getMessage());
			throw new BackEndException(Constants.EMPLOYEE_LOGGER_ERROR_DATABASE_NOTCONNECTED + exception.getMessage());
		}
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(QueryMapper.SELECT_ONE_EMPLOYEE_LOGIN_CREDENTIAL);
			preparedStatement.setString(1, employee.getUsername());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				employeeCredentialEntity.setEmpId(resultSet.getString(1));
				employeeCredentialEntity.setUserName(resultSet.getString(2));
				employeeCredentialEntity.setSecurityQuestion(resultSet.getString(3));
				employeeCredentialEntity.setSecurityAnswer(resultSet.getString(4));
				employeeCredentialEntity.setActiveStatus(resultSet.getBoolean(5));
				employeeCredentialEntity.setHash(resultSet.getString(6));
				employeeCredentialEntity.setSalt(resultSet.getString(7));
			}
			employee.setEmpId(employeeCredentialEntity.getEmpId());
			employee.setUsername(employeeCredentialEntity.getUserName());
			employee.setSecurityQuestion(employeeCredentialEntity.getSecurityQuestion());
			employee.setSecurityAnswer(employeeCredentialEntity.getSecurityAnswer());

		} catch (SQLException exception) {
			logger.error(Constants.EMPLOYEE_LOGGER_ERROR_FETCHING_FAILED + exception.getMessage());
			throw new BackEndException(Constants.EMPLOYEE_LOGGER_ERROR_FETCHING_FAILED + exception.getMessage());
		} finally {
			try {
				connection.close();
			} catch (SQLException exception) {
				logger.error(exception.getMessage());
				throw new BackEndException(exception.getMessage());
			}
		}
		return employee;
	}

	public boolean setPassword(Employee employee) throws BackEndException, RowNotFoundException {
		Connection connection;
		try {
			connection = DBUtil.getInstance().getConnection();
		} catch (Exception exception) {
			logger.error(Constants.EMPLOYEE_LOGGER_ERROR_DATABASE_NOTCONNECTED + exception.getMessage());
			throw new BackEndException(Constants.EMPLOYEE_LOGGER_ERROR_DATABASE_NOTCONNECTED + exception.getMessage());
		}

		PreparedStatement preparedStatement = null;
		boolean result = false;
		int rowsUpdated = 0;
		try {
			preparedStatement = connection.prepareStatement(QueryMapper.UPDATE_PASSWORD);
			String salt = CryptoFunction.getNextSalt();
			String hash = CryptoFunction.hash(employee.getPassword(), salt);

			employee.setHash(hash);
			employee.setSalt(salt);

			preparedStatement.setString(1, hash);
			preparedStatement.setString(2, salt);
			preparedStatement.setString(3, employee.getUsername());
			rowsUpdated = preparedStatement.executeUpdate();
			if (rowsUpdated == 0) {
				logger.error(Constants.EMPLOYEE_LOGGER_ERROR_USER_NOTFOUND);
				throw new RowNotFoundException(Constants.EMPLOYEE_LOGGER_ERROR_USER_NOTFOUND);
			}
			result = true;
		} catch (SQLException exception) {
			logger.error(Constants.EMPLOYEE_LOGGER_ERROR_FETCHING_FAILED + exception.getMessage());
			throw new BackEndException(Constants.EMPLOYEE_LOGGER_ERROR_FETCHING_FAILED + exception.getMessage());
		} finally {
			try {
				connection.close();
			} catch (SQLException exception) {
				logger.error(exception.getMessage());
				throw new BackEndException(exception.getMessage());
			}
		}

		return result;
	}

	public boolean changePassword(Employee employee, Scanner scanner)
			throws UnregisteredEmployeeException, WrongSecurityAnswerException, PasswordException, BackEndException {
		employeeDAO = new EmployeeDAOImpl();
		boolean result = false;
		String answer = "";
		String newPassword = "";
		String confirmPassword = "";
		try {
			if (employeeDAO.doesEmployeeExist(employee) > 0) {
				employee = employeeDAO.fetchOneConfidentialDetail(employee);
				System.out.println(employee.getSecurityQuestion());
				answer = scanner.nextLine();
				if (answer.equals(employee.getSecurityAnswer())) {
					System.out.println("Type the new password:");
					newPassword = scanner.nextLine();
					newPassword = newPassword.trim();
					if (!newPassword.equals("")) {
						if (!CryptoFunction.isExpectedPassword(newPassword, employee.getSalt(), employee.getHash())) {
							System.out.println("Re-type to confirm the new password:");
							confirmPassword = scanner.nextLine();
							if (newPassword.equals(confirmPassword)) {
								employee.setPassword(newPassword);
								result = employeeDAO.setPassword(employee);
							} else {
								logger.error("Passwords do not match!!!");
								throw new PasswordException("Passwords do not match!!!");
							}
						} else {
							logger.error("New password matches with old password!!!");
							throw new PasswordException("New password matches with old password!!!");
						}
					} else {
						logger.error("Password can't be empty or just spaces!!!");
						throw new PasswordException("Password can't be empty or just spaces!!!");
					}
				} else {
					logger.error("Answer to security question is wrong!!!");
					throw new WrongSecurityAnswerException("Answer to security question is wrong!!!");
				}
			}
		} catch (RowNotFoundException exception) {
			logger.error("You are not a registered user!!!");
			throw new UnregisteredEmployeeException("You are not a registered user!!!");
		}
		return result;
	}

	@Override
	public boolean changePassword(Employee idealEmployee, Employee actualEmployee)
			throws UnregisteredEmployeeException, WrongSecurityAnswerException, PasswordException, BackEndException,
			InvalidPasswordException, RowNotFoundException {
		employeeDAO = new EmployeeDAOImpl();
		boolean result = false;

		if (idealEmployee.getSecurityAnswer().equals(actualEmployee.getSecurityAnswer())) {
			if (!CryptoFunction.isExpectedPassword(actualEmployee.getPassword(), idealEmployee.getSalt(),
					idealEmployee.getHash())) {

				if (actualEmployee.getPassword().equals(actualEmployee.getConfirmPassword())) {
					result = employeeDAO.setPassword(actualEmployee);
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
