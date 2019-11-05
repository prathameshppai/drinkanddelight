package com.capgemini.dnd.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.capgemini.dnd.customexceptions.BackEndException;
import com.capgemini.dnd.customexceptions.ConnectionException;
import com.capgemini.dnd.customexceptions.DisplayException;
import com.capgemini.dnd.customexceptions.DoesNotExistException;
import com.capgemini.dnd.customexceptions.IncompleteDataException;
import com.capgemini.dnd.customexceptions.ProcessDateException;
import com.capgemini.dnd.customexceptions.RMOrderIDDoesNotExistException;
import com.capgemini.dnd.customexceptions.RMOrderNotAddedException;
import com.capgemini.dnd.customexceptions.RowNotAddedException;
import com.capgemini.dnd.customexceptions.RowNotFoundException;
import com.capgemini.dnd.customexceptions.SupplierAddressDoesNotExistsException;
import com.capgemini.dnd.customexceptions.UpdateException;
import com.capgemini.dnd.dto.Address;
import com.capgemini.dnd.dto.DisplayRawMaterialOrder;
import com.capgemini.dnd.dto.RawMaterialOrder;
import com.capgemini.dnd.dto.RawMaterialStock;
import com.capgemini.dnd.dto.Supplier;
import com.capgemini.dnd.entity.RawMaterialOrderEntity;
import com.capgemini.dnd.entity.RawMaterialStockEntity;
import com.capgemini.dnd.util.DBUtil;
import com.capgemini.dnd.util.HibernateUtil;
import com.capgemini.dnd.dao.Constants;

@Repository
public class RawMaterialDAOImpl implements RawMaterialDAO {

	Logger logger = Logger.getRootLogger();
	
	@Autowired
	private SessionFactory sessionFactory;

