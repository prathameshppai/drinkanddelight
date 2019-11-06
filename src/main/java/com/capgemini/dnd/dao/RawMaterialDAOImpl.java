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
import com.capgemini.dnd.customexceptions.SupplierAddressDoesNotExistsException;
import com.capgemini.dnd.customexceptions.UpdateException;
import com.capgemini.dnd.dto.Address;
import com.capgemini.dnd.dto.DisplayRawMaterialOrder;
import com.capgemini.dnd.dto.Distributor;
import com.capgemini.dnd.dto.RawMaterialOrder;
import com.capgemini.dnd.dto.RawMaterialStock;
import com.capgemini.dnd.dto.Supplier;
import com.capgemini.dnd.entity.SupplierEntity;
import com.capgemini.dnd.entity.SupplierEntity;
import com.capgemini.dnd.entity.RawMaterialOrderEntity;
import com.capgemini.dnd.entity.RawMaterialSpecsEntity;
import com.capgemini.dnd.entity.RawMaterialStockEntity;
import com.capgemini.dnd.entity.SupplierEntity;
import com.capgemini.dnd.entity.WarehouseEntity;
import com.capgemini.dnd.util.DBUtil;
import com.capgemini.dnd.util.HibernateUtil;

@Repository
public class RawMaterialDAOImpl implements RawMaterialDAO {

	Logger logger = Logger.getRootLogger();

	@Autowired
	private SessionFactory sessionFactory;

	public RawMaterialDAOImpl() {
	}

	public String updateStatusRawMaterialOrder(String orderId, String deliveryStatus) {
		Session session = null;
		Transaction transaction = null;
		try {
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();
			RawMaterialOrderEntity rawmaterialorder = (RawMaterialOrderEntity) session.get(RawMaterialOrderEntity.class,
					Integer.parseInt(orderId));
			rawmaterialorder.setDeliveryStatus(deliveryStatus);
			session.save(rawmaterialorder);
			transaction.commit();
			return Constants.UPADTED_SUCCESSFULLY_MESSAGE;
		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			try {
				throw new UpdateException(Constants.UPDATE_EXCEPTION_MESSAGE_FAILURE_DELIVERY);
			} catch (UpdateException ex) {
				return ex.getMessage();
			}
		} finally {
			session.close();
		}
	}

	/*******************************************************************************************************
	 - Function Name	:	addRawMaterialOrder
	 - Input Parameters	:	RawMaterialOrder newRMO
	 - Return Type		:	boolean
	 - Throws			:  	RMOrderNotAddedException, ConnectionException, SQLException, DisplayException 
	 - Author			:	Prathamesh Pai, Capgemini
	 - Creation Date	:	05/11/2019
	 - Description		:	Place Raw Material order 
	 ********************************************************************************************************/
	

	@Override
	public boolean addRawMaterialOrder(RawMaterialOrder newRMO)
			throws RMOrderNotAddedException, ConnectionException, SQLException, DisplayException {

		boolean added = false;
		RawMaterialOrderEntity rawMaterialOrderEntity = new RawMaterialOrderEntity(newRMO.getName(), newRMO.getSupplierId(), newRMO.getQuantityValue(), newRMO.getQuantityUnit(), newRMO.getDateOfDelivery(), newRMO.getPricePerUnit(), newRMO.getWarehouseId());
		Session session=null;
		Transaction transaction = null;
		try {
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();
		    session.save(rawMaterialOrderEntity);
			transaction.commit();
			added = true;
		} catch (HibernateException e) {
			e.printStackTrace();
			System.out.println("Exception 638");
		}
		finally {
			session.close();
		}
		if (!added) {
			throw new RMOrderNotAddedException(Constants.RM_ORDER_NOT_ADDED);
		}
		return added;

	}

