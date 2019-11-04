package com.capgemini.dnd.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import com.capgemini.dnd.customexceptions.BackEndException;
import com.capgemini.dnd.customexceptions.ConnectionException;
import com.capgemini.dnd.customexceptions.DisplayException;
import com.capgemini.dnd.customexceptions.DoesNotExistException;
import com.capgemini.dnd.customexceptions.ExitDateException;
import com.capgemini.dnd.customexceptions.ProductOrderIDDoesNotExistException;
import com.capgemini.dnd.customexceptions.ProductOrderNotAddedException;
import com.capgemini.dnd.customexceptions.UpdateException;
import com.capgemini.dnd.dto.Address;
import com.capgemini.dnd.dto.DisplayProductOrder;
import com.capgemini.dnd.dto.Distributor;
import com.capgemini.dnd.dto.ProductOrder;
import com.capgemini.dnd.dto.ProductStock;
import com.capgemini.dnd.entity.ProductStockEntity;
import com.capgemini.dnd.util.DBUtil;
import com.capgemini.dnd.util.HibernateUtil;
import com.capgemini.dnd.dao.Constants;
import com.capgemini.dnd.entity.ProductOrdersEntity;
import com.capgemini.dnd.dao.QueryMapper;

import org.hibernate.query.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ProductDAOImpl implements ProductDAO {

	// private static final Distributor supplier = null;
	Logger logger = Logger.getRootLogger();
	
	@Autowired
	private SessionFactory sessionFactory;
	
	

	/*******************************************
	 * Product order delivery status update
	 * Author: Ankit Kumar
	 * 
	 *********************************************/
	public String updateStatusProductOrder(String orderId,String deliveryStatus)  {
		Session session = null;
        Transaction transaction = null;
        try {
        	session = HibernateUtil.getASession();
            // start a transaction
            transaction = session.beginTransaction();
            ProductOrdersEntity product = (ProductOrdersEntity)session.get(ProductOrdersEntity.class,Integer.parseInt(orderId));
            product.setDeliveryStatus(deliveryStatus ); 
            session.save(product);
            // commit transaction
            transaction.commit();
            return Constants.UPADTED_SUCCESSFULLY_MESSAGE;
            //int result = query.executeUpdate();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            try
            { 
                // Throw an object of user defined exception 
                throw new UpdateException(Constants.UPDATE_EXCEPTION_MESSAGE_FAILURE_DELIVERY); 
            } 
            catch (UpdateException ex) 
            { 
            return ex.getMessage();
        }
    }
        finally {
        	session.close();
        }
}
//	public String updateStatusProductOrder(String oid, String newStatus) throws Exception {
//
//		Connection con = DBUtil.getInstance().getConnection();
//		PreparedStatement preparedStatement = null;
//		java.util.Date today_date = new Date();
//		int queryResult = 0;
//		if (newStatus.equalsIgnoreCase("RECEIVED")) {
//			try {
//				preparedStatement = con.prepareStatement(QueryMapper.UPDATE_DELIVERY_STATUS);
//
//				preparedStatement.setString(1, newStatus);
//				preparedStatement.setDate(2, DBUtil.stringtoDate(today_date));
//				preparedStatement.setInt(3, Integer.parseInt(oid));
//				queryResult = preparedStatement.executeUpdate();
//				if (queryResult == 0) {
//					logger.error(Constants.LOGGER_ERROR_MESSAGE_FAILED_UPDATION);
//					throw new UpdateException(Constants.UPDATE_EXCEPTION_MESSAGE_FAILURE_DELIVERY);
//
//				} else {
//					logger.info(Constants.LOGGER_INFO_DISPLAY_SUCCESSFUL);
//					return Constants.UPADTED_SUCCESSFULLY_MESSAGE;
//				}
//
//			} catch (SQLException sqlException) {
//				logger.error(sqlException.getMessage());
//
//				throw new UpdateException(Constants.UPDATE_EXCEPTION_MESSAGE_TECHNICAL_PROBLEM);
//			} finally {
//				try {
//
//					preparedStatement.close();
//					con.close();
//				} catch (SQLException sqlException) {
//					logger.error(sqlException.getMessage());
//					throw new UpdateException(Constants.UPDATE_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
//
//				}
//			}
//		} else {
//			try {
//				preparedStatement = con.prepareStatement(QueryMapper.UPDATE_DELIVERY_STATUS1);
//				preparedStatement.setString(1, newStatus);
//				preparedStatement.setInt(2, Integer.parseInt(oid));
//
//				queryResult = preparedStatement.executeUpdate();
//				if (queryResult == 0) {
//					logger.error(Constants.LOGGER_ERROR_MESSAGE_FAILED_UPDATION);
//					throw new UpdateException(Constants.UPDATE_EXCEPTION_MESSAGE_FAILURE_DELIVERY);
//
//				} else {
//					logger.info(Constants.LOGGER_INFO_DISPLAY_SUCCESSFUL);
//					return Constants.UPADTED_SUCCESSFULLY_MESSAGE;
//				}
//			} catch (SQLException sqlException) {
//				logger.error(sqlException.getMessage());
//				throw new UpdateException(Constants.UPDATE_EXCEPTION_MESSAGE_TECHNICAL_PROBLEM);
//			} finally {
//				try {
//
//					preparedStatement.close();
//					con.close();
//				} catch (SQLException sqlException) {
//					logger.error(sqlException.getMessage());
//					throw new UpdateException(Constants.UPDATE_EXCEPTION_MESSAGE_TECHNICAL_PROBLEM);
//
//				}
//			}
//		}
//
//	}

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
	public boolean addProductOrder(ProductOrder newPO)
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

		ProductOrdersEntity productOrdersEntity = new ProductOrdersEntity(newPO.getName(), newPO.getDistributorId(), newPO.getQuantityValue(), newPO.getQuantityUnit(), newPO.getDateofDelivery(), newPO.getPricePerUnit(), newPO.getWarehouseId());
		Session session = HibernateUtil.getASession();
		try {
			
			session.beginTransaction();
			session.save(productOrdersEntity);
			session.getTransaction().commit();
			added = true;
		} catch (HibernateException e) {
			e.printStackTrace();
			System.out.println("Exception 638");
		} finally {
			session.close();
		}
		//HibernateUtil.shutdown();
		
		if (!added) {
			throw new ProductOrderNotAddedException(Constants.PRODUCT_ORDER_NOT_ADDED);
		}
		return added;
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
	
	
	
	@Override
	public String trackProductOrder(ProductStock productStock) {
		
//		Session session = sessionFactory.getCurrentSession();
		Session session = sessionFactory.openSession();
		ProductStockEntity productStockEntity = session.load(ProductStockEntity.class, Integer.parseInt(productStock.getOrderId()));
		// session.getTransaction().commit();
//		if (session.getTransaction() !=null && session.getTransaction().isActive()) {
//			 session.getTransaction().rollback();
//			}
	      
			Date exitDate = productStockEntity.getExitDate();

			Date manDate = productStockEntity.getManufacturingDate();

			String warehouseId = productStockEntity.getWarehouseId();

			if(exitDate == null || manDate == null) {
				return "Data Incomplete...Please check database and update required information";
			}
			
			String message = "The order ID had been in the warehouse with warehouseID = " + warehouseId + " from "
				+ manDate.toString() + " to " + exitDate.toString() + "("
				+ DBUtil.diffBetweenDays(exitDate, manDate) + " days)";
			session.close();
		return message;
	
	
	}

	@Override
	public boolean doesProductOrderIdExist(String orderId) throws ProductOrderIDDoesNotExistException {
		boolean pOrderIdFound = false;
		int oid = -1;
		try {
			oid = Integer.parseInt(orderId);
		} catch (Exception e) {
//			logger.error(e.getMessage());
			return pOrderIdFound;
		}
		
//		Session session = sessionFactory.getCurrentSession();
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		@SuppressWarnings("rawtypes")
		Query query = session.createQuery("from ProductStockEntity where orderId = :oId");
		query.setParameter("oId", oid);
		if (query.getResultList().size() == 1) {
			pOrderIdFound = true;
			session.getTransaction().commit();
			session.close();
			return pOrderIdFound;
		} else {
//			logger.error(Constants.PRODUCT_ID_DOES_NOT_EXISTS_EXCEPTION);
//			session.close();
			throw new ProductOrderIDDoesNotExistException(Constants.PRODUCT_ID_DOES_NOT_EXISTS_EXCEPTION);
		}

	}

	@Override
	public boolean exitDateCheck(ProductStock productStock) throws ExitDateException {
		
		Session session = null;
		try {
			boolean datecheck = false;
			session = sessionFactory.openSession();
			session.beginTransaction();
//	        String hql = "select manufacturingDate, expiryDate from ProductStockEntity where orderId = :oId";
//	        Query q = session.createQuery(hql);
//		      q.setParameter("oId", Integer.parseInt(productStock.getOrderId()));
//		      Object[] dateDetails = (Object[]) q.uniqueResult();
		      
	        ProductStockEntity productStockEntity = session.load(ProductStockEntity.class, Integer.parseInt(productStock.getOrderId()));
//		      session.getTransaction().commit();
		      
		      Date manufacturingDate = productStockEntity.getManufacturingDate();
		      
				Date expiryDate = productStockEntity.getExpiryDate();
				
				if (productStock.getExitDate().after(manufacturingDate)	&& productStock.getExitDate().before(expiryDate)) {
					datecheck = true;
					return datecheck;
				}
				
				throw new ExitDateException(Constants.EXIT_DATE_EXCEPTION);
			}	
				
			catch (ExitDateException exception) {
//					logger.error(exception.getMessage());
					throw exception;
		
				}
		
		finally {
			session.close();
		}
			

	}

	@Override
	public String updateExitDateinStock(ProductStock productStock) {

		Session session = sessionFactory.openSession();
		session.beginTransaction();
        String hql = "update ProductStockEntity set exitDate = :exitDateVariable where orderId = :oId";
        Query q = session.createQuery(hql);
	      q.setParameter("oId", Integer.parseInt(productStock.getOrderId()));
	      q.setParameter("exitDateVariable", productStock.getExitDate());
	      int result = q.executeUpdate();
	      session.getTransaction().commit();
	  	
	    if (session.getTransaction() != null && session.getTransaction().isActive()) {
			 session.getTransaction().rollback();
		}
	      
	    session.close();
	    
	    return Constants.DATA_INSERTED_MESSAGE;
	}

	@Override
	public String updateProductStock(ProductStock productStock) {
		
		Session session = sessionFactory.openSession(); 
        session.beginTransaction();
        
        boolean orderIdcheckInStock = false;
        System.out.println("1");
		orderIdcheckInStock = doesProductOrderIdExistInStock(productStock.getOrderId());
		System.out.println("2");
		if (orderIdcheckInStock == false) {
			System.out.println("3");
			String hql = "insert into ProductStockEntity(orderId, name, pricePerUnit, quantityValue, quantityUnit, totalPrice, warehouseId, dateofDelivery)" +  " select orderId, name, pricePerUnit, quantityValue, quantityUnit, totalPrice, warehouseId, dateofDelivery from ProductOrdersEntity where orderId = :oId";
			Query q = session.createQuery(hql);
		      q.setParameter("oId", Integer.parseInt(productStock.getOrderId()));
			
			int result = q.executeUpdate();
			System.out.println(result + ":");
		}
		System.out.println("4");
		String hql = "update ProductStockEntity set manufacturingDate = :manDate, expiryDate = :expDate, qualityCheck = :qaCheck where orderID = :oId";
		Query q1 = session.createQuery(hql);
	      q1.setParameter("oId", Integer.parseInt(productStock.getOrderId()));
	      q1.setParameter("manDate", productStock.getManufacturingDate());
	      q1.setParameter("expDate", productStock.getExpiryDate());
	      q1.setParameter("qaCheck", productStock.getQualityCheck());
	      
	      int result = q1.executeUpdate();
			System.out.println(result);
			System.out.println("5");
			session.getTransaction().commit();
			if (session.getTransaction() != null && session.getTransaction().isActive()) {
				 session.getTransaction().rollback();
			}
		    session.close();
		      return Constants.DATA_INSERTED_MESSAGE;
       

	}

	@Override
	public boolean doesProductOrderIdExistInStock(String orderId) {
		
		boolean productOrderIdFound = false;
		int oid = -1;
		try {
			oid = Integer.parseInt(orderId);
		} catch (Exception e) {
//			logger.error(e.getMessage());
			return productOrderIdFound;
		}
		
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		@SuppressWarnings("rawtypes")
		Query query = session.createQuery("from ProductStockEntity where orderId = :oId");
		query.setParameter("oId", oid);
		if (query.getResultList().size() == 1) {
			productOrderIdFound = true;
//			session.getTransaction().commit();
			session.close();
			return productOrderIdFound;
		} else {
//			logger.error(Constants.PRODUCT_ID_DOES_NOT_EXIST_IN_STOCK_EXCEPTION);
			session.close();
			return productOrderIdFound;
		}
	}
	
	
	

}