	public RawMaterialDAOImpl() {}
	
	
	public String updateStatusRawMaterialOrder(String orderId,String deliveryStatus)  {
		Session session = null;
        Transaction transaction = null;
        try {
        	session = HibernateUtil.getASession();
            // start a transaction
            transaction = session.beginTransaction();
            RawMaterialOrderEntity rawmaterialorder = (RawMaterialOrderEntity)session.get(RawMaterialOrderEntity.class,Integer.parseInt(orderId));
            rawmaterialorder.setDeliveryStatus(deliveryStatus ); 
            session.save(rawmaterialorder);
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
//	public String updateStatusRawMaterialOrder(String oid, String newStatus) throws Exception {
//		Connection con = DBUtil.getInstance().getConnection();
//		PreparedStatement preparedStatement = null;
//		int queryResult = 0;
//		java.util.Date today_date = new Date();
//		if (newStatus.equalsIgnoreCase("RECEIVED")) {
//			try {
//				preparedStatement = con.prepareStatement(QueryMapper.UPDATE_RM_DELIVERY_STATUS);
//				preparedStatement.setString(1, newStatus);
//				preparedStatement.setDate(2, DBUtil.stringtoDate(today_date));
//				preparedStatement.setInt(3, Integer.parseInt(oid));
//				queryResult = preparedStatement.executeUpdate();
//				if (queryResult == 0) {
//					logger.error(Constants.LOGGER_ERROR_MESSAGE_FAILED_UPDATION);
//					throw new UpdateException(Constants.UPDATE_EXCEPTION_MESSAGE_FAILURE_DELIVERY);
//
//				} else {
//					logger.info(Constants.LOGGER_INFO_MESSAGE_DELIVERY_SUCCESSFUL);
//					return Constants.UPADTED_SUCCESSFULLY_MESSAGE;
//				}
//
//			} catch (SQLException sqlException) {
//				logger.error(sqlException.getMessage());
//				throw new UpdateException(Constants.UPDATE_EXCEPTION_MESSAGE_TECHNICAL_PROBLEM);
//			} finally {
//				try {
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
//				preparedStatement = con.prepareStatement(QueryMapper.UPDATE_RM_DELIVERY_STATUS1);
//
//				preparedStatement.setString(1, newStatus);
//				preparedStatement.setInt(2, Integer.parseInt(oid));
//
//				queryResult = preparedStatement.executeUpdate();
//				if (queryResult == 0) {
//					logger.error(Constants.LOGGER_ERROR_MESSAGE_FAILED_UPDATION);
//					throw new UpdateException(Constants.UPDATE_EXCEPTION_MESSAGE_FAILURE_DELIVERY);
//
//				} else {
//					logger.info(Constants.LOGGER_INFO_MESSAGE_DELIVERY_SUCCESSFUL);
//					return Constants.UPADTED_SUCCESSFULLY_MESSAGE;
//				}
//			} catch (SQLException sqlException) {
//				logger.error(sqlException.getMessage());
//				throw new UpdateException(Constants.UPDATE_EXCEPTION_MESSAGE_TECHNICAL_PROBLEM);
//			} finally {
//				try {
//					preparedStatement.close();
//					con.close();
//				} catch (SQLException sqlException) {
//					logger.error(sqlException.getMessage());
//					throw new UpdateException(Constants.UPDATE_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
//
//				}
//			}
//		}
//	}

	/*****************************************************************
	 * - Method Name: displayRawMaterialOrderDetails() - Input Parameters : - Throws
	 * : Exception - Creation Date : 25/09/2019 - Description : Returns list of all
	 * raw materials
	 *******************************************************************/
	public List<RawMaterialOrder> displayRawMaterialOrderDetails() throws Exception {
		List<RawMaterialOrder> rmoList1 = new ArrayList<RawMaterialOrder>();
		Connection con = DBUtil.getInstance().getConnection();
		PreparedStatement pst = null;
		int isFetched = 0;
		try {
			pst = con.prepareStatement(QueryMapper.DISPLAY_RAWMATERIAL_ORDER);
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				isFetched = 1;
				int index = 1;

				String orderId = Integer.valueOf(rs.getInt(index++)).toString();
				String name = rs.getString(index++);
				String rawMaterialId = rs.getString(index++);
				String supplierId = rs.getString(index++);
				double quantityValue = rs.getDouble(index++);
				String quantityUnit = rs.getString(index++);
				Date dateOfOrder = rs.getDate(index++);
				Date dateofDelivery = rs.getDate(index++);
				double pricePerUnit = rs.getDouble(index++);
				double totalPrice = rs.getDouble(index++);
				String deliveryStatus = rs.getString(index++);
				String warehouseId = rs.getString(index++);
				rmoList1.add(new RawMaterialOrder(orderId, name, rawMaterialId, supplierId, quantityValue, quantityUnit,
						dateOfOrder, dateofDelivery, pricePerUnit, totalPrice, deliveryStatus, warehouseId));
			}
			if (isFetched == 0) {
				logger.error(Constants.LOGGER_ERROR_FETCH_FAILED);
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_INALID_INPUT);

			} else {
				logger.info(Constants.LOGGER_INFO_DISPLAY_SUCCESSFUL);

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
		return rmoList1;

	}

	/*****************************************************************
	 * - Method Name: displayPendingRawMaterialOrderDetails() - Input Parameters : -
	 * Throws : Exception - Creation Date : 25/09/2019 - Description : Returns list
	 * of pending raw materials
	 *******************************************************************/
	public List<RawMaterialOrder> displayPendingRawMaterialOrderDetails() throws Exception {
		List<RawMaterialOrder> rmoList1 = new ArrayList<RawMaterialOrder>();

		Connection con = DBUtil.getInstance().getConnection();

		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement(QueryMapper.DISPLAY_PENDING_RAWMATERIAL_ORDER);
			ResultSet rs = pst.executeQuery();

			int isFetched = 0;
			while (rs.next()) {
				int index = 1;
				isFetched = 1;
				String orderId = Integer.valueOf(rs.getInt(index++)).toString();
				String name = rs.getString(index++);
				String rawMaterialId = rs.getString(index++);
				String supplierId = rs.getString(index++);
				double quantityValue = rs.getDouble(index++);
				String quantityUnit = rs.getString(index++);
				Date dateOfOrder = rs.getDate(index++);
				Date dateofDelivery = rs.getDate(index++);
				double pricePerUnit = rs.getDouble(index++);
				double totalPrice = rs.getDouble(index++);
				String deliveryStatus = rs.getString(index++);
				String warehouseId = rs.getString(index++);
				rmoList1.add(new RawMaterialOrder(orderId, name, rawMaterialId, supplierId, quantityValue, quantityUnit,
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
				con.close();
			} catch (SQLException sqlException) {
				logger.error(sqlException.getMessage());
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);

			}
		}
		return rmoList1;
	}

	/*****************************************************************
	 * - Method Name: displayReceivedRawMaterialOrderDetails() - Input Parameters :
	 * - Throws : Exception - Creation Date : 25/09/2019 - Description : Returns
	 * list of received raw materials
	 *******************************************************************/
	public List<RawMaterialOrder> displayReceivedRawMaterialOrderDetails() throws Exception {
		List<RawMaterialOrder> rmoList1 = new ArrayList<RawMaterialOrder>();
		Connection con = DBUtil.getInstance().getConnection();

		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement(QueryMapper.DISPLAY_RECEIVED_RAWMATERIAL_ORDER);
			ResultSet rs = pst.executeQuery();

			int isFetched = 0;
			while (rs.next()) {
				int index = 1;
				isFetched = 1;
				String orderId = Integer.valueOf(rs.getInt(index++)).toString();
				String name = rs.getString(index++);
				String rawMaterialId = rs.getString(index++);
				String supplierId = rs.getString(index++);
				double quantityValue = rs.getDouble(index++);
				String quantityUnit = rs.getString(index++);
				Date dateOfOrder = rs.getDate(index++);
				Date dateofDelivery = rs.getDate(index++);
				double pricePerUnit = rs.getDouble(index++);
				double totalPrice = rs.getDouble(index++);
				String deliveryStatus = rs.getString(index++);
				String warehouseId = rs.getString(index++);
				rmoList1.add(new RawMaterialOrder(orderId, name, rawMaterialId, supplierId, quantityValue, quantityUnit,
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
				con.close();
			} catch (SQLException sqlException) {
				logger.error(sqlException.getMessage());
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);

			}
		}
		return rmoList1;
	}

	/*****************************************************************
	 * - Method Name: displayCancelledRawMaterialOrderDetails() - Input Parameters :
	 * - Throws : Exception - Creation Date : 25/09/2019 - Description : Returns
	 * list of cancelled raw materials
	 *******************************************************************/
	public List<RawMaterialOrder> displayCancelledRawMaterialOrderDetails() throws Exception {
		List<RawMaterialOrder> rmoList1 = new ArrayList<RawMaterialOrder>();

		Connection con = DBUtil.getInstance().getConnection();

		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement(QueryMapper.DISPLAY_CANCELLED_RAWMATERIAL_ORDER);
			ResultSet rs = pst.executeQuery();

			int isFetched = 0;
			while (rs.next()) {
				int index = 1;
				isFetched = 1;
				String orderId = Integer.valueOf(rs.getInt(index++)).toString();
				String name = rs.getString(index++);
				String rawMaterialId = rs.getString(index++);
				String supplierId = rs.getString(index++);
				double quantityValue = rs.getDouble(index++);
				String quantityUnit = rs.getString(index++);
				Date dateOfOrder = rs.getDate(index++);
				Date dateofDelivery = rs.getDate(index++);
				double pricePerUnit = rs.getDouble(index++);
				double totalPrice = rs.getDouble(index++);
				String deliveryStatus = rs.getString(index++);
				String warehouseId = rs.getString(index++);
				rmoList1.add(new RawMaterialOrder(orderId, name, rawMaterialId, supplierId, quantityValue, quantityUnit,
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
				con.close();
			} catch (SQLException sqlException) {
				logger.error(sqlException.getMessage());
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);

			}
		}
		return rmoList1;
	}

	/*****************************************************************
	 * - Method Name: displayDispatchedRawMaterialOrderDetails() - Input Parameters
	 * : - Throws : Exception - Creation Date : 25/09/2019 - Description : Returns
	 * list of dispatched raw materials
	 *******************************************************************/

	@Override
	public List<RawMaterialOrder> displayDispatchedRawMaterialOrderDetails() throws Exception {
		List<RawMaterialOrder> rmoList1 = new ArrayList<RawMaterialOrder>();
		Connection con = DBUtil.getInstance().getConnection();
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement(QueryMapper.DISPLAY_DISPATCHED_RAWMATERIAL_ORDER);
			ResultSet rs = pst.executeQuery();
			int isFetched = 0;
			while (rs.next()) {
				int index = 1;
				isFetched = 1;

				String orderId = Integer.valueOf(rs.getInt(index++)).toString();
				String name = rs.getString(index++);
				String rawMaterialId = rs.getString(index++);
				String supplierId = rs.getString(index++);
				double quantityValue = rs.getDouble(index++);
				String quantityUnit = rs.getString(index++);
				Date dateOfOrder = rs.getDate(index++);
				Date dateofDelivery = rs.getDate(index++);
				double pricePerUnit = rs.getDouble(index++);
				double totalPrice = rs.getDouble(index++);
				String deliveryStatus = rs.getString(index++);
				String warehouseId = rs.getString(index++);
				rmoList1.add(new RawMaterialOrder(orderId, name, rawMaterialId, supplierId, quantityValue, quantityUnit,
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
				con.close();
			} catch (SQLException sqlException) {
				logger.error(sqlException.getMessage());
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);

			}
		}
		return rmoList1;

	}

	/*****************************************************************
	 * - Method Name: displayRawmaterialOrdersbetweenDetails() - Input Parameters :
	 * - Throws : Exception - Creation Date : 25/09/2019 - Description : Returns
	 * list of raw materials between two dates given by user
	 *******************************************************************/

	public List<RawMaterialOrder> displayRawmaterialOrdersbetweenDetails(java.util.Date dt1, java.util.Date dt2)
			throws Exception {
		List<RawMaterialOrder> rmoList1 = new ArrayList<RawMaterialOrder>();
		Connection con = DBUtil.getInstance().getConnection();
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement(QueryMapper.DISPLAY_RAWMATERIAL_ORDER_BW_DATES);
			pst.setDate(1, DBUtil.stringtoDate(dt1));
			pst.setDate(2, DBUtil.stringtoDate(dt2));
			ResultSet rs = pst.executeQuery();

			int isFetched = 0;
			while (rs.next()) {
				int index = 1;
				isFetched = 1;

				String orderId = Integer.valueOf(rs.getInt(index++)).toString();
				String name = rs.getString(index++);
				String rawMaterialId = rs.getString(index++);
				String supplierId = rs.getString(index++);
				double quantityValue = rs.getDouble(index++);
				String quantityUnit = rs.getString(index++);
				Date dateOfOrder = rs.getDate(index++);
				Date dateofDelivery = rs.getDate(index++);
				double pricePerUnit = rs.getDouble(index++);
				double totalPrice = rs.getDouble(index++);
				String deliveryStatus = rs.getString(index++);
				String warehouseId = rs.getString(index++);
				rmoList1.add(new RawMaterialOrder(orderId, name, rawMaterialId, supplierId, quantityValue, quantityUnit,
						dateOfOrder, dateofDelivery, pricePerUnit, totalPrice, deliveryStatus, warehouseId));

			}
			if (isFetched == 0) {
				logger.error(Constants.DISPLAY_EXCEPTION_NO_RECORDS_FOUND);
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
				con.close();
			} catch (SQLException sqlException) {
				logger.error(sqlException.getMessage());
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);

			}
		}
		return rmoList1;
	}

