package com.capgemini.dnd.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.capgemini.dnd.customexceptions.BackEndException;
import com.capgemini.dnd.customexceptions.ConnectionException;
import com.capgemini.dnd.customexceptions.DisplayException;
import com.capgemini.dnd.customexceptions.DoesNotExistException;
import com.capgemini.dnd.customexceptions.ProcessDateException;
import com.capgemini.dnd.customexceptions.RMIDDoesNotExistException;
import com.capgemini.dnd.customexceptions.RMNameDoesNotExistException;
import com.capgemini.dnd.customexceptions.RMOrderIDDoesNotExistException;
import com.capgemini.dnd.customexceptions.RMOrderNotAddedException;
import com.capgemini.dnd.customexceptions.RowNotAddedException;
import com.capgemini.dnd.customexceptions.RowNotFoundException;
import com.capgemini.dnd.customexceptions.SupplierAddressDoesNotExistsException;
import com.capgemini.dnd.customexceptions.SupplierIDDoesNotExistException;
import com.capgemini.dnd.customexceptions.UpdateException;
import com.capgemini.dnd.customexceptions.WIdDoesNotExistException;
import com.capgemini.dnd.dto.Address;
import com.capgemini.dnd.dto.DisplayRawMaterialOrder;
import com.capgemini.dnd.dto.Employee;
import com.capgemini.dnd.dto.RawMaterialOrder;
import com.capgemini.dnd.dto.RawMaterialStock;
import com.capgemini.dnd.dto.Supplier;
import com.capgemini.dnd.service.RawMaterialService;
import com.capgemini.dnd.service.RawMaterialServiceImpl;
import com.capgemini.dnd.util.DBUtil;

public class RawMaterialDAOImpl implements RawMaterialDAO {

	Logger logger = Logger.getRootLogger();

	public RawMaterialDAOImpl() {

	}

