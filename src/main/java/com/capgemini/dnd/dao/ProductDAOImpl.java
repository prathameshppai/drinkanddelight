package com.capgemini.dnd.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.capgemini.dnd.customexceptions.BackEndException;
import com.capgemini.dnd.customexceptions.ConnectionException;
import com.capgemini.dnd.customexceptions.DisplayException;
import com.capgemini.dnd.customexceptions.DistributorIDDoesNotExistException;
import com.capgemini.dnd.customexceptions.DoesNotExistException;
import com.capgemini.dnd.customexceptions.ExitDateException;
import com.capgemini.dnd.customexceptions.ProductIDDoesNotExistException;
import com.capgemini.dnd.customexceptions.ProductNameDoesNotExistException;
import com.capgemini.dnd.customexceptions.ProductOrderIDDoesNotExistException;
import com.capgemini.dnd.customexceptions.ProductOrderNotAddedException;
import com.capgemini.dnd.customexceptions.UpdateException;
import com.capgemini.dnd.customexceptions.WIdDoesNotExistException;
import com.capgemini.dnd.dto.Address;
import com.capgemini.dnd.dto.DisplayProductOrder;
import com.capgemini.dnd.dto.Distributor;
import com.capgemini.dnd.dto.ProductOrder;
import com.capgemini.dnd.dto.ProductStock;
import com.capgemini.dnd.entity.ProductStockEntity;
import com.capgemini.dnd.util.DBUtil;
import com.capgemini.dnd.util.HibernateUtil;
import com.capgemini.dnd.entity.ProductOrdersEntity;
import com.capgemini.dnd.dao.QueryMapper;

import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
//import org.hibernate.service.ServiceRegistryBuilder;

public class ProductDAOImpl implements ProductDAO {

	// private static final Distributor supplier = null;
	Logger logger = Logger.getRootLogger();

	/*
	 * Product order delivery status update
	 * 
	 */
	public String updateStatusProductOrder(String oid, String newStatus) throws Exception {

		Connection con = DBUtil.getInstance().getConnection();
		PreparedStatement preparedStatement = null;
		java.util.Date today_date = new Date();
		int queryResult = 0;
		if (newStatus.equalsIgnoreCase("RECEIVED")) {
			try {
				preparedStatement = con.prepareStatement(QueryMapper.UPDATE_DELIVERY_STATUS);

				preparedStatement.setString(1, newStatus);
				preparedStatement.setDate(2, DBUtil.stringtoDate(today_date));
				preparedStatement.setInt(3, Integer.parseInt(oid));
				queryResult = preparedStatement.executeUpdate();
				if (queryResult == 0) {
					logger.error(Constants.LOGGER_ERROR_MESSAGE_FAILED_UPDATION);
					throw new UpdateException(Constants.UPDATE_EXCEPTION_MESSAGE_FAILURE_DELIVERY);

				} else {
					logger.info(Constants.LOGGER_INFO_DISPLAY_SUCCESSFUL);
					return Constants.UPADTED_SUCCESSFULLY_MESSAGE;
				}

			} catch (SQLException sqlException) {
				logger.error(sqlException.getMessage());

				throw new UpdateException(Constants.UPDATE_EXCEPTION_MESSAGE_TECHNICAL_PROBLEM);
			} finally {
				try {

					preparedStatement.close();
					con.close();
				} catch (SQLException sqlException) {
					logger.error(sqlException.getMessage());
					throw new UpdateException(Constants.UPDATE_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);

				}
			}
		} else {
			try {
				preparedStatement = con.prepareStatement(QueryMapper.UPDATE_DELIVERY_STATUS1);
				preparedStatement.setString(1, newStatus);
				preparedStatement.setInt(2, Integer.parseInt(oid));

				queryResult = preparedStatement.executeUpdate();
				if (queryResult == 0) {
					logger.error(Constants.LOGGER_ERROR_MESSAGE_FAILED_UPDATION);
					throw new UpdateException(Constants.UPDATE_EXCEPTION_MESSAGE_FAILURE_DELIVERY);

				} else {
					logger.info(Constants.LOGGER_INFO_DISPLAY_SUCCESSFUL);
					return Constants.UPADTED_SUCCESSFULLY_MESSAGE;
				}
			} catch (SQLException sqlException) {
				logger.error(sqlException.getMessage());
				throw new UpdateException(Constants.UPDATE_EXCEPTION_MESSAGE_TECHNICAL_PROBLEM);
			} finally {
				try {

					preparedStatement.close();
					con.close();
				} catch (SQLException sqlException) {
					logger.error(sqlException.getMessage());
					throw new UpdateException(Constants.UPDATE_EXCEPTION_MESSAGE_TECHNICAL_PROBLEM);

				}
			}
		}

	}

	/*****************************************************************
	 * - Method Name: displayProductOrderDetails() - Input Parameters : - Throws :
	 * Exception - Creation Date : 25/09/2019 - Description : Returns list of all
	 * products
	 * 
	 * @throws Exception
	 *******************************************************************/