	/*****************************************************************
	 * - Method Name: displayOrdersFromSupplier - Input Parameters : - Throws :
	 * Exception - Creation Date : 25/09/2019 - Description : Returns list of raw
	 * materials by a particular supplier
	 *******************************************************************/
	public List<RawMaterialOrder> displayOrdersFromSupplier(String supid) throws Exception {

		List<RawMaterialOrder> rmoList1 = new ArrayList<RawMaterialOrder>();
		Connection con = DBUtil.getInstance().getConnection();
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement(QueryMapper.DISPLAY_RAWMATERIAL_ORDER_FROM_SUPPLIER);
			pst.setString(1, supid);
			ResultSet rs = pst.executeQuery();
			int isFetched = 0;
			while (rs.next()) {
				int index = 1;
				isFetched = 1;

				String orderId = Integer.valueOf(rs.getInt(index++)).toString();
				String name = rs.getString(index++);
				String rawMaterialId = rs.getString(index++);
				String supplierId = rs.getString(index++);
				double quantityValue = rs.getDouble(index++);
				String quantityUnit = rs.getString(index++);
				Date dateOfOrder = rs.getDate(index++);
				Date dateofDelivery = rs.getDate(index++);
				double pricePerUnit = rs.getDouble(index++);
				double totalPrice = rs.getDouble(index++);
				String deliveryStatus = rs.getString(index++);
				String warehouseId = rs.getString(index++);
				rmoList1.add(new RawMaterialOrder(orderId, name, rawMaterialId, supplierId, quantityValue, quantityUnit,
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
				con.close();
			} catch (SQLException sqlException) {
				logger.error(sqlException.getMessage());
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);

			}
		}
		return rmoList1;
	}