	public List<SupplierEntity> fetchSupplierDetail(Supplier supplierDetails) throws BackEndException, DoesNotExistException, DisplayException {
		Session session = null;
		Criteria cr = null;
		List<SupplierEntity> supplierlist = new ArrayList<SupplierEntity>();
		PreparedStatement pst = null;
		
		try {
         session = sessionFactory.openSession();
			session.beginTransaction();

			String supplierId = supplierDetails.getSupplierId();
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<SupplierEntity> criteria = builder.createQuery(SupplierEntity.class);
			Root<SupplierEntity> root = criteria.from(SupplierEntity.class);

			criteria.select(root).where(builder.equal(root.get("supplierId"), supplierId));

			Query<SupplierEntity> query = session.createQuery(criteria);
			supplierlist = query.list();
			System.out.println(supplierlist);
			if (supplierlist.isEmpty()) {
				logger.error(Constants.LOGGER_ERROR_FETCH_FAILED);
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_NO_RECORDS_FOUND);

			} else {
				logger.info(Constants.LOGGER_INFO_DISPLAY_SUCCESSFUL);

			}
		} catch (Exception e) {

			e.printStackTrace();
			throw new DisplayException(Constants.DISPLAY_EXCEPTION_NO_RECORDS_FOUND);
		}

		finally {

			session.close();
		}
		return supplierlist;

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

	@SuppressWarnings("unused")
	@Override
	public List<RawMaterialOrderEntity> displayRawmaterialOrders(DisplayRawMaterialOrder displayRawMaterialOrderObject)
			throws DisplayException, BackEndException {

		Session session = null;
		Criteria cr = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<RawMaterialOrderEntity> list = new ArrayList<RawMaterialOrderEntity>();
		PreparedStatement pst = null;
		int isFetched = 0;

		try {

			session = sessionFactory.openSession();
			session.beginTransaction();

			String deliveryStatus = displayRawMaterialOrderObject.getDeliveryStatus();

			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<RawMaterialOrderEntity> criteria = builder.createQuery(RawMaterialOrderEntity.class);
			Root<RawMaterialOrderEntity> root = criteria.from(RawMaterialOrderEntity.class);

			if (deliveryStatus.equals("ALL")) {

				;
			} else {

				criteria.select(root).where(builder.equal(root.get("deliveryStatus"), deliveryStatus));

			}
			String supplierId = displayRawMaterialOrderObject.getSupplierid();

			if (supplierId.equals("ALL"))
				;
			else
				criteria.select(root).where(builder.equal(root.get("supplierId"), supplierId));

			String startDate = displayRawMaterialOrderObject.getStartdate();
			String endDate = displayRawMaterialOrderObject.getEndDate();

			if (startDate != null && endDate != null) {
				criteria.select(root)
						.where(builder.between(root.get("dateOfDelivery"), sdf.parse(startDate), sdf.parse(endDate)));

			}

			Query<RawMaterialOrderEntity> q = session.createQuery(criteria);
			list = q.list();

			if (list.isEmpty()) {
				logger.error(Constants.LOGGER_ERROR_FETCH_FAILED);
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_NO_RECORDS_FOUND);

			} else {
				logger.info(Constants.LOGGER_INFO_DISPLAY_SUCCESSFUL);

			}
		} catch (Exception e) {

			e.printStackTrace();
			throw new DisplayException(Constants.DISPLAY_EXCEPTION_NO_RECORDS_FOUND);
		}