	public String updateStatusRawMaterialOrder(String oid, String newStatus) throws Exception {
		Connection con = DBUtil.getInstance().getConnection();
		PreparedStatement preparedStatement = null;
		int queryResult = 0;
		java.util.Date today_date = new Date();
		if (newStatus.equalsIgnoreCase("RECEIVED")) {
			try {
				preparedStatement = con.prepareStatement(QueryMapper.UPDATE_RM_DELIVERY_STATUS);
				preparedStatement.setString(1, newStatus);
				preparedStatement.setDate(2, DBUtil.stringtoDate(today_date));
				preparedStatement.setInt(3, Integer.parseInt(oid));
				queryResult = preparedStatement.executeUpdate();
				if (queryResult == 0) {
					logger.error(Constants.LOGGER_ERROR_MESSAGE_FAILED_UPDATION);
					throw new UpdateException(Constants.UPDATE_EXCEPTION_MESSAGE_FAILURE_DELIVERY);

				} else {
					logger.info(Constants.LOGGER_INFO_MESSAGE_DELIVERY_SUCCESSFUL);
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
				preparedStatement = con.prepareStatement(QueryMapper.UPDATE_RM_DELIVERY_STATUS1);

				preparedStatement.setString(1, newStatus);
				preparedStatement.setInt(2, Integer.parseInt(oid));

				queryResult = preparedStatement.executeUpdate();
				if (queryResult == 0) {
					logger.error(Constants.LOGGER_ERROR_MESSAGE_FAILED_UPDATION);
					throw new UpdateException(Constants.UPDATE_EXCEPTION_MESSAGE_FAILURE_DELIVERY);

				} else {
					logger.info(Constants.LOGGER_INFO_MESSAGE_DELIVERY_SUCCESSFUL);
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
		}
	}

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

		Connection con;
		try {
			con = DBUtil.getInstance().getConnection();
		} catch (Exception e) {
			logger.error(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
			throw new ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
		}

		PreparedStatement preparedStatement = null, preparedStatement1 = null;
		boolean added = false;
		String rmId = null;
		try {
			preparedStatement1 = con.prepareStatement(QueryMapper.FETCH_RMID_FROM_RMNAME);
			preparedStatement1.setString(1, newRMO.getName().toUpperCase());
			ResultSet rs = preparedStatement1.executeQuery();
			while (rs.next()) {
				rmId = rs.getString(1);
				System.out.println(rmId);
			}
		} catch (SQLException sqlException) {
			logger.error(sqlException.getMessage());
			throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_TECHNICAL_PROBLEM);
		}

		try {

			preparedStatement = con.prepareStatement(QueryMapper.ADDRMORDER);
			preparedStatement.setString(1, newRMO.getName().toUpperCase());
			preparedStatement.setString(2, rmId.toUpperCase());
			preparedStatement.setString(3, newRMO.getSupplierId().toUpperCase());
			preparedStatement.setDouble(4, newRMO.getQuantityValue());
			preparedStatement.setString(5, newRMO.getQuantityUnit().toLowerCase());
			preparedStatement.setDate(6, DBUtil.stringtoDate(newRMO.getDateOfOrder()));
			preparedStatement.setDate(7, DBUtil.stringtoDate(newRMO.getDateOfDelivery()));
			preparedStatement.setDouble(8, newRMO.getPricePerUnit());
			preparedStatement.setDouble(9, newRMO.getTotalPrice());
			preparedStatement.setString(10, newRMO.getDeliveryStatus().toUpperCase());
			preparedStatement.setString(11, newRMO.getWarehouseId().toLowerCase());

			int noOfRows = preparedStatement.executeUpdate();

			con.close();

			if (noOfRows == 1) {
				added = true;
			}

			if (!added) {
				throw new RMOrderNotAddedException(Constants.RM_ORDER_NOT_ADDED);
			}
			return added;
		} catch (RMOrderNotAddedException | SQLException exception) {
			logger.error(Constants.RM_ORDER_NOT_ADDED);
			throw exception;
		} finally {
			con.close();
		}
	}

	public boolean doesRawMaterialOrderIdExist(String orderId)
			throws RMOrderIDDoesNotExistException, ConnectionException, SQLException {
		boolean rmIdFound = false;
		Connection con;
		try {

			con = DBUtil.getInstance().getConnection();
		} catch (Exception e) {

			logger.error(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
			throw new ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
		}

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		preparedStatement = con.prepareStatement(QueryMapper.SELECT_ALL_RM_ORDER);
		preparedStatement.setString(1, orderId);
		resultSet = preparedStatement.executeQuery();

		while (resultSet.next()) {
			rmIdFound = true;
			break;
		}

		if (!rmIdFound) {
			logger.error(Constants.RAWMATERIAL_ID_DOES_NOT_EXISTS_EXCEPTION);
			throw new RMOrderIDDoesNotExistException(Constants.RAWMATERIAL_ID_DOES_NOT_EXISTS_EXCEPTION);
		}

		con.close();
		return rmIdFound;
	}

	@Override
	public boolean doesRawMaterialIdExist(String rmId, String name)
			throws RMIDDoesNotExistException, ConnectionException, SQLException {
		boolean rmIdFound = false;
		Connection con;
		try {

			con = DBUtil.getInstance().getConnection();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
		}

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		preparedStatement = con.prepareStatement(QueryMapper.SELECT_RMSID_ORDER);
		preparedStatement.setString(1, name.toUpperCase());
		resultSet = preparedStatement.executeQuery();

		while (resultSet.next()) {
			String rmsId = resultSet.getString(1);
			if (rmsId.equalsIgnoreCase(rmId)) {
				rmIdFound = true;
				break;
			}
		}

		con.close();
		if (rmIdFound) {
			return rmIdFound;
		}
		if (!rmIdFound) {
			logger.error(Constants.RAWMATERIAL_ID_DOES_NOT_EXISTS_EXCEPTION);
			throw new RMIDDoesNotExistException(Constants.RAWMATERIAL_ID_DOES_NOT_EXISTS_EXCEPTION);
		}

		return rmIdFound;
	}

	@Override
	public boolean doesRawMaterialOrderIdExistInStock(String orderId)
			throws RMOrderIDDoesNotExistException, ConnectionException, SQLException {

		boolean rmOrderIdFound = false;
		int oid = -1;
		try {
			oid = Integer.parseInt(orderId);
		} catch (Exception e) {

		}
		Connection con;
		try {

			con = DBUtil.getInstance().getConnection();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
		}

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		preparedStatement = con.prepareStatement(QueryMapper.SELECT_RM_STOCK);
		preparedStatement.setInt(1, oid);
		resultSet = preparedStatement.executeQuery();

		while (resultSet.next()) {
			int rmsId = resultSet.getInt(1);
			if (rmsId == oid) {
				rmOrderIdFound = true;
				break;
			}
		}

		con.close();
		if (rmOrderIdFound) {
			return rmOrderIdFound;
		}
		if (!rmOrderIdFound) {
			logger.error(Constants.RAWMATERIAL_ID_DOES_NOT_EXIST_IN_STOCK_EXCEPTION);
			return false;
			// throw new
			// RMOrderIDDoesNotExistException(Constants.RAWMATERIAL_ID_DOES_NOT_EXIST_IN_STOCK_EXCEPTION);
		}

		return rmOrderIdFound;

	}

	public boolean doesSupplierIdExist(String suppId)
			throws SupplierIDDoesNotExistException, ConnectionException, SQLException {

		boolean suppIdFound = false;
		Connection connection;
		try {

			connection = DBUtil.getInstance().getConnection();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
		}

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		preparedStatement = connection.prepareStatement(QueryMapper.CHECK_IF_SUPPLIERID_EXIST);
		preparedStatement.setString(1, suppId);
		resultSet = preparedStatement.executeQuery();

		while (resultSet.next()) {
			suppIdFound = true;
			break;
		}

		if (!suppIdFound) {
			logger.error(Constants.SUPPLIER_ID_DOES_NOT_EXISTS_EXCEPTION);
			throw new SupplierIDDoesNotExistException(Constants.SUPPLIER_ID_DOES_NOT_EXISTS_EXCEPTION);
		}

		connection.close();
		return suppIdFound;
	}

	public boolean doesRMNameExist(String name) throws RMNameDoesNotExistException, ConnectionException, SQLException {
		boolean rmNameFound = false;
		Connection connection;
		try {

			connection = DBUtil.getInstance().getConnection();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
		}

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		preparedStatement = connection.prepareStatement(QueryMapper.CHECK_IF_RNAMEID_EXIST);
		preparedStatement.setString(1, name);
		resultSet = preparedStatement.executeQuery();

		while (resultSet.next()) {
			rmNameFound = true;
			break;
		}

		if (!rmNameFound) {
			logger.error(Constants.RMNAME_DOES_NOT_EXISTS_EXCEPTION);
			throw new RMNameDoesNotExistException(Constants.RMNAME_DOES_NOT_EXISTS_EXCEPTION);
		}

		connection.close();
		return rmNameFound;
	}

	public boolean doesWIdExist(String wId) throws WIdDoesNotExistException, ConnectionException, SQLException {

		boolean wIdFound = false;
		Connection connection;
		try {

			connection = DBUtil.getInstance().getConnection();
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
		}

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		preparedStatement = connection.prepareStatement(QueryMapper.CHECK_IF_WID_EXIST);
		preparedStatement.setString(1, wId);
		resultSet = preparedStatement.executeQuery();

		while (resultSet.next()) {
			wIdFound = true;
			break;
		}

		if (!wIdFound) {
			logger.error(Constants.WID_DOES_NOT_EXISTS_EXCEPTION);
			throw new WIdDoesNotExistException(Constants.WID_DOES_NOT_EXISTS_EXCEPTION);
		}

		connection.close();
		return wIdFound;
	}

	/*******************************************************************************************************
	 * - Function Name : update raw material stock - Input Parameters : String
	 * OrderId, Date manufacturing Date, process Date, string quality status -
	 * Return Type : Void - Throws : SQL Exception, Connection Exception - Author :
	 * CAPGEMINI - Creation Date : 25/09/2019 - Description : updating manufacturing
	 * date, process date and quality status into raw material stock table.
	 ********************************************************************************************************/
	@Override
	public String updateRMStock(RawMaterialStock rawMaterialStock) throws SQLException, ConnectionException {
		Connection connection = null;
		boolean rmOrderinStock = false;
		PreparedStatement statement1 = null;
		ResultSet resultSet = null;
		PreparedStatement statement2 = null;
		PreparedStatement statement = null;
		try {

			connection = DBUtil.getInstance().getConnection();

			rmOrderinStock = doesRawMaterialOrderIdExistInStock(rawMaterialStock.getOrderId());
			if (rmOrderinStock == false) {

				statement1 = connection
						.prepareStatement(QueryMapper.RETRIEVERMORDERDETAILSFORRMSTOCK);
				statement1.setInt(1, Integer.parseInt(rawMaterialStock.getOrderId()));
				resultSet = statement1.executeQuery();
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

				statement2 = connection.prepareStatement(QueryMapper.INSERTRMSTOCK);
				statement2.setInt(1, Integer.parseInt(rawMaterialStock.getOrderId()));
				statement2.setString(2, name);
				statement2.setDouble(3, priceperunit);
				statement2.setDouble(4, quantityValue);
				statement2.setString(5, quantityUnit);
				statement2.setDouble(6, totalprice);
				statement2.setString(7, warehouseId);
				statement2.setDate(8, DBUtil.stringtoDate(dateofdelivery));

				statement2.executeUpdate();
				resultSet.close();
				statement1.close();
				statement2.close();
			}

			statement = connection.prepareStatement(QueryMapper.UPDATERMSTOCK);
			statement.setDate(1, DBUtil.stringtoDate(rawMaterialStock.getManufacturingDate()));
			statement.setDate(2, DBUtil.stringtoDate(rawMaterialStock.getExpiryDate()));
			statement.setString(3, rawMaterialStock.getQualityCheck());
			statement.setInt(4, Integer.parseInt(rawMaterialStock.getOrderId()));
			statement.executeUpdate();

			
			return Constants.DATA_INSERTED_MESSAGE;

		}

		catch (SQLException exception) {
			logger.error(Constants.LOGGER_ERROR_MESSAGE_QUERY_NOT_EXECUTED);
			throw new SQLException(Constants.LOGGER_ERROR_MESSAGE_QUERY_NOT_EXECUTED);

		}

		catch (Exception exception) {
			logger.error(Constants.LOGGER_ERROR_MESSAGE_DATABASE_NOT_CONNECTED);
			throw new ConnectionException(Constants.LOGGER_ERROR_MESSAGE_DATABASE_NOT_CONNECTED);
		}
		
		finally {
			
			
			statement.close();
			
			connection.close();
		}

	}

	/*******************************************************************************************************
	 * - Function Name : process date check - Input Parameters : String orderId,
	 * Date process date - Return Type : boolean - Throws : SQLException,
	 * ConnectionException, ProcessDateException - Author : CAPGEMINI - Creation
	 * Date : 25/09/2019 - Description : checking that process_date should be after
	 * delivery_date and before expiry_date.
	 ********************************************************************************************************/

	@Override
	public boolean processDateCheck(RawMaterialStock rawMaterialStock)
			throws SQLException, ConnectionException, ProcessDateException {
		Connection connection = null;
		boolean datecheck = false;

		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			try {
				connection = DBUtil.getInstance().getConnection();
			} catch (Exception e) {
				throw new ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
			}
			statement = connection.prepareStatement(QueryMapper.CHECKPROCESSDATE);
			statement.setInt(1, Integer.parseInt(rawMaterialStock.getOrderId()));

			resultSet = statement.executeQuery();

			java.sql.Date deliveryDate = null;
			java.sql.Date expiryDate = null;

			while (resultSet.next()) {

				deliveryDate = resultSet.getDate(1);

				expiryDate = resultSet.getDate(2);

				if (rawMaterialStock.getProcessDate().after(deliveryDate)
						&& rawMaterialStock.getProcessDate().before(expiryDate)) {
					datecheck = true;
					return datecheck;
				}

				else
					throw new ProcessDateException(Constants.PROCESS_DATE_EXCEPTION_MESSAGE);

			}

		} catch (SQLException exception) {
			logger.error(Constants.LOGGER_ERROR_MESSAGE_QUERY_NOT_EXECUTED);
			throw new SQLException(Constants.LOGGER_ERROR_MESSAGE_QUERY_NOT_EXECUTED);

		}

		catch (ProcessDateException exception) {
			logger.error(Constants.PROCESS_DATE_EXCEPTION_MESSAGE);
			throw exception;

		}
		
		finally {
			resultSet.close();
			statement.close();
			connection.close();
		}

		return datecheck;

	}

	/*******************************************************************************************************
	 * - Function Name : update process_date in Stock - Input Parameters : String
	 * orderId, Date Process_date - Return Type : void - Throws : No Exception -
	 * Author : CAPGEMINI - Creation Date : 25/09/2019 - Description : updating
	 * process date for an orderId in the Raw Material Stock table.
	 ********************************************************************************************************/

	@Override
	public String updateProcessDateinStock(RawMaterialStock rawMaterialStock) {
		Connection connection = null;
		PreparedStatement statement = null;
		try {

			connection = DBUtil.getInstance().getConnection();

			statement  = connection.prepareStatement(QueryMapper.UPDATEPROCESSDATE);
			statement.setDate(1, DBUtil.stringtoDate(rawMaterialStock.getProcessDate()));
			statement.setInt(2, Integer.parseInt(rawMaterialStock.getOrderId()));
			statement.executeUpdate();

			
			return Constants.DATA_INSERTED_MESSAGE;

		}

		catch (SQLException exception) {
			logger.info(Constants.LOGGER_ERROR_MESSAGE_QUERY_NOT_EXECUTED);
			return Constants.LOGGER_ERROR_MESSAGE_QUERY_NOT_EXECUTED;

		} catch (Exception exception) {
			logger.info(Constants.LOGGER_ERROR_MESSAGE_DATABASE_NOT_CONNECTED);
			return Constants.LOGGER_ERROR_MESSAGE_DATABASE_NOT_CONNECTED;
		}
		finally {
			try {
			statement.close();
			connection.close();
			}
			catch(SQLException exception) {
				logger.error(exception.getMessage());
			}
		}

	}

	/*******************************************************************************************************
	 * - Function Name : Track raw material order - Input Parameters : String
	 * orderId - Return Type : String - Throws : No Exception - Author : CAPGEMINI -
	 * Creation Date : 23/09/2019 - Description : Raw Material order is tracked in
	 * the warehouse along with its shelf life
	 ********************************************************************************************************/

	@Override
	public String trackRawMaterialOrder(RawMaterialStock rawMaterialStock) {
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement statement = null;
		try {

			connection = DBUtil.getInstance().getConnection();

			statement = connection.prepareStatement(QueryMapper.TRACKRMORDER);
			statement.setInt(1, Integer.parseInt(rawMaterialStock.getOrderId()));
			resultSet = statement.executeQuery();

			String warehouseId = null;
			java.sql.Date processDate = null;
			java.sql.Date deliveryDate = null;

			while (resultSet.next()) {

				processDate = resultSet.getDate(1);

				deliveryDate = resultSet.getDate(2);

				warehouseId = resultSet.getString(3);

			}
			

			String message = "The order ID had been in the warehouse with warehouseID = " + warehouseId + " from "
					+ deliveryDate.toString() + " to " + processDate.toString() + "("
					+ DBUtil.diffBetweenDays(processDate, deliveryDate) + " days)";

			return message;

		} catch (SQLException exception) {
			logger.error(Constants.LOGGER_ERROR_MESSAGE_QUERY_NOT_EXECUTED);
			return Constants.LOGGER_ERROR_MESSAGE_QUERY_NOT_EXECUTED;
		} catch (Exception exception) {
			logger.error(Constants.LOGGER_ERROR_MESSAGE_DATABASE_NOT_CONNECTED);
			return Constants.LOGGER_ERROR_MESSAGE_DATABASE_NOT_CONNECTED;
		} finally {
			try {
				
				resultSet.close();
				statement.close();
				connection.close();
			} catch (SQLException exception) {
				logger.error(exception.getMessage());
			}
		}
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

				supplierDetails.setName(resultSet.getString(4));
				supplierDetails.setPhoneNo(resultSet.getString(5));
				supplierDetails.setEmailId(resultSet.getString(3));
				supplierDetails.setAddress(resultSet.getString(2));
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

	@Override
	public List<RawMaterialOrder> displayRawmaterialOrders(DisplayRawMaterialOrder displayRawMaterialOrderObject) throws Exception {
		List<RawMaterialOrder> rmoList1 = new ArrayList<RawMaterialOrder>();
		Connection con = DBUtil.getInstance().getConnection();
		System.out.println("in dao");
		PreparedStatement pst = null;
		int isFetched = 0;
		try {
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String DeliveryStatus = displayRawMaterialOrderObject.getDeliveryStatus();
	        String generateQuery= "";
				{	
					if(DeliveryStatus.equals("ALL"))
					 generateQuery = "select * from RawmaterialOrders where DeliveryStatus in "  + "( select DeliveryStatus from RawmaterialOrders )";
				else
					 generateQuery = "select * from RawmaterialOrders where DeliveryStatus in ( '" +DeliveryStatus+ "')"; 	
					
			}
				
				String supplierid = displayRawMaterialOrderObject.getSupplierid();
				{
					if(supplierid.equals("ALL"))
					 generateQuery += " AND supplierid in ( select supplierid  from RawmaterialOrders )";
				else
				
					generateQuery += " AND supplierid in ( '" + supplierid + "' )";
				}
				
					String startDate = displayRawMaterialOrderObject.getStartdate();  
					String endDate = displayRawMaterialOrderObject.getEndDate(); 
					System.out.println(startDate);
					System.out.println(endDate);
				
		
	
			if(startDate != null && endDate !=null) {
		
//			 ;
//		else
			
			generateQuery += " AND  dateofdelivery BETWEEN '"+ startDate+ "' AND '" +endDate+"'  ";
			}
			
			System.out.println(generateQuery);
		
			RawMaterialService rawmaterialServiceObject = new RawMaterialServiceImpl();
				
			pst = con.prepareStatement(generateQuery);
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
		
		
	}