	/*******************************************************************************************************
	 * - Function Name : add raw material order - Input Parameters :RawmaterialOrder
	 * newRMO - Return Type : String - Throws : Exception - Author : Capgemini -
	 * Creation Date : 25/09/2019 - Description : Raw Material orders is placed i.e.
	 * entry is added in database @throws
	 * 
	 * @throws DisplayException
	 ********************************************************************************************************/

	@Override
	public boolean addRawMaterialOrder(RawMaterialOrder newRMO)
			throws RMOrderNotAddedException, ConnectionException, SQLException, DisplayException {

		boolean added = false;
		RawMaterialOrderEntity rawMaterialOrderEntity = new RawMaterialOrderEntity(newRMO.getName(), newRMO.getSupplierId(), newRMO.getQuantityValue(), newRMO.getQuantityUnit(), newRMO.getDateOfDelivery(), newRMO.getPricePerUnit(), newRMO.getWarehouseId());
		Session session=null;
		try {
			session = HibernateUtil.getASession();
			session.beginTransaction();
		    session.save(rawMaterialOrderEntity);
			session.getTransaction().commit();
			added = true;
		} catch (HibernateException e) {
			e.printStackTrace();
			System.out.println("Exception 638");
		}
//		finally {
//			session.close();
//		}
		if (!added) {
			throw new RMOrderNotAddedException(Constants.RM_ORDER_NOT_ADDED);
		}
		return added;
	
	}

	// ------------------------------------------------------------------------------------------------------------------------------------

	public Supplier fetchSupplierDetail(Supplier supplierDetails) throws BackEndException, DoesNotExistException {
		Connection connection;
		try {
			connection = DBUtil.getInstance().getConnection();
		} catch (Exception exception) {
			logger.error(Constants.SUPPLIER_LOGGER_ERROR_DATABASE_NOTCONNECTED + exception.getMessage());
			throw new BackEndException(Constants.SUPPLIER_LOGGER_ERROR_DATABASE_NOTCONNECTED + exception.getMessage());
		}
		ResultSet resultSet = null;
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(QueryMapper.SELECT_ONE_SUPPLIER_ID);
			preparedStatement.setString(1, supplierDetails.getSupplierId());
			resultSet = preparedStatement.executeQuery();
			int SupplierCounter = 0;
			while (resultSet.next()) {
				SupplierCounter++;

				supplierDetails.setName(resultSet.getString(2));
				supplierDetails.setPhoneNo(resultSet.getString(5));
				supplierDetails.setEmailId(resultSet.getString(4));
				supplierDetails.setAddress(resultSet.getString(3));

			}
			if (SupplierCounter == 0)
				throw new DoesNotExistException(Constants.SUPPLIER_ID_DOES_NOT_EXISTS_EXCEPTION);
		} catch (SQLException exception) {
			logger.error(Constants.SUPPLIER_LOGGER_ERROR_FETCHING_FAILED + exception.getMessage());
			throw new BackEndException(Constants.SUPPLIER_LOGGER_ERROR_FETCHING_FAILED + exception.getMessage());

		} finally {
			try {
				connection.close();
			} catch (SQLException exception) {
				logger.error(exception.getMessage());
				throw new BackEndException(exception.getMessage());
			}
		}
		return supplierDetails;
	}

	public boolean addSupplierAddress(Address newsupplieraddress) throws Exception {
		Connection connection;
		try {
			connection = DBUtil.getInstance().getConnection();
		} catch (Exception exception) {
			logger.error(Constants.DATABASE_NOT_CONNECTED + exception.getMessage());
			throw new BackEndException(Constants.DATABASE_NOT_CONNECTED + exception.getMessage());
		}
		boolean added = false;
		Connection con = DBUtil.getInstance().getConnection();
		PreparedStatement stmt = con.prepareStatement(QueryMapper.INSERT_ADDRESS);
		try {
			stmt = connection.prepareStatement(QueryMapper.INSERT_ONE_EMPLOYEE);

			int noOfRows = stmt.executeUpdate();

			stmt.close();
			con.close();
			if (noOfRows == 1)
				added = true;
			if (!added) {
				logger.error(Constants.ROW_NOT_ADDED_MESSAGE);
				throw new RowNotAddedException(Constants.ROW_NOT_ADDED_MESSAGE);
			}

			return added;
		} finally {

		}
	}

	public boolean doesSupplierAddressExist(String Address)
			throws SupplierAddressDoesNotExistsException, ConnectionException, SQLException {
		boolean supplieraddressFound = false;
		Connection con;
		try {

			con = DBUtil.getInstance().getConnection();
		} catch (Exception e) {

			logger.error(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
			throw new ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
		}

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			preparedStatement = con.prepareStatement(QueryMapper.SELECT_ALL_ADDRESS);
			preparedStatement.setString(1, Address);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				supplieraddressFound = true;
			}

			if (!supplieraddressFound) {
				logger.error(Constants.SUPPLIER_ADDRESS_EXCEPTION);
				throw new SupplierAddressDoesNotExistsException(Constants.SUPPLIER_ADDRESS_EXCEPTION);
			}
			return supplieraddressFound;
		} finally {
			resultSet.close();
			preparedStatement.close();
			con.close();
		}
	}