		finally {

			session.close();
		}
		return list;

	}


	/*******************************************************************************************************
	 - Function Name	:	getRawMaterialNames
	 - Input Parameters	:	none
	 - Return Type		:	ArrayList
	 - Throws			:  	DisplayException, ConnectionException  
	 - Author			:	Prathamesh Pai, Capgemini
	 - Creation Date	:	05/11/2019
	 - Description		:	Get a list of Raw Material Names 
	 ********************************************************************************************************/
	
	@Override
	public ArrayList<String> getRawMaterialNames() throws DisplayException, ConnectionException {

		ArrayList<String> rawMaterialNamesList = new ArrayList<String>();
		List<RawMaterialSpecsEntity> rawMaterialSpecsEntityList;// = new ArrayList<RawMaterialSpecsEntity>();
		
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();
			String hql = "from RawMaterialSpecsEntity";
			Query query = session.createQuery(hql);
			rawMaterialSpecsEntityList = query.list();
		} 
		catch(HibernateException exception) {
			logger.error(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
			throw new ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
		}
		finally {
			session.close();
		}
		
		for(RawMaterialSpecsEntity rawMaterialSpecsEntity : rawMaterialSpecsEntityList) {
			rawMaterialNamesList.add(rawMaterialSpecsEntity.getName());
		}
		
		return rawMaterialNamesList;
	}

	/*******************************************************************************************************
	 - Function Name	:	getSupplierIds
	 - Input Parameters	:	none
	 - Return Type		:	ArrayList
	 - Throws			:  	DisplayException, ConnectionException  
	 - Author			:	Prathamesh Pai, Capgemini
	 - Creation Date	:	05/11/2019
	 - Description		:	Get a list of Supplier IDs
	 ********************************************************************************************************/

	
	@Override
	public ArrayList<String> getSupplierIds() throws DisplayException, ConnectionException {

		ArrayList<String> supplierIdsList = new ArrayList<String>();
		List<SupplierEntity> supplierEntityList;
		
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();
			String hql = "from SupplierEntity";
			Query query = session.createQuery(hql);
			supplierEntityList = query.list();
		} 
		catch(HibernateException exception) {
			logger.error(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
			throw new ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
		}
		finally {
			session.close();
		}
		
		for(SupplierEntity supplierEntity : supplierEntityList) {
			supplierIdsList.add(supplierEntity.getSupplierId());
		}
		
		return supplierIdsList;
	}

	/*******************************************************************************************************
	 - Function Name	:	getWarehouseIds
	 - Input Parameters	:	none
	 - Return Type		:	ArrayList
	 - Throws			:  	DisplayException, ConnectionException  
	 - Author			:	Prathamesh Pai, Capgemini
	 - Creation Date	:	05/11/2019
	 - Description		:	Get a list of Warehouse IDs
	 ********************************************************************************************************/
	
	@Override
	public ArrayList<String> getWarehouseIds() throws DisplayException, ConnectionException {

		ArrayList<String> warehouseIdsList = new ArrayList<String>();
		List<WarehouseEntity> warehouseEntityList;// = new ArrayList<RawMaterialSpecsEntity>();
		
		Session session = null;
		Transaction transaction = null;
		
		try {
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();
			String hql = "from WarehouseEntity";
			Query query = session.createQuery(hql);
			warehouseEntityList = query.list();
		} 
		catch(HibernateException exception) {
			logger.error(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
			throw new ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
		}
		finally {
			session.close();
		}
		
		for(WarehouseEntity warehouseEntity : warehouseEntityList) {
			warehouseIdsList.add(warehouseEntity.getWarehouseId());
		}
		
		return warehouseIdsList;
	}

	
	/*******************************************************************************************************
	 - Function Name	:	trackRawMaterialOrder
	 - Input Parameters	:	RawMaterialStock rawMaterialStock
	 - Return Type		:	String
	 - Throws			:  	No exception
	 - Author			:	Gaurav Gaikwad, Capgemini
	 - Creation Date	:	05/11/2019
	 - Description		:	Track a particular order and calculate its shelf life 
	 ********************************************************************************************************/
	@Override
	public String trackRawMaterialOrder(RawMaterialStock rawMaterialStock) {


        Session session = sessionFactory.openSession();
        session.beginTransaction();
		
		
		try {
        RawMaterialStockEntity rmStockEntity = session.load(RawMaterialStockEntity.class, Integer.parseInt(rawMaterialStock.getOrderId()));

	      
	      				Date processDate = rmStockEntity.getProcessDate();
	      
	      				Date deliveryDate = rmStockEntity.getDateofDelivery();
	      
	      				String warehouseId = rmStockEntity.getWarehouseId();
	      
	      			if(processDate == null || deliveryDate == null) {
	      				return Constants.INCOMPLETE_INFORMATION_IN_DATABASE;
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

	/*******************************************************************************************************
	 - Function Name	:	doesRawMaterialOrderIdExist
	 - Input Parameters	:	String orderId
	 - Return Type		:	boolean
	 - Throws			:  	RMOrderIDDoesNotExistException
	 - Author			:	Gaurav Gaikwad, Capgemini
	 - Creation Date	:	05/11/2019
	 - Description		:	Checks if the Order ID exists in the Orders Table
	 ********************************************************************************************************/
	@Override
	public boolean doesRawMaterialOrderIdExist(String orderId) throws RMOrderIDDoesNotExistException {
		boolean rmOrderIdFound = false;
		int oid = -1;
		try {
			oid = Integer.parseInt(orderId);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return rmOrderIdFound;
		}

		Session session = sessionFactory.openSession();
		session.beginTransaction();
		@SuppressWarnings("rawtypes")
		Query query = session.createQuery("from RawMaterialOrderEntity where orderId = :oId");
		query.setParameter("oId", oid);
		if (query.getResultList().size() == 1) {
			rmOrderIdFound = true;
			session.close();
			return rmOrderIdFound;
		} else {
			logger.error(Constants.RAWMATERIAL_ID_DOES_NOT_EXISTS_EXCEPTION);
			session.close();
			throw new RMOrderIDDoesNotExistException(Constants.RAWMATERIAL_ID_DOES_NOT_EXISTS_EXCEPTION);
		}

	}

	/*******************************************************************************************************
	 - Function Name	:	processDateCheck
	 - Input Parameters	:	RawMaterialStock rawMaterialStock
	 - Return Type		:	boolean
	 - Throws			:  	ProcessDateException, IncompleteDataException
	 - Author			:	Gaurav Gaikwad, Capgemini
	 - Creation Date	:	05/11/2019
	 - Description		:	Checks if the process date entered is valid or not
	 ********************************************************************************************************/
	@Override
	public boolean processDateCheck(RawMaterialStock rawMaterialStock)
			throws ProcessDateException, IncompleteDataException {

		Session session = null;

		try {
			boolean datecheck = false;
			session = sessionFactory.openSession();
			session.beginTransaction();

	


	        try {
	        RawMaterialStockEntity rmStockEntity = session.load(RawMaterialStockEntity.class, Integer.parseInt(rawMaterialStock.getOrderId()));
	        
		      Date manufacturingDate = rmStockEntity.getManufacturingDate();
		      
		      Date expiryDate = rmStockEntity.getExpiryDate();
		      
		      				if (rawMaterialStock.getProcessDate().after(manufacturingDate)
		      						&& rawMaterialStock.getProcessDate().before(expiryDate)) {
		      					datecheck = true;
		      					return datecheck;
		      				}
		      
		      				else {
		      					logger.error(Constants.PROCESS_DATE_EXCEPTION_MESSAGE);
		      					throw new ProcessDateException(Constants.PROCESS_DATE_EXCEPTION_MESSAGE);
		      				}
		}
		catch(ObjectNotFoundException exception) {
			logger.error(Constants.PROCESS_DATE_EXCEPTION_MESSAGE);
			session.close();
			throw new IncompleteDataException(Constants.INCOMPLETE_INFORMATION_IN_DATABASE);
		}
		      			
		      
		      		} 
		      
		      		catch (ProcessDateException exception) {
		      			logger.error(Constants.PROCESS_DATE_EXCEPTION_MESSAGE);
		      			throw exception;
		      
		      		}
		finally {
			session.close();
		}

	}

	/*******************************************************************************************************
	 - Function Name	:	updateProcessDateInStock
	 - Input Parameters	:	RawMaterialStock rawMaterialStock
	 - Return Type		:	String
	 - Throws			:  	No exception
	 - Author			:	Gaurav Gaikwad, Capgemini
	 - Creation Date	:	05/11/2019
	 - Description		:	Updates Details of Process Date in Database 
	 ********************************************************************************************************/
	@Override
	public String updateProcessDateinStock(RawMaterialStock rawMaterialStock) {


		Session session = sessionFactory.openSession(); 
        session.beginTransaction();
        RawMaterialStockEntity rmStockEntity = session.load(RawMaterialStockEntity.class, Integer.parseInt(rawMaterialStock.getOrderId()));
       
	      rmStockEntity.setProcessDate(rawMaterialStock.getProcessDate());
	      session.save(rmStockEntity);
	      

	      
	      session.getTransaction().commit();
	      if (session.getTransaction() != null && session.getTransaction().isActive()) {
				 session.getTransaction().rollback();
			}
	      session.close();
	      return Constants.DATA_INSERTED_MESSAGE;


	}

	/*******************************************************************************************************
	 - Function Name	:	updateRawMaterialStock
	 - Input Parameters	:	RawMaterialStock rawMaterialStock
	 - Return Type		:	String
	 - Throws			:  	No exception
	 - Author			:	Gaurav Gaikwad, Capgemini
	 - Creation Date	:	05/11/2019
	 - Description		:	Updates Details of Stock in Database 
	 ********************************************************************************************************/
	@Override
	public String updateRawMaterialStock(RawMaterialStock rawMaterialStock) {

		
		Session session = sessionFactory.openSession(); 
        session.beginTransaction();
        
        boolean orderIdcheckInStock = false;

		orderIdcheckInStock = doesRawMaterialOrderIdExistInStock(rawMaterialStock.getOrderId());
		
		if (orderIdcheckInStock == false) {

			String hql = "insert into RawMaterialStockEntity(orderId, name, pricePerUnit, quantityValue, quantityUnit, totalPrice, warehouseId, dateofDelivery)" +  " select orderId, name, pricePerUnit, quantityValue, quantityUnit, totalPrice, warehouseId, dateOfDelivery from RawMaterialOrderEntity where orderId = :oId";
			@SuppressWarnings("rawtypes")
			Query q = session.createQuery(hql);
		      q.setParameter("oId", Integer.parseInt(rawMaterialStock.getOrderId()));
			
			@SuppressWarnings("unused")

			int result = q.executeUpdate();
			
		}

		
		RawMaterialStockEntity rmStockEntity = session.load(RawMaterialStockEntity.class, Integer.parseInt(rawMaterialStock.getOrderId()));

		rmStockEntity.setManufacturingDate(rawMaterialStock.getManufacturingDate());
		rmStockEntity.setExpiryDate(rawMaterialStock.getExpiryDate());
		rmStockEntity.setQualityCheck(rawMaterialStock.getQualityCheck());

		session.save(rmStockEntity);

		session.getTransaction().commit();
		if (session.getTransaction() != null && session.getTransaction().isActive()) {
				 session.getTransaction().rollback();
			}
			session.close();
		    return Constants.DATA_INSERTED_MESSAGE;
       


	}

	/*******************************************************************************************************
	 - Function Name	:	doesRawMaterialOrderExistInStock
	 - Input Parameters	:	orderId
	 - Return Type		:	boolean
	 - Throws			:  	No exception
	 - Author			:	Gaurav Gaikwad, Capgemini
	 - Creation Date	:	05/11/2019
	 - Description		:	Checks if Raw Material Order ID exists in Stock
	 ********************************************************************************************************/
	@Override
	public boolean doesRawMaterialOrderIdExistInStock(String orderId) {

		boolean rmOrderIdFound = false;
		int oid = -1;
		try {
			oid = Integer.parseInt(orderId);
		} catch (Exception e) {
			return rmOrderIdFound;
		}

		Session session = sessionFactory.openSession();
		session.beginTransaction();
		@SuppressWarnings("rawtypes")
		Query query = session.createQuery("from RawMaterialStockEntity where orderId = :oId");
		query.setParameter("oId", oid);
		if (query.getResultList().size() == 1) {
			rmOrderIdFound = true;
			session.close();
			return rmOrderIdFound;
		} else {
			logger.error(Constants.RAWMATERIAL_ID_DOES_NOT_EXIST_IN_STOCK_EXCEPTION);
			session.close();
			return rmOrderIdFound;
		}

	}
	public List<SupplierEntity> fetchSupplierDetail(Distributor distributor)
			throws BackEndException, DoesNotExistException, DisplayException {
		Session session = null;
		Criteria cr = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<SupplierEntity> distributorlist = new ArrayList<SupplierEntity>();
		PreparedStatement pst = null;
		int isFetched = 0;

		try {
			System.out.println("hello");

			session = sessionFactory.openSession();
			session.beginTransaction();

			String distributorId = distributor.getDistributorId();
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<SupplierEntity> criteria = builder.createQuery(SupplierEntity.class);
			Root<SupplierEntity> root = criteria.from(SupplierEntity.class);

			criteria.select(root).where(builder.equal(root.get("distributorId"), distributorId));

			Query<SupplierEntity> query = session.createQuery(criteria);
			distributorlist = query.list();
			System.out.println(distributorlist);
			if (distributorlist.isEmpty()) {
				logger.error(Constants.LOGGER_ERROR_FETCH_FAILED);
				throw new DisplayException(Constants.DISPLAY_EXCEPTION_NO_RECORDS_FOUND);

			} else {
				logger.info(Constants.LOGGER_INFO_DISPLAY_SUCCESSFUL);

			}
		} catch (Exception e) {

			e.printStackTrace();
			throw new DisplayException(Constants.DISPLAY_EXCEPTION_NO_RECORDS_FOUND);
		}

		finally {

			session.close();
		}
		return distributorlist;

	}



}