	public List<ProductOrder> displayProductOrderDetails() throws Exception {
		List<ProductOrder> poList1 = new ArrayList<ProductOrder>();
		Connection connection = DBUtil.getInstance().getConnection();
		PreparedStatement pst = null;

		try {
			pst = connection.prepareStatement(QueryMapper.DISPLAY_PRODUCT_ORDER);
			ResultSet rs = pst.executeQuery();

			int isFetched = 0;
			while (rs.next()) {

				isFetched = 1;
				int index = 1;

				String orderId = Integer.valueOf(rs.getInt(index++)).toString();
				String name = rs.getString(index++);
				String productId = rs.getString(index++);
				String distributorId = rs.getString(index++);
				double quantityValue = rs.getDouble(index++);
				String quantityUnit = rs.getString(index++);
				Date dateOfOrder = rs.getDate(index++);
				Date dateofDelivery = rs.getDate(index++);
				double pricePerUnit = rs.getDouble(index++);
				double totalPrice = rs.getDouble(index++);
				String deliveryStatus = rs.getString(index++);
				String warehouseId = rs.getString(index++);
				poList1.add(new ProductOrder(orderId, name, productId, distributorId, quantityValue, quantityUnit,
						dateOfOrder, dateofDelivery, pricePerUnit, totalPrice, deliveryStatus, warehouseId));
			}

			if (isFetched == 0) {
				logger.error(Constants.LOGGER_ERROR_FETCH_FAILED);
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_FETCH_FAILED);

			} else {
				logger.info(Constants.LOGGER_INFO_DISPLAY_SUCCESSFUL);

			}

		} catch (SQLException sqlException) {
			logger.error(sqlException.getMessage());
			throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_TECHNICAL_PROBLEM);
		} finally {
			try {

				pst.close();
				connection.close();
			} catch (SQLException sqlException) {
				logger.error(sqlException.getMessage());
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);

			}
		}
		return poList1;

	}

	/*****************************************************************
	 * - Method Name: displayProductOrderbetweenDetails - Input Parameters : -
	 * Throws : Exception - Creation Date : 25/09/2019 - Description : Returns list
	 * of all received products between tow dates entered by user
	 *******************************************************************/

	public List<ProductOrder> displayProductOrderbetweenDetails(java.util.Date dt1, java.util.Date dt2)
			throws Exception {
		List<ProductOrder> poList1 = new ArrayList<ProductOrder>();
		Connection connection = DBUtil.getInstance().getConnection();

		PreparedStatement pst = null;
		try {
			pst = connection.prepareStatement(QueryMapper.DISPLAY_PRODUCT_ORDER_BW_DATES);

			pst.setDate(1, DBUtil.stringtoDate(dt1));
			pst.setDate(2, DBUtil.stringtoDate(dt2));
			ResultSet rs = pst.executeQuery();

			int isFetched = 0;
			while (rs.next()) {
				isFetched = 1;
				int index = 1;

				String orderId = Integer.valueOf(rs.getInt(index++)).toString();
				String name = rs.getString(index++);
				String productId = rs.getString(index++);
				String distributorId = rs.getString(index++);
				double quantityValue = rs.getDouble(index++);
				String quantityUnit = rs.getString(index++);
				Date dateOfOrder = rs.getDate(index++);
				Date dateofDelivery = rs.getDate(index++);
				double pricePerUnit = rs.getDouble(index++);
				double totalPrice = rs.getDouble(index++);
				String deliveryStatus = rs.getString(index++);
				String warehouseId = rs.getString(index++);
				poList1.add(new ProductOrder(orderId, name, productId, distributorId, quantityValue, quantityUnit,
						dateOfOrder, dateofDelivery, pricePerUnit, totalPrice, deliveryStatus, warehouseId));

			}

			if (isFetched == 0) {
				logger.error(Constants.LOGGER_ERROR_FETCH_FAILED);
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_NO_RECORDS_FOUND);

			} else {
				logger.info(Constants.LOGGER_INFO_DISPLAY_SUCCESSFUL);

			}

		} catch (SQLException sqlException) {
			logger.error(sqlException.getMessage());
			throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_TECHNICAL_PROBLEM);
		} finally {
			try {

				pst.close();
				connection.close();
			} catch (SQLException sqlException) {
				logger.error(sqlException.getMessage());
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);

			}
		}
		return poList1;

	}

	/*****************************************************************
	 * - Method Name: displayOrdersFromDistributor - Input Parameters : - Throws :
	 * Exception - Creation Date : 25/09/2019 - Description : Returns list of all
	 * received products from a particular distributor
	 *******************************************************************/

	public List<ProductOrder> displayOrdersFromDistributor(String distId) throws Exception {
		List<ProductOrder> poList1 = new ArrayList<ProductOrder>();
		Connection connection = DBUtil.getInstance().getConnection();
		PreparedStatement pst = null;

		try {

			pst = connection.prepareStatement(QueryMapper.DISPLAY_PRODUCT_ORDER_FROM_DISTRIBUTOR);
			pst.setString(1, distId);
			ResultSet rs = pst.executeQuery();
			int isFetched = 0;
			while (rs.next()) {
				isFetched = 1;
				int index = 1;

				String orderId = Integer.valueOf(rs.getInt(index++)).toString();
				String name = rs.getString(index++);
				String productId = rs.getString(index++);
				String distributorId = rs.getString(index++);
				double quantityValue = rs.getDouble(index++);
				String quantityUnit = rs.getString(index++);
				Date dateOfOrder = rs.getDate(index++);
				Date dateofDelivery = rs.getDate(index++);
				double pricePerUnit = rs.getDouble(index++);
				double totalPrice = rs.getDouble(index++);
				String deliveryStatus = rs.getString(index++);
				String warehouseId = rs.getString(index++);
				poList1.add(new ProductOrder(orderId, name, productId, distributorId, quantityValue, quantityUnit,
						dateOfOrder, dateofDelivery, pricePerUnit, totalPrice, deliveryStatus, warehouseId));

			}

			if (isFetched == 0) {
				logger.error(Constants.LOGGER_ERROR_FETCH_FAILED);
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_FETCH_FAILED);

			} else {
				logger.info(Constants.LOGGER_INFO_MESSAGE_DELIVERY_SUCCESSFUL);

			}

		} catch (SQLException sqlException) {
			logger.error(sqlException.getMessage());
			throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_TECHNICAL_PROBLEM);
		} finally {
			try {

				pst.close();
				connection.close();
			} catch (SQLException sqlException) {
				logger.error(sqlException.getMessage());
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);

			}
		}
		return poList1;

	}

	/*****************************************************************
	 * - Method Name: displayPendingProductOrderDetails - Input Parameters : -
	 * Throws : Exception - Creation Date : 25/09/2019 - Description : Returns list
	 * of all pending products from a particular distributor
	 *******************************************************************/

	public List<ProductOrder> displayPendingProductOrderDetails() throws Exception {
		List<ProductOrder> poList1 = new ArrayList<ProductOrder>();
		Connection connection = DBUtil.getInstance().getConnection();
		PreparedStatement pst = null;
		try {
			pst = connection.prepareStatement(QueryMapper.DISPLAY_PENDING_PRODUCT_ORDER);
			ResultSet rs = pst.executeQuery();

			int isFetched = 0;
			while (rs.next()) {
				isFetched = 1;
				int index = 1;

				String orderId = Integer.valueOf(rs.getInt(index++)).toString();
				String name = rs.getString(index++);
				String productId = rs.getString(index++);
				String distributorId = rs.getString(index++);
				double quantityValue = rs.getDouble(index++);
				String quantityUnit = rs.getString(index++);
				Date dateOfOrder = rs.getDate(index++);
				Date dateofDelivery = rs.getDate(index++);
				double pricePerUnit = rs.getDouble(index++);
				double totalPrice = rs.getDouble(index++);
				String deliveryStatus = rs.getString(index++);
				String warehouseId = rs.getString(index++);
				poList1.add(new ProductOrder(orderId, name, productId, distributorId, quantityValue, quantityUnit,
						dateOfOrder, dateofDelivery, pricePerUnit, totalPrice, deliveryStatus, warehouseId));

			}
			if (isFetched == 0) {
				logger.error(Constants.LOGGER_ERROR_FETCH_FAILED);
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_FETCH_FAILED);

			} else {
				logger.info(Constants.LOGGER_INFO_DISPLAY_SUCCESSFUL);

			}

		} catch (SQLException sqlException) {
			logger.error(sqlException.getMessage());
			throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_TECHNICAL_PROBLEM);
		} finally {
			try {

				pst.close();
				connection.close();
			} catch (SQLException sqlException) {
				logger.error(sqlException.getMessage());
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);

			}
		}
		return poList1;
	}

	/*****************************************************************
	 * - Method Name: displayReceivedProductOrderDetails - Input Parameters : -
	 * Throws : Exception - Creation Date : 25/09/2019 - Description : Returns list
	 * of all received products from a particular distributor
	 ********************************************************************/

	public List<ProductOrder> displayReceivedProductOrderDetails() throws Exception {

		List<ProductOrder> poList1 = new ArrayList<ProductOrder>();

		Connection connection = DBUtil.getInstance().getConnection();
		PreparedStatement pst = null;
		try {
			pst = connection.prepareStatement(QueryMapper.DISPLAY_RECEIVED_PRODUCT_ORDER);
			ResultSet rs = pst.executeQuery();

			int isFetched = 0;
			while (rs.next()) {
				isFetched = 1;
				int index = 1;

				String orderId = Integer.valueOf(rs.getInt(index++)).toString();
				String name = rs.getString(index++);
				String productId = rs.getString(index++);
				String distributorId = rs.getString(index++);
				double quantityValue = rs.getDouble(index++);
				String quantityUnit = rs.getString(index++);
				Date dateOfOrder = rs.getDate(index++);
				Date dateofDelivery = rs.getDate(index++);
				double pricePerUnit = rs.getDouble(index++);
				double totalPrice = rs.getDouble(index++);
				String deliveryStatus = rs.getString(index++);
				String warehouseId = rs.getString(index++);
				poList1.add(new ProductOrder(orderId, name, productId, distributorId, quantityValue, quantityUnit,
						dateOfOrder, dateofDelivery, pricePerUnit, totalPrice, deliveryStatus, warehouseId));

			}
			if (isFetched == 0) {
				logger.error(Constants.LOGGER_ERROR_FETCH_FAILED);
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_FETCH_FAILED);

			} else {
				logger.info(Constants.LOGGER_INFO_DISPLAY_SUCCESSFUL);

			}

		} catch (SQLException sqlException) {
			logger.error(sqlException.getMessage());
			throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_TECHNICAL_PROBLEM);
		} finally {
			try {

				pst.close();
				connection.close();
			} catch (SQLException sqlException) {
				logger.error(sqlException.getMessage());
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);

			}
		}
		return poList1;
	}

	/*****************************************************************
	 * - Method Name: displayDispatchedProductOrderDetails - Input Parameters : -
	 * Throws : Exception - Creation Date : 25/09/2019 - Description : Returns list
	 * of all dispatched products from a particular distributor
	 *******************************************************************/

	public List<ProductOrder> displayDispatchedProductOrderDetails() throws Exception {
		List<ProductOrder> poList1 = new ArrayList<ProductOrder>();

		Connection connection = DBUtil.getInstance().getConnection();
		PreparedStatement pst = null;
		try {
			pst = connection.prepareStatement(QueryMapper.DISPLAY_DISPATCHED_PRODUCT_ORDER);
			ResultSet rs = pst.executeQuery();

			int isFetched = 0;
			while (rs.next()) {
				isFetched = 1;
				int index = 1;

				String orderId = Integer.valueOf(rs.getInt(index++)).toString();
				String name = rs.getString(index++);
				String productId = rs.getString(index++);
				String distributorId = rs.getString(index++);
				double quantityValue = rs.getDouble(index++);
				String quantityUnit = rs.getString(index++);
				Date dateOfOrder = rs.getDate(index++);
				Date dateofDelivery = rs.getDate(index++);
				double pricePerUnit = rs.getDouble(index++);
				double totalPrice = rs.getDouble(index++);
				String deliveryStatus = rs.getString(index++);
				String warehouseId = rs.getString(index++);
				poList1.add(new ProductOrder(orderId, name, productId, distributorId, quantityValue, quantityUnit,
						dateOfOrder, dateofDelivery, pricePerUnit, totalPrice, deliveryStatus, warehouseId));

			}
			if (isFetched == 0) {
				logger.error(Constants.LOGGER_ERROR_FETCH_FAILED);
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_FETCH_FAILED);

			} else {
				logger.info(Constants.LOGGER_INFO_DISPLAY_SUCCESSFUL);

			}

		} catch (SQLException sqlException) {
			logger.error(sqlException.getMessage());
			throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_TECHNICAL_PROBLEM);
		} finally {
			try {

				pst.close();
				connection.close();
			} catch (SQLException sqlException) {
				logger.error(sqlException.getMessage());
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);

			}
		}
		return poList1;

	}

	/*****************************************************************
	 * - Method Name: displayCancelledProductOrderDetails - Input Parameters : -
	 * Throws : Exception - Creation Date : 25/09/2019 - Description : Returns list
	 * of all cancelled products from a particular distributor
	 *******************************************************************/

	public List<ProductOrder> displayCancelledProductOrderDetails() throws Exception {
		List<ProductOrder> poList1 = new ArrayList<ProductOrder>();
		Connection connection = DBUtil.getInstance().getConnection();
		PreparedStatement pst = null;
		try {
			pst = connection.prepareStatement(QueryMapper.DISPLAY_CANCELLED_PRODUCT_ORDER);
			ResultSet rs = pst.executeQuery();

			int isFetched = 0;
			while (rs.next()) {
				isFetched = 1;
				int index = 1;

				String orderId = Integer.valueOf(rs.getInt(index++)).toString();
				String name = rs.getString(index++);
				String productId = rs.getString(index++);
				String distributorId = rs.getString(index++);
				double quantityValue = rs.getDouble(index++);
				String quantityUnit = rs.getString(index++);
				Date dateOfOrder = rs.getDate(index++);
				Date dateofDelivery = rs.getDate(index++);
				double pricePerUnit = rs.getDouble(index++);
				double totalPrice = rs.getDouble(index++);
				String deliveryStatus = rs.getString(index++);
				String warehouseId = rs.getString(index++);
				poList1.add(new ProductOrder(orderId, name, productId, distributorId, quantityValue, quantityUnit,
						dateOfOrder, dateofDelivery, pricePerUnit, totalPrice, deliveryStatus, warehouseId));

			}
			if (isFetched == 0) {
				logger.error(Constants.LOGGER_ERROR_FETCH_FAILED);
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_FETCH_FAILED);

			} else {
				logger.info(Constants.LOGGER_INFO_DISPLAY_SUCCESSFUL);

			}

		} catch (SQLException sqlException) {
			logger.error(sqlException.getMessage());
			throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_TECHNICAL_PROBLEM);
		} finally {
			try {

				pst.close();
				connection.close();
			} catch (SQLException sqlException) {
				logger.error(sqlException.getMessage());
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);

			}
		}
		return poList1;

	}

	/*******************************************************************************************************
	 * - Function Name : add product order - Input Parameters : ProductOrder po -
	 * Return Type : String - Throws : Exception - Author : Capgemini - Creation
	 * Date : 25/09/2019 - Description : Product order is placed i.e. entry is added
	 * in database
	 * 
	 ********************************************************************************************************/

	@Override
	public boolean addProductOrder(ProductOrdersEntity newPO)
			throws ProductOrderNotAddedException, ConnectionException, SQLException, DisplayException {

//		Connection con;
//		try {
//			con = DBUtil.getInstance().getConnection();
//		} catch (Exception e) {
//			logger.error(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
//			throw new ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
//		}
//
//		PreparedStatement preparedStatement = null, preparedStatement1 = null;
		boolean added = false;
//		String pId = null;
//		try {
//			preparedStatement1 = con.prepareStatement(QueryMapper.FETCH_PRODUCTID_FROM_PRODUCTNAME);
//			preparedStatement1.setString(1, newPO.getName().toUpperCase());
//			ResultSet rs = preparedStatement1.executeQuery();
//			while (rs.next()) {
//				pId = rs.getString(1);
//			}
//		} catch (SQLException sqlException) {
//			logger.error(sqlException.getMessage());
//			throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_TECHNICAL_PROBLEM);
//		}
//
//		try {
//			preparedStatement = con.prepareStatement(QueryMapper.ADDPRODUCTORDER);
//			preparedStatement.setString(1, newPO.getName().toUpperCase());
//			preparedStatement.setString(2, pId.toUpperCase());
//			preparedStatement.setString(3, newPO.getDistributorId().toUpperCase());
//			preparedStatement.setDouble(4, newPO.getQuantityValue());
//			preparedStatement.setString(5, newPO.getQuantityUnit().toLowerCase());
//			preparedStatement.setDate(6, DBUtil.stringtoDate(newPO.getDateOfOrder()));
//			preparedStatement.setDate(7, DBUtil.stringtoDate(newPO.getDateofDelivery()));
//			preparedStatement.setDouble(8, newPO.getPricePerUnit());
//			preparedStatement.setDouble(9, newPO.getTotalPrice());
//			preparedStatement.setString(10, newPO.getDeliveryStatus().toUpperCase());
//			preparedStatement.setString(11, newPO.getWarehouseId().toLowerCase());
//
//			int noOfRows = preparedStatement.executeUpdate();
//
//			con.close();
//
//			if (noOfRows == 1) {
//				added = true;
//			}
//
//			if (!added) {
//				throw new ProductOrderNotAddedException(Constants.PRODUCT_ORDER_NOT_ADDED);
//			}
//			return added;
//		} catch (ProductOrderNotAddedException | SQLException exception) {
//			logger.error(Constants.PRODUCT_ORDER_NOT_ADDED);
//			throw exception;
//		}

		try {
			Session session = HibernateUtil.getSessionFactory().openSession();
			System.out.println("633");
			session.beginTransaction();
			session.save(newPO);
			session.getTransaction().commit();
		} catch (HibernateException e) {
			e.printStackTrace();
			System.out.println("Exception 638");
		}
		//HibernateUtil.shutdown();
		added = true;
		
		if (!added) {
			throw new ProductOrderNotAddedException(Constants.PRODUCT_ORDER_NOT_ADDED);
		}
		return added;
	}

	public boolean doesProductOrderIdExist(String orderId)
			throws ProductOrderIDDoesNotExistException, ConnectionException, SQLException {

		boolean pOrderIdFound = false;
		Connection connection;
		try {

			connection = DBUtil.getInstance().getConnection();
		} catch (Exception e) {

			logger.error(e.getMessage());
			throw new ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
		}

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		preparedStatement = connection.prepareStatement(QueryMapper.SELECT_ALL_PRODUCT_ORDER);
		preparedStatement.setString(1, orderId);
		resultSet = preparedStatement.executeQuery();

		while (resultSet.next()) {
			pOrderIdFound = true;
			break;
		}

		if (!pOrderIdFound) {
			logger.error(Constants.PRODUCT_ID_DOES_NOT_EXISTS_EXCEPTION);
			throw new ProductOrderIDDoesNotExistException(Constants.PRODUCT_ID_DOES_NOT_EXISTS_EXCEPTION);
		}

		connection.close();
		return pOrderIdFound;
	}

	@Override
	public boolean doesProductIdExist(String prodId, String name)
			throws ProductIDDoesNotExistException, ConnectionException, SQLException {

		boolean pIdFound = false;
		Connection connection;
		try {

			connection = DBUtil.getInstance().getConnection();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
		}

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		preparedStatement = connection.prepareStatement(QueryMapper.SELECT_PRODUCTID_ORDER);
		preparedStatement.setString(1, name.toUpperCase());
		resultSet = preparedStatement.executeQuery();

		while (resultSet.next()) {
			String pId = resultSet.getString(1);
			if (pId.equalsIgnoreCase(prodId)) {
				pIdFound = true;
				break;
			}
		}

		connection.close();
		if (pIdFound) {
			return pIdFound;
		}
		if (!pIdFound) {
			logger.error(Constants.PRODUCT_ID_DOES_NOT_EXISTS_EXCEPTION);
			throw new ProductIDDoesNotExistException(Constants.PRODUCT_ID_DOES_NOT_EXISTS_EXCEPTION);
		}

		return pIdFound;
	}

	@Override
	public boolean doesProductOrderIdExistInStock(String orderId)
			throws ProductOrderIDDoesNotExistException, ConnectionException, SQLException {

		boolean productOrderIdFound = false;
		int oid = -1;
		try {
			oid = Integer.parseInt(orderId);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return productOrderIdFound;
		}
		Connection connection;
		try {

			connection = DBUtil.getInstance().getConnection();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
			// throw new
			// ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
		}
		
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		preparedStatement = connection.prepareStatement(QueryMapper.SELECT_PRODUCT_STOCK);
		preparedStatement.setInt(1, oid);
		resultSet = preparedStatement.executeQuery();
		
		while (resultSet.next()) {
			
			int pId = resultSet.getInt(1);
			if (pId == oid) {
				productOrderIdFound = true;
				break;
			}
		}

		connection.close();
		if (productOrderIdFound) {
			
			return productOrderIdFound;
		}
		if (!productOrderIdFound) {
			logger.error(Constants.PRODUCT_ID_DOES_NOT_EXIST_IN_STOCK_EXCEPTION);
			return productOrderIdFound;
		}

		return productOrderIdFound;

	}

	public boolean doesDistributorIdExist(String distId)
			throws DistributorIDDoesNotExistException, ConnectionException, SQLException {

		boolean distIdFound = false;
		Connection connection;
		try {

			connection = DBUtil.getInstance().getConnection();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
		}

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		preparedStatement = connection.prepareStatement(QueryMapper.CHECK_IF_DISTRIBUTOR_ID_EXIST);
		preparedStatement.setString(1, distId);
		resultSet = preparedStatement.executeQuery();

		while (resultSet.next()) {
			distIdFound = true;
			break;
		}

		if (!distIdFound) {
			logger.error(Constants.DISTRIBUTOR_ID_DOES_NOT_EXISTS_EXCEPTION);
			throw new DistributorIDDoesNotExistException(Constants.DISTRIBUTOR_ID_DOES_NOT_EXISTS_EXCEPTION);
		}

		connection.close();
		return distIdFound;
	}

	public boolean doesProductNameExist(String name)
			throws ProductNameDoesNotExistException, ConnectionException, SQLException {

		boolean productNameFound = false;
		Connection connection;
		try {

			connection = DBUtil.getInstance().getConnection();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
		}

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		preparedStatement = connection.prepareStatement(QueryMapper.CHECK_IF_PRODUCT_NAME_EXIST);
		preparedStatement.setString(1, name);
		resultSet = preparedStatement.executeQuery();

		while (resultSet.next()) {
			productNameFound = true;
			break;
		}

		if (!productNameFound) {
			logger.error(Constants.PRODUCTNAME_DOES_NOT_EXISTS_EXCEPTION);
			throw new ProductNameDoesNotExistException(Constants.PRODUCTNAME_DOES_NOT_EXISTS_EXCEPTION);
		}

		connection.close();
		return productNameFound;

	}

	@Override
	public boolean doesWIdExist(String WId) throws WIdDoesNotExistException, ConnectionException, SQLException {
		boolean found = false;
		Connection connection;
		try {
			connection = DBUtil.getInstance().getConnection();
		} catch (Exception e) {
			throw new ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
		}
		Statement statement = null;
		ResultSet resultSet = null;
		statement = connection.createStatement();
		String sql = "SELECT * FROM Warehouse WHERE WarehouseID='" + WId.toLowerCase() + "';";
		resultSet = statement.executeQuery(sql);

		while (resultSet.next()) {
			found = true;
			break;
		}
		if (!found)
			throw new WIdDoesNotExistException(Constants.WAREHOUSE_ID_DOES_NOT_EXISTS_EXCEPTION);

		return found;
	}

	/*******************************************************************************************************
	 * - Function Name : track product order - Input Parameters : String orderid -
	 * Return Type : String - Throws : No Exception - Author : Capgemini - Creation
	 * Date : 25/09/2019 - Description : Product order is tracked in the warehouse
	 * along with its shelf life
	 ********************************************************************************************************/

	@Override
	public String trackProductOrder(ProductStock productStock) {
//		Connection connection = null;
//		PreparedStatement statement = null;
//		ResultSet resultSet = null;
//		try {
//			connection = DBUtil.getInstance().getConnection();
//
//			statement = connection.prepareStatement(QueryMapper.TRACKPRODUCTORDER);
//			statement.setInt(1, Integer.parseInt(productStock.getOrderId()));
//			resultSet = statement.executeQuery();
//
//			String warehouseId = null;
//			java.sql.Date exitDate = null;
//			java.sql.Date manDate = null;
//
//			while (resultSet.next()) {
//
//				exitDate = resultSet.getDate(1);
//
//				manDate = resultSet.getDate(2);
//
//				warehouseId = resultSet.getString(3);
//
//			}
//
//			String message = "The order ID had been in the warehouse with warehouseID = " + warehouseId + " from "
//					+ manDate.toString() + " to " + exitDate.toString() + "("
//					+ DBUtil.diffBetweenDays(exitDate, manDate) + " days)";
//
//			return message;
//
//		} catch (SQLException e) {
//			logger.error(Constants.LOGGER_ERROR_MESSAGE_QUERY_NOT_EXECUTED);
//			return Constants.LOGGER_ERROR_MESSAGE_QUERY_NOT_EXECUTED;
//		}
//
//		catch (Exception e) {
//			logger.error(Constants.LOGGER_ERROR_MESSAGE_DATABASE_NOT_CONNECTED);
//			return Constants.LOGGER_ERROR_MESSAGE_DATABASE_NOT_CONNECTED;
//		} finally {
//			try {
//				resultSet.close();
//				statement.close();
//				connection.close();
//			} catch (SQLException exception) {
//				logger.error(exception.getMessage());
//			}
//		}
		
		
		
		
//		Transaction transaction = null;
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            
//            transaction = session.beginTransaction();
//            
//            String hql = "select exitDate, manufacturingDate, warehouseID from ProductStock where orderID = :orderId";
//            Query q = session.createQuery(hql);
//            q.setParameter(0, Integer.parseInt(productStock.getOrderId()));
//            Object[] trackDetails = (Object[]) q.uniqueResult();
//            
//            
//            System.out.println(trackDetails[0] + ":" + trackDetails[1] + ":" + trackDetails[2]);
//            
//            
//            transaction.commit();
//            
//            
//            return "Hello";
//            
//        } catch (Exception e) {
//            if (transaction != null) {
//                transaction.rollback();
//            }
//            e.printStackTrace();
//            return "HelloException";
//        }
		
		
//		Configuration config = new Configuration().configure().addAnnotatedClass(ProductStockEntity.class);    
//        ServiceRegistry registry = new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build();
//        SessionFactory sf = config.buildSessionFactory(registry);   
//        Session session = sf.openSession();
		
		Session session = HibernateUtil.getASession(); 
        
        session.beginTransaction();
		
        String hql = "select exitDate, manufacturingDate, warehouseId from ProductStockEntity where orderId = :oId";
	      Query q = session.createQuery(hql);
	      q.setParameter("oId", Integer.parseInt(productStock.getOrderId()));
	      Object[] trackDetails = (Object[]) q.uniqueResult();
	      
	      session.getTransaction().commit();
	      
	      				Date exitDate = (Date) trackDetails[0];
	      
	      				Date manDate = (Date) trackDetails[1];
	      
	      				String warehouseId = (String) trackDetails[2];
	      
	      				System.out.println(trackDetails[0] + ":" + trackDetails[1] + ":" + trackDetails[2]);		
	      
	      			String message = "The order ID had been in the warehouse with warehouseID = " + warehouseId + " from "
	      					+ manDate.toString() + " to " + exitDate.toString() + "("
	      					+ DBUtil.diffBetweenDays(exitDate, manDate) + " days)";
	      
	      			return message;
	    
	}

	/*******************************************************************************************************
	 * - Function Name : Exit Date Check - Input Parameters : String orderId, Date
	 * exit_date - Return Type : boolean - Throws : ExitDateException, SQLException,
	 * ConnectionException - Author : CAPGEMINI - Creation Date : 25/09/2019 -
	 * Description : checking that exit_date should be after manufacturing_date and
	 * before expiry_date.
	 ********************************************************************************************************/

	@Override
	public boolean exitDateCheck(ProductStock productStock)
			throws ExitDateException, SQLException, ConnectionException {
//		Connection connection = null;
//		boolean datecheck = false;
//		PreparedStatement statement = null;
//		ResultSet resultSet = null;
//		try {
//			connection = DBUtil.getInstance().getConnection();
//			statement = connection.prepareStatement(QueryMapper.CHECKEXITDATE);
//			statement.setInt(1, Integer.parseInt(productStock.getOrderId()));
//			resultSet = statement.executeQuery();
//
//			java.sql.Date manufacturingDate = null;
//			java.sql.Date expiryDate = null;
//
//			while (resultSet.next()) {
//
//				manufacturingDate = resultSet.getDate(1);
//
//				expiryDate = resultSet.getDate(2);
//
//				if (productStock.getExitDate().after(manufacturingDate)
//						&& productStock.getExitDate().before(expiryDate)) {
//					datecheck = true;
//					return datecheck;
//				}
//			}
//			throw new ExitDateException(Constants.EXIT_DATE_EXCEPTION);
//
//		} catch (SQLException exception) {
//			logger.error(Constants.LOGGER_ERROR_MESSAGE_QUERY_NOT_EXECUTED);
//			throw new SQLException(Constants.LOGGER_ERROR_MESSAGE_QUERY_NOT_EXECUTED);
//
//		}
//
//		catch (ExitDateException exception) {
//			logger.error(exception.getMessage());
//			throw exception;
//
//		}
//
//		catch (Exception exception) {
//			logger.error(Constants.LOGGER_ERROR_MESSAGE_DATABASE_NOT_CONNECTED);
//			throw new ConnectionException(Constants.LOGGER_ERROR_MESSAGE_DATABASE_NOT_CONNECTED);
//
//		}
//
//		finally {
//			resultSet.close();
//			statement.close();
//			connection.close();
//		}
	try {
		boolean datecheck = false;
		Session session = HibernateUtil.getASession(); 
        session.beginTransaction();
        String hql = "select manufacturingDate, expiryDate from ProductStockEntity where orderId = :oId";
        Query q = session.createQuery(hql);
	      q.setParameter("oId", Integer.parseInt(productStock.getOrderId()));
	      Object[] dateDetails = (Object[]) q.uniqueResult();
	      
	      session.getTransaction().commit();
	      
	      Date manufacturingDate = (Date) dateDetails[0];
	      
			Date expiryDate = (Date) dateDetails[1];
			
			if (productStock.getExitDate().after(manufacturingDate)	&& productStock.getExitDate().before(expiryDate)) {
				datecheck = true;
				return datecheck;
			}
			
			throw new ExitDateException(Constants.EXIT_DATE_EXCEPTION);
		}	
			
		catch (ExitDateException exception) {
				logger.error(exception.getMessage());
				throw exception;
	
			}
		

	}

	/*******************************************************************************************************
	 * - Function Name : update ExitDate in Stock - Input Parameters : String
	 * orderId, Date Exit_date - Return Type : Void - Throws : SQL Exception,
	 * Exception - Author : CAPGEMINI - Creation Date : 25/09/2019 - Description :
	 * updating exit date for an orderId in the Product Stock table.
	 ********************************************************************************************************/

	@Override
	public String updateExitDateinStock(ProductStock productStock) {
//		Connection connection = null;
//		PreparedStatement statement = null;
//		try {
//			connection = DBUtil.getInstance().getConnection();
//
//			statement = connection.prepareStatement(QueryMapper.UPDATEEXITDATE);
//			statement.setDate(1, DBUtil.stringtoDate(productStock.getExitDate()));
//			statement.setInt(2, Integer.parseInt(productStock.getOrderId()));
//			statement.executeUpdate();
//			return Constants.DATA_INSERTED_MESSAGE;
//		}
//
//		catch (SQLException exception) {
//			logger.error(Constants.LOGGER_ERROR_MESSAGE_QUERY_NOT_EXECUTED);
//			return Constants.LOGGER_ERROR_MESSAGE_QUERY_NOT_EXECUTED;
//
//		} catch (Exception exception) {
//			logger.error(Constants.LOGGER_ERROR_MESSAGE_DATABASE_NOT_CONNECTED);
//			return Constants.LOGGER_ERROR_MESSAGE_DATABASE_NOT_CONNECTED;
//		} finally {
//			try {
//				statement.close();
//				connection.close();
//			} catch (SQLException exception) {
//				logger.error(exception.getMessage());
//			}
//
//		}
		
		
		Session session = HibernateUtil.getASession(); 
        session.beginTransaction();
        String hql = "update ProductStockEntity set exitDate = :exitDateVariable where orderId = :oId";
        Query q = session.createQuery(hql);
	      q.setParameter("oId", Integer.parseInt(productStock.getOrderId()));
	      q.setParameter("exitDateVariable", productStock.getExitDate());

	}

	/*******************************************************************************************************
	 * - Function Name : update product stock - Input Parameters : String orderid,
	 * Date manufacturing date, Date exit date, String quality Status - Return Type
	 * : Void - Throws : SQL Exception, Exception - Author : Capgemini - Creation
	 * Date : 25/09/2019 - Description : updating manufacturing date, exit date and
	 * quality status into product stock table.
	 ********************************************************************************************************/

	@Override
	public String updateProductStock(ProductStock productStock) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		PreparedStatement statement2 = null;
		PreparedStatement statement1 = null;
		try {
			
			connection = DBUtil.getInstance().getConnection();
			
			boolean orderIdcheckInStock = false;
			orderIdcheckInStock = doesProductOrderIdExistInStock(productStock.getOrderId());
			if (orderIdcheckInStock == false) {
				
				statement = connection.prepareStatement(QueryMapper.RETRIEVEPRODUCTORDERDETAILSFORPRODUCTSTOCK);
				statement.setInt(1, Integer.parseInt(productStock.getOrderId()));
				resultSet = statement.executeQuery();
				String name = null;
				double priceperunit = 0;
				double quantityValue = 0;
				String quantityUnit = null;
				double totalprice = 0;
				String warehouseId = null;
				Date dateofdelivery = null;

				while (resultSet.next()) {
					
					name = resultSet.getString(1);
					priceperunit = resultSet.getDouble(2);
					quantityValue = resultSet.getDouble(3);
					quantityUnit = resultSet.getString(4);
					totalprice = resultSet.getDouble(5);
					warehouseId = resultSet.getString(6);
					dateofdelivery = resultSet.getDate(7);
				}
				
				statement2 = connection.prepareStatement(QueryMapper.INSERTPRODUCTSTOCK);
				statement2.setInt(1, Integer.parseInt(productStock.getOrderId()));
				statement2.setString(2, name);
				statement2.setDouble(3, priceperunit);
				statement2.setDouble(4, quantityValue);
				statement2.setString(5, quantityUnit);
				statement2.setDouble(6, totalprice);
				statement2.setString(7, warehouseId);
				statement2.setDate(8, DBUtil.stringtoDate(dateofdelivery));

				statement2.executeUpdate();
				
				resultSet.close();
				statement.close();
				statement2.close();
			}
			
			statement1 = connection.prepareStatement(QueryMapper.UPDATEPRODUCTSTOCK);
			statement1.setDate(1, DBUtil.stringtoDate(productStock.getManufacturingDate()));
			statement1.setDate(2, DBUtil.stringtoDate(productStock.getExpiryDate()));
			statement1.setString(3, productStock.getQualityCheck());
			statement1.setInt(4, Integer.parseInt(productStock.getOrderId()));
			statement1.executeUpdate();

			return Constants.DATA_INSERTED_MESSAGE;

		}

		catch (SQLException exception) {
			logger.error(Constants.LOGGER_ERROR_MESSAGE_QUERY_NOT_EXECUTED);
			return Constants.LOGGER_ERROR_MESSAGE_DATABASE_NOT_CONNECTED;

		}

		catch (Exception exception) {
			logger.error(Constants.LOGGER_ERROR_MESSAGE_DATABASE_NOT_CONNECTED);
			return Constants.LOGGER_ERROR_MESSAGE_DATABASE_NOT_CONNECTED;
		}

		finally {
			try {

				//statement1.close();

				connection.close();
			} catch (SQLException exception) {
				logger.error(exception.getMessage());
			}
		}

	}

	// ------------------------------------------------------------------------------------------------------------------------------------

	public Distributor fetchDistributorDetail(Distributor distributor) throws BackEndException, DoesNotExistException {
		Connection connection;
		try {
			connection = DBUtil.getInstance().getConnection();
		} catch (Exception exception) {
			logger.error(Constants.DISTRIBUTOR_LOGGER_ERROR_DATABASE_NOTCONNECTED + exception.getMessage());
			throw new BackEndException(
					Constants.DISTRIBUTOR_LOGGER_ERROR_DATABASE_NOTCONNECTED + exception.getMessage());
		}
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;

		try {
			preparedStatement = connection.prepareStatement(QueryMapper.SELECT_ONE_DISTRIBUTOR_ID);
			preparedStatement.setString(1, distributor.getDistributorId());
			resultSet = preparedStatement.executeQuery();
			int DistributorCounter = 0;
			while (resultSet.next()) {
				DistributorCounter++;
				distributor.setName(resultSet.getString(2));
				distributor.setEmailId(resultSet.getString(4));
				distributor.setPhoneNo(resultSet.getString(5));
			}
			if (DistributorCounter == 0)
				throw new DoesNotExistException(Constants.DISTRIBUTOR_ID_DOES_NOT_EXISTS_EXCEPTION);
		} catch (SQLException exception) {
			logger.error(Constants.DISTRIBUTOR_LOGGER_ERROR_FETCHING_FAILED + exception.getMessage());
			throw new BackEndException(Constants.DISTRIBUTOR_LOGGER_ERROR_FETCHING_FAILED + exception.getMessage());

		} finally {
			try {
				resultSet.close();
				preparedStatement.close();
				connection.close();
			} catch (SQLException exception) {
				logger.error(exception.getMessage());
				throw new BackEndException(exception.getMessage());
			}
		}
		return distributor;
	}
	
	

	public Address fetchAddress(Distributor distributor) throws BackEndException, DoesNotExistException {
		Connection connection;
		try {
			connection = DBUtil.getInstance().getConnection();
		} catch (Exception exception) {
			logger.error(Constants.DISTRIBUTOR_LOGGER_ERROR_DATABASE_NOTCONNECTED + exception.getMessage());
			throw new BackEndException(
					Constants.DISTRIBUTOR_LOGGER_ERROR_DATABASE_NOTCONNECTED + exception.getMessage());
		}
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		Address address = new Address();
		address.setAddressId(distributor.getAddressId());
		try {
			preparedStatement = connection.prepareStatement(QueryMapper.SELECT_ONE_DISTRIBUTOR_ADDRESS_ID);
			preparedStatement.setInt(1, distributor.getAddressId());
			resultSet = preparedStatement.executeQuery();
			int AddressIdCounter = 0;
			while (resultSet.next()) {
				AddressIdCounter++;

				address.setPlotNo(resultSet.getInt(2));
				address.setBuildingName(resultSet.getString(3));
				address.setStreetName(resultSet.getString(4));
				address.setLandmark(resultSet.getString(5));
				address.setCity(resultSet.getString(6));
				address.setState(resultSet.getString(7));
				address.setPincode(resultSet.getString(8));

			}

			if (AddressIdCounter == 0)
				throw new DoesNotExistException(Constants.DISTRIBUTOR_ADDRESS_ID_DOES_NOT_EXISTS_EXCEPTION);
		} catch (SQLException exception) {
			logger.error(Constants.DISTRIBUTOR_LOGGER_ERROR_FETCHING_FAILED + exception.getMessage());
			throw new BackEndException(Constants.DISTRIBUTOR_LOGGER_ERROR_FETCHING_FAILED + exception.getMessage());

		} finally {
			try {
				connection.close();
			} catch (SQLException exception) {
				logger.error(exception.getMessage());
				throw new BackEndException(exception.getMessage());
			}
		}
		return address;
	}

	/*******************************************************************************************************
	 * - Function Name :Product Display - Input Parameters : String orderid, Date
	 * manufacturing date, Date exit date, String quality Status - Return Type :
	 * Void - Throws : SQL Exception, Exception - Author : Capgemini - Creation Date
	 * : 25/09/2019 - Description : updating manufacturing date, exit date and
	 * quality status into product stock table.
	 ********************************************************************************************************/
	@Override
	public List<ProductOrder> displayProductOrders(DisplayProductOrder displayProductOrderObject) throws Exception {
		List<ProductOrder> poList = new ArrayList<ProductOrder>();
		PreparedStatement pst = null;
		Connection con = null;
		int isFetched = 0;
		try {
			con = DBUtil.getInstance().getConnection();
			String DeliveryStatus = displayProductOrderObject.getDeliveryStatus();
			String generateQuery = "";
			{
				if (DeliveryStatus.equals("ALL"))
					generateQuery = "SELECT * FROM ProductOrders WHERE deliverystatus in "
							+ "( select DeliveryStatus from ProductOrders )";
				else
					generateQuery = "SELECT * FROM ProductOrders WHERE deliverystatus in ( '" + DeliveryStatus + "')";

			}

			String distributorid = displayProductOrderObject.getDistributorid();
			{
				if (distributorid.equals("ALL"))
					generateQuery += " AND distributorid in ( select distributorid  from ProductOrders )";
				else

					generateQuery += " AND distributorid in ( '" + distributorid + "' )";
			}

			String startDate = displayProductOrderObject.getStartdate();
			String endDate = displayProductOrderObject.getEndDate();

			if (startDate != null && endDate != null)

				generateQuery += " AND  dateofdelivery BETWEEN '" + startDate + "' AND '" + endDate + "'  ";
			System.out.println(generateQuery);
			pst = con.prepareStatement(generateQuery);
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				isFetched = 1;
				int index = 1;
				String orderId = Integer.valueOf(rs.getInt(index++)).toString();
				String name = rs.getString(index++);
				String productId = rs.getString(index++);
				String distributorId = rs.getString(index++);
				double quantityValue = rs.getDouble(index++);
				String quantityUnit = rs.getString(index++);
				Date dateOfOrder = rs.getDate(index++);
				Date dateofDelivery = rs.getDate(index++);
				double pricePerUnit = rs.getDouble(index++);
				double totalPrice = rs.getDouble(index++);
				String deliveryStatus = rs.getString(index++);
				String warehouseId = rs.getString(index++);
				poList.add(new ProductOrder(orderId, name, productId, distributorId, quantityValue, quantityUnit,
						dateOfOrder, dateofDelivery, pricePerUnit, totalPrice, deliveryStatus, warehouseId));
			}
			if (isFetched == 0) {
				logger.error(Constants.LOGGER_ERROR_FETCH_FAILED);
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_INALID_INPUT);
			}
		} catch (SQLException sqlException) {
			logger.error(sqlException.getMessage());
			throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_TECHNICAL_PROBLEM);
		} finally {
			try {
				// resultSet.close();
				pst.close();
				con.close();
			} catch (SQLException sqlException) {
				logger.error(sqlException.getMessage());
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
			}
		}
		return poList;
	}

	@Override
	public ArrayList<String> getProductNames() throws DisplayException, ConnectionException {

		ArrayList<String> productNamesList = new ArrayList<String>();
		Connection connection;
		try {
			connection = DBUtil.getInstance().getConnection();
		} catch (Exception e) {
			logger.error(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
			throw new ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
		}
		PreparedStatement pst = null;

		try {
			pst = connection.prepareStatement(QueryMapper.FETCH_PRODUCT_NAMES);
			ResultSet rs = pst.executeQuery();

			int isFetched = 0;
			while (rs.next()) {
				isFetched = 1;
				String productName = rs.getString(1);
				productNamesList.add(productName);
			}

			if (isFetched == 0) {
				logger.error(Constants.LOGGER_ERROR_FETCH_FAILED);
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_FETCH_FAILED);

			} else {
				logger.info(Constants.LOGGER_INFO_DISPLAY_SUCCESSFUL);

			}

		} catch (SQLException sqlException) {
			logger.error(sqlException.getMessage());
			throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_TECHNICAL_PROBLEM);
		} finally {
			try {

				pst.close();
				connection.close();
			} catch (SQLException sqlException) {
				logger.error(sqlException.getMessage());
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);

			}
		}
		return productNamesList;
	}

	@Override
	public ArrayList<String> getDistributorIds() throws DisplayException, ConnectionException {

		ArrayList<String> distributorIdsList = new ArrayList<String>();
		Connection connection;
		try {
			connection = DBUtil.getInstance().getConnection();
		} catch (Exception e) {
			logger.error(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
			throw new ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
		}
		PreparedStatement pst = null;

		try {
			pst = connection.prepareStatement(QueryMapper.FETCH_DISTRIBUTOR_IDS);
			ResultSet rs = pst.executeQuery();

			int isFetched = 0;
			while (rs.next()) {
				isFetched = 1;
				String distributorId = rs.getString(1);
				distributorIdsList.add(distributorId);
			}

			if (isFetched == 0) {
				logger.error(Constants.LOGGER_ERROR_FETCH_FAILED);
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_FETCH_FAILED);

			} else {
				logger.info(Constants.LOGGER_INFO_DISPLAY_SUCCESSFUL);

			}

		} catch (SQLException sqlException) {
			logger.error(sqlException.getMessage());
			throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_TECHNICAL_PROBLEM);
		} finally {
			try {
				pst.close();
				connection.close();
			} catch (SQLException sqlException) {
				logger.error(sqlException.getMessage());
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);

			}
		}
		return distributorIdsList;
	}

	@Override
	public ArrayList<String> getWarehouseIds() throws DisplayException, ConnectionException {

		ArrayList<String> warehouseIdsList = new ArrayList<String>();
		Connection connection;
		try {
			connection = DBUtil.getInstance().getConnection();
		} catch (Exception e) {
			logger.error(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
			throw new ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
		}
		PreparedStatement pst = null;

		try {
			pst = connection.prepareStatement(QueryMapper.FETCH_WAREHOUSE_IDS);
			ResultSet rs = pst.executeQuery();

			int isFetched = 0;
			while (rs.next()) {
				isFetched = 1;
				String warehouseId = rs.getString(1);
				warehouseIdsList.add(warehouseId);
			}

			if (isFetched == 0) {
				logger.error(Constants.LOGGER_ERROR_FETCH_FAILED);
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_FETCH_FAILED);

			} else {
				logger.info(Constants.LOGGER_INFO_DISPLAY_SUCCESSFUL);

			}

		} catch (SQLException sqlException) {
			logger.error(sqlException.getMessage());
			throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_TECHNICAL_PROBLEM);
		} finally {
			try {
				pst.close();
				connection.close();
			} catch (SQLException sqlException) {
				logger.error(sqlException.getMessage());
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);

			}
		}
		return warehouseIdsList;
	}

}


//class App {
//public static void main(String[] args) {
//	ProductDAOImpl p = new ProductDAOImpl();
//	String str = p.trackProductOrder(new ProductStock("5"));
//	System.out.println(str);
//}
//}