//	@Override
//	public List<RawMaterialOrderEntity> displayRawmaterialOrders(DisplayRawMaterialOrder displayRawMaterialOrderObject)
//			throws Exception {
//		String hql="";
//		Session session=null;
//		SessionFactory sessionFactory=null;
//		 List<RawMaterialOrderEntity > list = new ArrayList<RawMaterialOrderEntity>();
//		PreparedStatement pst = null;
//		int isFetched = 0;
//		
//			String deliveryStatus = displayRawMaterialOrderObject.getDeliveryStatus();
//			String generateQuery = "";
//			
//				if (deliveryStatus.equals("ALL"))
//					hql = "from RawMaterialOrderEntity where deliveryStatus in (select deliveryStatus from RawMaterialOrderEntity)";
//				else
//					hql = "from RawMaterialOrderEntity where deliveryStatus in ( '" + deliveryStatus + "')";
//
//		
//             String supplierId = displayRawMaterialOrderObject.getSupplierid();
//			
//				if (supplierId.equals("ALL"))
//					hql += " AND supplierId in (select supplierId from RawMaterialOrderEntity)";
//				else
//
//					hql += "  AND supplierId in ( '" + supplierId + "' )";
//			
//
//			String startDate = displayRawMaterialOrderObject.getStartdate();
//			String endDate = displayRawMaterialOrderObject.getEndDate();
//
//			if (startDate != null && endDate != null) {
//				
//	hql = " from RawMaterialOrderEntity where dateOfDelivery BETWEEN '" + startDate + "' AND '" + endDate + "' ";
//				}
//            System.out.println(hql);
//            System.out.println("dao");
//            
//			try {
//				System.out.println("hello");
//			    sessionFactory = HibernateUtil.getSessionFactory();
//				// session =sessionFactory.openSession();
//			    session =sessionFactory.getCurrentSession();
//				session.beginTransaction();
//				 Query q = session.createQuery(hql);
//				list =  q.list();
//				System.out.println(list);
//				 if (list.isEmpty()) {
//						logger.error(Constants.LOGGER_ERROR_FETCH_FAILED);
//						throw new DisplayException(Constants.DISPLAY_EXCEPTION_INALID_INPUT);
//
//					} 
//				 else {
//						logger.info(Constants.LOGGER_INFO_DISPLAY_SUCCESSFUL);
//
//					}
//			} catch (Exception e) {
//				
//				e.printStackTrace();
//			}
//	       
//			
//
//	       finally {
//		
//            session.close();
//			sessionFactory.close();
//		}
//		return list;
//
//	}


//	@Override
//	public List<RawMaterialOrder> displayRawmaterialOrders(DisplayRawMaterialOrder displayRawMaterialOrderObject)
//			throws Exception {
//		String hql="";
//		Session session=null;
//		SessionFactory sessionFactory=null;
//		 List<RawMaterialOrder> list = new ArrayList<RawMaterialOrder>();
//		PreparedStatement pst = null;
//		int isFetched = 0;
//		
//			String deliveryStatus = displayRawMaterialOrderObject.getDeliveryStatus();
//			String generateQuery = "";
//			
//				if (deliveryStatus.equals("ALL"))
//					hql = "from RawMaterialOrderEntity where deliveryStatus in (select deliveryStatus from RawMaterialOrderEntity)";
//				else
//					hql = "from RawMaterialOrderEntity where deliveryStatus in ( '" + deliveryStatus + "')";
//
//		
//             String supplierId = displayRawMaterialOrderObject.getSupplierid();
//			
//				if (supplierId.equals("ALL"))
//					hql += " AND supplierId in (select supplierId from RawMaterialOrderEntity)";
//				else
//
//					hql += "  AND supplierId in ( '" + supplierId + "' )";
//			
//
//			String startDate = displayRawMaterialOrderObject.getStartdate();
//			String endDate = displayRawMaterialOrderObject.getEndDate();
//
//			if (startDate != null && endDate != null) {
//				
//					hql += " AND  dateOfDelivery BETWEEN '" + startDate + "' AND '" + endDate + "'  ";
//				}
//            System.out.println(hql);
//            System.out.println("dao");
//            
//			try {
//				System.out.println("hello");
////			    sessionFactory = HibernateUtil.getSessionFactory();
//				// session =sessionFactory.openSession();
//			    session =sessionFactory.getCurrentSession();
//				session.beginTransaction();
//				 Query q = session.createQuery(hql);
//				list =  q.list();
//				System.out.println(list);
//				 if (list.isEmpty()) {
//						logger.error(Constants.LOGGER_ERROR_FETCH_FAILED);
//						throw new DisplayException(Constants.DISPLAY_EXCEPTION_INALID_INPUT);
//
//					} 
//				 else {
//						logger.info(Constants.LOGGER_INFO_DISPLAY_SUCCESSFUL);
//
//					}
//			} catch (Exception e) {
//				
//				e.printStackTrace();
//			}
//	       
//			
//
//	       finally {
//		
//            session.close();
//			sessionFactory.close();
//		}
//		return list;
//
//	}
//

	@SuppressWarnings("unused")
	@Override
	public List<RawMaterialOrderEntity> displayRawmaterialOrders(DisplayRawMaterialOrder displayRawMaterialOrderObject) throws DisplayException, BackEndException
			 {
		
		Session session=null;
	    Transaction tx = null;
	    Criteria cr = null;
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SessionFactory sessionFactory = null;
		 List<RawMaterialOrderEntity > list = new ArrayList<RawMaterialOrderEntity>();
		PreparedStatement pst = null;
		int isFetched = 0;
		
			
            
			try {
				System.out.println("hello");
			   
				
				 session = HibernateUtil.getASession();
				
			   
				tx = session.beginTransaction();
				String deliveryStatus = displayRawMaterialOrderObject.getDeliveryStatus();
				
				
				CriteriaBuilder builder = session.getCriteriaBuilder();
				CriteriaQuery<RawMaterialOrderEntity> criteria = builder.createQuery(RawMaterialOrderEntity.class);
				Root<RawMaterialOrderEntity> root = criteria.from(RawMaterialOrderEntity.class);

					if (deliveryStatus.equals("ALL")) {
						
						;
	            }	
					else {

			       criteria.select(root).where(builder.equal(root.get("deliveryStatus"),deliveryStatus));
             
					}
	             String supplierId = displayRawMaterialOrderObject.getSupplierid();
				
					if (supplierId.equals("ALL"))
						;
					else
						 criteria.select(root).where(builder.equal(root.get("supplierId"),supplierId));
						
				

				String startDate = displayRawMaterialOrderObject.getStartdate();
				String endDate = displayRawMaterialOrderObject.getEndDate();

				if (startDate != null && endDate != null) {
					criteria.select(root).where(builder.between(root.get("dateOfDelivery"),sdf.parse(startDate),sdf.parse(endDate)));
				
					}
     
				 Query<RawMaterialOrderEntity> q=session.createQuery(criteria);
				 list = q.list();

				 if (list.isEmpty()) {
						logger.error(Constants.LOGGER_ERROR_FETCH_FAILED);
						throw new DisplayException(Constants.DISPLAY_EXCEPTION_NO_RECORDS_FOUND);

					} 
				 else {
						logger.info(Constants.LOGGER_INFO_DISPLAY_SUCCESSFUL);

					}
			} catch (Exception e) {
				
				e.printStackTrace();
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_NO_RECORDS_FOUND);
			}
	       
			

	       finally {
		

          HibernateUtil.closeSession(session);
		//sessionFactory.close();
		}
		return list;

	}



	//sql based getRawMaterialNamesppp
//	@Override
//	public ArrayList<String> getRawMaterialNames() throws DisplayException, ConnectionException {
//
//		ArrayList<String> rawMaterialNamesList = new ArrayList<String>();
//		Connection connection;
//		try {
//			connection = DBUtil.getInstance().getConnection();
//		} catch (Exception e) {
//			logger.error(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
//			throw new ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
//		}
//		PreparedStatement pst = null;
//
//		try {
//			pst = connection.prepareStatement(QueryMapper.FETCH_RAWMATERIAL_NAMES);
//			ResultSet rs = pst.executeQuery();
//
//			int isFetched = 0;
//			while (rs.next()) {
//				isFetched = 1;
//				String rawMaterialName = rs.getString(1);
//				rawMaterialNamesList.add(rawMaterialName);
//			}
//
//			if (isFetched == 0) {
//				logger.error(Constants.LOGGER_ERROR_FETCH_FAILED);
//				throw new DisplayException(Constants.DISPLAY_EXCEPTION_FETCH_FAILED);
//
//			} else {
//				logger.info(Constants.LOGGER_INFO_DISPLAY_SUCCESSFUL);
//
//			}
//
//		} catch (SQLException sqlException) {
//			logger.error(sqlException.getMessage());
//			throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_TECHNICAL_PROBLEM);
//		} finally {
//			try {
//
//				pst.close();
//				connection.close();
//			} catch (SQLException sqlException) {
//				logger.error(sqlException.getMessage());
//				throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
//
//			}
//		}
//		return rawMaterialNamesList;
//	}
	
	@Override
	public ArrayList<String> getRawMaterialNames() throws DisplayException, ConnectionException {

		ArrayList<String> rawMaterialNamesList = new ArrayList<String>();
		Connection connection;
		try {
			connection = DBUtil.getInstance().getConnection();
		} catch (Exception e) {
			logger.error(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
			throw new ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
		}
		PreparedStatement pst = null;

		try {
			pst = connection.prepareStatement(QueryMapper.FETCH_RAWMATERIAL_NAMES);
			ResultSet rs = pst.executeQuery();

			int isFetched = 0;
			while (rs.next()) {
				isFetched = 1;
				String rawMaterialName = rs.getString(1);
				rawMaterialNamesList.add(rawMaterialName);
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
		return rawMaterialNamesList;
	}

	@Override
	public ArrayList<String> getDistributorIds() throws DisplayException, ConnectionException {

		ArrayList<String> supplierIdsList = new ArrayList<String>();
		Connection connection;
		try {
			connection = DBUtil.getInstance().getConnection();
		} catch (Exception e) {
			logger.error(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
			throw new ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
		}
		PreparedStatement pst = null;

		try {
			pst = connection.prepareStatement(QueryMapper.FETCH_SUPPLIER_IDS);
			ResultSet rs = pst.executeQuery();

			int isFetched = 0;
			while (rs.next()) {
				isFetched = 1;
				String supplierId = rs.getString(1);
				supplierIdsList.add(supplierId);
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
		return supplierIdsList;
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
	public String trackRawMaterialOrder(RawMaterialStock rawMaterialStock) {
		

        Session session = sessionFactory.openSession();
        session.beginTransaction();
		
		System.out.println(rawMaterialStock.getOrderId());
		try {
        RawMaterialStockEntity rmStockEntity = session.load(RawMaterialStockEntity.class, Integer.parseInt(rawMaterialStock.getOrderId()));
//	     System.out.println(rmStockEntity);
      
//	      session.getTransaction().commit();
	      
	      				Date processDate = rmStockEntity.getProcessDate();
	      
	      				Date deliveryDate = rmStockEntity.getDateofDelivery();
	      
	      				String warehouseId = rmStockEntity.getWarehouseId();
	      
	      			if(processDate == null || deliveryDate == null) {
	      				return "Data Incomplete...Please check database and update required information";
	      			}
	      
	      			String message = "The order ID had been in the warehouse with warehouseID = " + warehouseId + " from "
	      					+ deliveryDate.toString() + " to " + processDate.toString() + "("
	      					+ DBUtil.diffBetweenDays(processDate, deliveryDate) + " days)";
	      			session.close();
	      			return message;
	      			
	}
	
	catch(ObjectNotFoundException exception) {
		session.close();
		return Constants.INCOMPLETE_INFORMATION_IN_DATABASE;
	}
	    
	}

	@Override
	public boolean doesRawMaterialOrderIdExist(String orderId) throws RMOrderIDDoesNotExistException {
		boolean rmOrderIdFound = false;
		int oid = -1;
		try {
			oid = Integer.parseInt(orderId);
		} catch (Exception e) {
//			logger.error(e.getMessage());
			return rmOrderIdFound;
		}
		
		
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		@SuppressWarnings("rawtypes")
		Query query = session.createQuery("from RawMaterialOrderEntity where orderId = :oId");
		query.setParameter("oId", oid);
		if (query.getResultList().size() == 1) {
			rmOrderIdFound = true;
//			session.getTransaction().commit();
			session.close();
			return rmOrderIdFound;
		} else {
//			logger.error(Constants.RAWMATERIAL_ID_DOES_NOT_EXISTS_EXCEPTION);
			session.close();
			throw new RMOrderIDDoesNotExistException(Constants.RAWMATERIAL_ID_DOES_NOT_EXISTS_EXCEPTION);
		}

	}

	@Override
	public boolean processDateCheck(RawMaterialStock rawMaterialStock) throws ProcessDateException, IncompleteDataException {
		
		Session session = null;
		
		try {
			boolean datecheck = false;
			session = sessionFactory.openSession(); 
	        session.beginTransaction();

	        try {
	        RawMaterialStockEntity rmStockEntity = session.load(RawMaterialStockEntity.class, Integer.parseInt(rawMaterialStock.getOrderId()));
		    
//	        session.getTransaction().commit();
	        
		      Date manufacturingDate = rmStockEntity.getManufacturingDate();
		      
		      Date expiryDate = rmStockEntity.getExpiryDate();
		      
		      				if (rawMaterialStock.getProcessDate().after(manufacturingDate)
		      						&& rawMaterialStock.getProcessDate().before(expiryDate)) {
		      					datecheck = true;
		      					return datecheck;
		      				}
		      
		      				else
		      					throw new ProcessDateException(Constants.PROCESS_DATE_EXCEPTION_MESSAGE);
	        
		}
		catch(ObjectNotFoundException exception) {
			session.close();
			throw new IncompleteDataException(Constants.INCOMPLETE_INFORMATION_IN_DATABASE);
		}
		      			
		      
		      		} 
		      
		      		catch (ProcessDateException exception) {
//		      			logger.error(Constants.PROCESS_DATE_EXCEPTION_MESSAGE);
		      			throw exception;
		      
		      		}
		finally {
			session.close();
		}
		  
			

		
	}

	@Override
	public String updateProcessDateinStock(RawMaterialStock rawMaterialStock) {

		Session session = sessionFactory.openSession(); 
        session.beginTransaction();
        RawMaterialStockEntity rmStockEntity = session.load(RawMaterialStockEntity.class, Integer.parseInt(rawMaterialStock.getOrderId()));
//        String hql = "update RawMaterialStockEntity set processDate = :processDateVariable where orderId = :oId";
//        Query q = session.createQuery(hql);
//	      q.setParameter("oId", Integer.parseInt(rawMaterialStock.getOrderId()));
//	      q.setParameter("processDateVariable", rawMaterialStock.getProcessDate());
        
	      rmStockEntity.setProcessDate(rawMaterialStock.getProcessDate());
	      session.save(rmStockEntity);
	      
//	      int result = q.executeUpdate();
//	      System.out.println(result);
	      
	      session.getTransaction().commit();
	      if (session.getTransaction() != null && session.getTransaction().isActive()) {
				 session.getTransaction().rollback();
			}
	      session.close();
	      return Constants.DATA_INSERTED_MESSAGE;

	}

	@Override
	public String updateRawMaterialStock(RawMaterialStock rawMaterialStock) {
		
		Session session = sessionFactory.openSession(); 
        session.beginTransaction();
        
        boolean orderIdcheckInStock = false;
        System.out.println("1");
		orderIdcheckInStock = doesRawMaterialOrderIdExistInStock(rawMaterialStock.getOrderId());
		System.out.println("2");
		if (orderIdcheckInStock == false) {
			System.out.println("3");
			String hql = "insert into RawMaterialStockEntity(orderId, name, pricePerUnit, quantityValue, quantityUnit, totalPrice, warehouseId, dateofDelivery)" +  " select orderId, name, pricePerUnit, quantityValue, quantityUnit, totalPrice, warehouseId, dateOfDelivery from RawMaterialOrderEntity where orderId = :oId";
			@SuppressWarnings("rawtypes")
			Query q = session.createQuery(hql);
		      q.setParameter("oId", Integer.parseInt(rawMaterialStock.getOrderId()));
			
			int result = q.executeUpdate();
			System.out.println(result + ":");
		}
		System.out.println("4");
//		String hql = "update RawMaterialStockEntity set manufacturingDate = :manDate, expiryDate = :expDate, qualityCheck = :qaCheck where orderID = :oId";
//		Query q1 = session.createQuery(hql);
//	      q1.setParameter("oId", Integer.parseInt(rawMaterialStock.getOrderId()));
//	      q1.setParameter("manDate", rawMaterialStock.getManufacturingDate());
//	      q1.setParameter("expDate", rawMaterialStock.getExpiryDate());
//	      q1.setParameter("qaCheck", rawMaterialStock.getQualityCheck());
//	      
//	      int result = q1.executeUpdate();
//			System.out.println(result);
		
		RawMaterialStockEntity rmStockEntity = session.load(RawMaterialStockEntity.class, Integer.parseInt(rawMaterialStock.getOrderId()));
		
		rmStockEntity.setManufacturingDate(rawMaterialStock.getManufacturingDate());
		rmStockEntity.setExpiryDate(rawMaterialStock.getExpiryDate());
		rmStockEntity.setQualityCheck(rawMaterialStock.getQualityCheck());
		
		session.save(rmStockEntity);
		
		
			System.out.println("5");
			session.getTransaction().commit();
			if (session.getTransaction() != null && session.getTransaction().isActive()) {
				 session.getTransaction().rollback();
			}
			session.close();
		    return Constants.DATA_INSERTED_MESSAGE;
       
       

	}

	@Override
	public boolean doesRawMaterialOrderIdExistInStock(String orderId) {
		
		boolean rmOrderIdFound = false;
		int oid = -1;
		try {
			oid = Integer.parseInt(orderId);
		} catch (Exception e) {
//			logger.error(e.getMessage());
			return rmOrderIdFound;
		}
		
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		@SuppressWarnings("rawtypes")
		Query query = session.createQuery("from RawMaterialStockEntity where orderId = :oId");
		query.setParameter("oId", oid);
		if (query.getResultList().size() == 1) {
			rmOrderIdFound = true;
//			session.getTransaction().commit();
			session.close();
			return rmOrderIdFound;
		} else {
			logger.error(Constants.RAWMATERIAL_ID_DOES_NOT_EXIST_IN_STOCK_EXCEPTION);
			session.close();
			return rmOrderIdFound;
		}	 

	}
	
	public static void main(String[] args) throws BackEndException, RowNotFoundException {
		
		RawMaterialDAO ed = new RawMaterialDAOImpl();
		System.out.println(ed.doesRawMaterialOrderIdExistInStock("1"));
	}
	

}
