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
import com.capgemini.dnd.customexceptions.ExitDateException;
import com.capgemini.dnd.customexceptions.IncompleteDataException;
import com.capgemini.dnd.customexceptions.ProductOrderIDDoesNotExistException;
import com.capgemini.dnd.customexceptions.ProductOrderNotAddedException;
import com.capgemini.dnd.customexceptions.UpdateException;
import com.capgemini.dnd.dto.Address;
import com.capgemini.dnd.dto.DisplayProductOrder;
import com.capgemini.dnd.dto.Distributor;
import com.capgemini.dnd.dto.ProductOrder;
import com.capgemini.dnd.dto.ProductStock;
import com.capgemini.dnd.entity.DistributorEntity;
import com.capgemini.dnd.entity.ProductOrdersEntity;
import com.capgemini.dnd.entity.ProductSpecsEntity;
import com.capgemini.dnd.entity.ProductStockEntity;
import com.capgemini.dnd.entity.DistributorEntity;
import com.capgemini.dnd.entity.WarehouseEntity;

import com.capgemini.dnd.util.DBUtil;
import com.capgemini.dnd.util.HibernateUtil;

@Repository
public class ProductDAOImpl implements ProductDAO {

	// private static final Distributor supplier = null;
	Logger logger = Logger.getRootLogger();

	@Autowired
	private SessionFactory sessionFactory;

	/*******************************************
	 * Product order delivery status update Author: Ankit Kumar
	 * 
	 * 
	 */

	public String updateStatusProductOrder(String orderId, String deliveryStatus) {
		Session session = null;
		Transaction transaction = null;
		try {
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();
			ProductOrdersEntity product = (ProductOrdersEntity) session.get(ProductOrdersEntity.class,
					Integer.parseInt(orderId));
			product.setDeliveryStatus(deliveryStatus);
			session.save(product);
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



	@SuppressWarnings("unused")
	@Override
	public List<ProductOrdersEntity> displayProductOrders(DisplayProductOrder displayProductOrderObject)
			throws DisplayException, BackEndException {
		String hql = "";
		Session session = null;
		Transaction tx = null;
		Criteria cr = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SessionFactory sessionFactory = null;
		List<ProductOrdersEntity> list = new ArrayList<ProductOrdersEntity>();
		PreparedStatement pst = null;
		int isFetched = 0;

		try {
			session = HibernateUtil.getASession();

			tx = session.beginTransaction();
			String deliveryStatus = displayProductOrderObject.getDeliveryStatus();

			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<ProductOrdersEntity> criteria = builder.createQuery(ProductOrdersEntity.class);
			Root<ProductOrdersEntity> root = criteria.from(ProductOrdersEntity.class);

			if (deliveryStatus.equals("ALL")) {

				;
			} else {

				criteria.select(root).where(builder.equal(root.get("deliveryStatus"), deliveryStatus));

			}
			String distributorid = displayProductOrderObject.getDistributorid();

			if (distributorid.equals("ALL"))
				;
			else
				criteria.select(root).where(builder.equal(root.get("distributorId"), distributorid));

			String startDate = displayProductOrderObject.getStartdate();
			String endDate = displayProductOrderObject.getEndDate();

			if (startDate != null && endDate != null) {
				criteria.select(root)
						.where(builder.between(root.get("dateofDelivery"), sdf.parse(startDate), sdf.parse(endDate)));

			}

			Query<ProductOrdersEntity> q = session.createQuery(criteria);
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

			HibernateUtil.closeSession(session);
			// sessionFactory.close();
		}
		return list;

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

		boolean added = false;
		ProductOrdersEntity productOrdersEntity = new ProductOrdersEntity(newPO.getName(), newPO.getDistributorId(),
				newPO.getQuantityValue(), newPO.getQuantityUnit(), newPO.getDateofDelivery(), newPO.getPricePerUnit(),
				newPO.getWarehouseId());
		System.out.println(newPO.getName() + " " +newPO.getDistributorId() + " " +
				newPO.getQuantityValue() + " " +newPO.getQuantityUnit() + " " +newPO.getDateofDelivery() + " " +newPO.getPricePerUnit() + " " +
				newPO.getWarehouseId());
		Session session = null;
		Transaction transaction = null;
		try {
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();
			session.save(productOrdersEntity);
			transaction.commit();
			added = true;
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			session.close();
		}

		if (!added) {
			throw new ProductOrderNotAddedException(Constants.PRODUCT_ORDER_NOT_ADDED);
		}
		return added;
	}

	// ------------------------------------------------------------------------------------------------------------------------------------

	public List<DistributorEntity> fetchDistributorDetail(Distributor distributor)
			throws BackEndException, DoesNotExistException, DisplayException {
		Session session = null;
		Criteria cr = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<DistributorEntity> distributorlist = new ArrayList<DistributorEntity>();
		
		try {
			session = sessionFactory.openSession();
			String distributorId = distributor.getDistributorId();
			CriteriaBuilder builder = session.getCriteriaBuilder();
			CriteriaQuery<DistributorEntity> criteria = builder.createQuery(DistributorEntity.class);
			Root<DistributorEntity> root = criteria.from(DistributorEntity.class);

			criteria.select(root).where(builder.equal(root.get("distributorId"), distributorId));

			Query<DistributorEntity> query = session.createQuery(criteria);
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
//	@Override
//	public List<ProductOrder> displayProductOrders(DisplayProductOrder displayProductOrderObject) throws Exception {
//		List<ProductOrder> poList = new ArrayList<ProductOrder>();
//		PreparedStatement pst = null;
//		Connection con = null;
//		int isFetched = 0;
//		try {
//			con = DBUtil.getInstance().getConnection();
//			String DeliveryStatus = displayProductOrderObject.getDeliveryStatus();
//			String generateQuery = "";
//			{
//				if (DeliveryStatus.equals("ALL"))
//					generateQuery = "SELECT * FROM ProductOrders WHERE deliverystatus in "
//							+ "( select DeliveryStatus from ProductOrders )";
//				else
//					generateQuery = "SELECT * FROM ProductOrders WHERE deliverystatus in ( '" + DeliveryStatus + "')";
//
//			}
//
//			String distributorid = displayProductOrderObject.getDistributorid();
//			{
//				if (distributorid.equals("ALL"))
//					generateQuery += " AND distributorid in ( select distributorid  from ProductOrders )";
//				else
//
//					generateQuery += " AND distributorid in ( '" + distributorid + "' )";
//			}
//
//			String startDate = displayProductOrderObject.getStartdate();
//			String endDate = displayProductOrderObject.getEndDate();
//
//			if (startDate != null && endDate != null)
//
//				generateQuery += " AND  dateofdelivery BETWEEN '" + startDate + "' AND '" + endDate + "'  ";
//			System.out.println(generateQuery);
//			pst = con.prepareStatement(generateQuery);
//			ResultSet rs = pst.executeQuery();
//
//			while (rs.next()) {
//				isFetched = 1;
//				int index = 1;
//				String orderId = Integer.valueOf(rs.getInt(index++)).toString();
//				String name = rs.getString(index++);
//				String productId = rs.getString(index++);
//				String distributorId = rs.getString(index++);
//				double quantityValue = rs.getDouble(index++);
//				String quantityUnit = rs.getString(index++);
//				Date dateOfOrder = rs.getDate(index++);
//				Date dateofDelivery = rs.getDate(index++);
//				double pricePerUnit = rs.getDouble(index++);
//				double totalPrice = rs.getDouble(index++);
//				String deliveryStatus = rs.getString(index++);
//				String warehouseId = rs.getString(index++);
//				poList.add(new ProductOrder(orderId, name, productId, distributorId, quantityValue, quantityUnit,
//						dateOfOrder, dateofDelivery, pricePerUnit, totalPrice, deliveryStatus, warehouseId));
//			}
//			if (isFetched == 0) {
//				logger.error(Constants.LOGGER_ERROR_FETCH_FAILED);
//				throw new DisplayException(Constants.DISPLAY_EXCEPTION_INALID_INPUT);
//			}
//		} catch (SQLException sqlException) {
//			logger.error(sqlException.getMessage());
//			throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_TECHNICAL_PROBLEM);
//		} finally {
//			try {
//				// resultSet.close();
//				pst.close();
//				con.close();
//			} catch (SQLException sqlException) {
//				logger.error(sqlException.getMessage());
//				throw new DisplayException(Constants.DISPLAY_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
//			}
//		}
//		return poList;
//	}

	@Override
	public ArrayList<String> getProductNames() throws DisplayException, ConnectionException {

		ArrayList<String> productNamesList = new ArrayList<String>();
		List<ProductSpecsEntity> productSpecsEntityList;

		Session session = null;
		Transaction transaction = null;

		try {
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();
			String hql = "from ProductSpecsEntity";
			Query query = session.createQuery(hql);
			productSpecsEntityList = query.list();
		} catch (HibernateException exception) {
			logger.error(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
			throw new ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
		} finally {
			session.close();
		}

		for (ProductSpecsEntity productSpecsEntity : productSpecsEntityList) {
			productNamesList.add(productSpecsEntity.getName());
		}

		return productNamesList;
	}

	@Override
	public ArrayList<String> getDistributorIds() throws DisplayException, ConnectionException {

		ArrayList<String> distributorIdsList = new ArrayList<String>();
		List<DistributorEntity> distributorEntityList;

		Session session = null;
		Transaction transaction = null;

		try {
			session = sessionFactory.openSession();
			transaction = session.beginTransaction();
			String hql = "from DistributorEntity";
			Query query = session.createQuery(hql);
			distributorEntityList = query.list();
		} catch (HibernateException exception) {
			logger.error(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
			throw new ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
		} finally {
			session.close();
		}

		for (DistributorEntity distributorEntity : distributorEntityList) {
			distributorIdsList.add(distributorEntity.getDistributorId());
		}

		return distributorIdsList;
	}

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
		} catch (HibernateException exception) {
			logger.error(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
			throw new ConnectionException(Constants.CONNECTION_EXCEPTION_MESSAGE_DBCONNECTION_ERROR);
		} finally {
			session.close();
		}

		for (WarehouseEntity warehouseEntity : warehouseEntityList) {
			warehouseIdsList.add(warehouseEntity.getWarehouseId());
		}

		return warehouseIdsList;
	}

	/*******************************************************************************************************
	 * - Function Name : trackProductOrder - Input Parameters : ProductStock
	 * productStock - Return Type : String - Throws : No exception - Author : Diksha
	 * Gupta, Capgemini - Creation Date : 05/11/2019 - Description : Track a
	 * particular order and calculate its shelf life
	 ********************************************************************************************************/
	@Override
	public String trackProductOrder(ProductStock productStock) {

		Session session = sessionFactory.openSession();

		try {
			ProductStockEntity productStockEntity = session.load(ProductStockEntity.class,
					Integer.parseInt(productStock.getOrderId()));

			Date exitDate = productStockEntity.getExitDate();

			Date manDate = productStockEntity.getManufacturingDate();

			String warehouseId = productStockEntity.getWarehouseId();

			if (exitDate == null || manDate == null) {
				return Constants.INCOMPLETE_INFORMATION_IN_DATABASE;
			}

			String message = "The order ID had been in the warehouse with warehouseID = " + warehouseId + " from "
					+ manDate.toString() + " to " + exitDate.toString() + "("
					+ DBUtil.diffBetweenDays(exitDate, manDate) + " days)";
			session.close();
			return message;

		}

		catch (ObjectNotFoundException exception) {
			session.close();
			return Constants.INCOMPLETE_INFORMATION_IN_DATABASE;
		}

	}

	/*******************************************************************************************************
	 * - Function Name : exitDateCheck - Input Parameters : String orderId - Return
	 * Type : boolean - Throws : ProductOrderIDDoesNotExistException - Author :
	 * Diksha Gupta, Capgemini - Creation Date : 05/11/2019 - Description : Checks
	 * if the Order ID exists in the Orders Table
	 ********************************************************************************************************/
	@Override
	public boolean doesProductOrderIdExist(String orderId) throws ProductOrderIDDoesNotExistException {
		boolean pOrderIdFound = false;
		int oid = -1;
		try {
			oid = Integer.parseInt(orderId);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return pOrderIdFound;
		}

		Session session = sessionFactory.openSession();
		session.beginTransaction();
		@SuppressWarnings("rawtypes")
		Query query = session.createQuery("from ProductOrdersEntity where orderId = :oId");
		query.setParameter("oId", oid);
		if (query.getResultList().size() == 1) {
			pOrderIdFound = true;
			session.close();
			return pOrderIdFound;
		} else {
			logger.error(Constants.PRODUCT_ID_DOES_NOT_EXISTS_EXCEPTION);
			session.close();
			throw new ProductOrderIDDoesNotExistException(Constants.PRODUCT_ID_DOES_NOT_EXISTS_EXCEPTION);
		}

	}

	/*******************************************************************************************************
	 * - Function Name : exitDateCheck - Input Parameters : ProductStock
	 * productStock - Return Type : boolean - Throws : ExitDateException,
	 * IncompleteDataException - Author : Diksha Gupta, Capgemini - Creation Date :
	 * 05/11/2019 - Description : Checks if the exit date entered is valid or not
	 ********************************************************************************************************/
	@Override
	public boolean exitDateCheck(ProductStock productStock) throws ExitDateException, IncompleteDataException {

		Session session = null;
		try {
			boolean datecheck = false;
			session = sessionFactory.openSession();
			session.beginTransaction();

			try {
				ProductStockEntity productStockEntity = session.load(ProductStockEntity.class,
						Integer.parseInt(productStock.getOrderId()));

				Date manufacturingDate = productStockEntity.getManufacturingDate();

				Date expiryDate = productStockEntity.getExpiryDate();

				if (productStock.getExitDate().after(manufacturingDate)
						&& productStock.getExitDate().before(expiryDate)) {
					datecheck = true;
					return datecheck;
				}

			} catch (ObjectNotFoundException exception) {
				session.close();
				throw new IncompleteDataException(Constants.INCOMPLETE_INFORMATION_IN_DATABASE);
			}

			throw new ExitDateException(Constants.EXIT_DATE_EXCEPTION);
		}

		catch (ExitDateException exception) {
			logger.error(exception.getMessage());
			throw exception;

		}

		finally {
			session.close();
		}

	}

	/*******************************************************************************************************
	 * - Function Name : updateExitDateInStock - Input Parameters : ProductStock
	 * productStock - Return Type : String - Throws : No exception - Author : Diksha
	 * Gupta, Capgemini - Creation Date : 05/11/2019 - Description : Updates Details
	 * of Exit Date in Database
	 ********************************************************************************************************/
	@Override
	public String updateExitDateinStock(ProductStock productStock) {

		Session session = sessionFactory.openSession();
		session.beginTransaction();

		ProductStockEntity productStockEntity = session.load(ProductStockEntity.class,
				Integer.parseInt(productStock.getOrderId()));

		productStockEntity.setExitDate(productStock.getExitDate());

		session.save(productStockEntity);
		session.getTransaction().commit();

		if (session.getTransaction() != null && session.getTransaction().isActive()) {
			session.getTransaction().rollback();
		}

		session.close();

		return Constants.DATA_INSERTED_MESSAGE;
	}

	/*******************************************************************************************************
	 * - Function Name : updateProductStock - Input Parameters : ProductStock
	 * productStock - Return Type : String - Throws : No exception - Author : Diksha
	 * Gupta, Capgemini - Creation Date : 05/11/2019 - Description : Updates Details
	 * of Stock in Database
	 ********************************************************************************************************/
	@Override
	public String updateProductStock(ProductStock productStock) {

		Session session = sessionFactory.openSession();
		session.beginTransaction();

		boolean orderIdcheckInStock = false;

		orderIdcheckInStock = doesProductOrderIdExistInStock(productStock.getOrderId());

		if (orderIdcheckInStock == false) {

			String hql = "insert into ProductStockEntity(orderId, name, pricePerUnit, quantityValue, quantityUnit, totalPrice, warehouseId, dateofDelivery)"
					+ " select orderId, name, pricePerUnit, quantityValue, quantityUnit, totalPrice, warehouseId, dateofDelivery from ProductOrdersEntity where orderId = :oId";
			@SuppressWarnings("rawtypes")
			Query q = session.createQuery(hql);
			q.setParameter("oId", Integer.parseInt(productStock.getOrderId()));

			@SuppressWarnings("unused")
			int result = q.executeUpdate();

		}

		ProductStockEntity productStockEntity = session.load(ProductStockEntity.class,
				Integer.parseInt(productStock.getOrderId()));

		productStockEntity.setManufacturingDate(productStock.getManufacturingDate());
		productStockEntity.setExpiryDate(productStock.getExpiryDate());
		productStockEntity.setQualityCheck(productStock.getQualityCheck());

		session.save(productStockEntity);

		session.getTransaction().commit();
		if (session.getTransaction() != null && session.getTransaction().isActive()) {
			session.getTransaction().rollback();
		}
		session.close();
		return Constants.DATA_INSERTED_MESSAGE;

	}

	/*******************************************************************************************************
	 * - Function Name : doesProductOrderExistInStock - Input Parameters : orderId -
	 * Return Type : boolean - Throws : No exception - Author : Diksha Gupta,
	 * Capgemini - Creation Date : 05/11/2019 - Description : Checks if Product
	 * Order ID exists in Stock
	 ********************************************************************************************************/
	@Override
	public boolean doesProductOrderIdExistInStock(String orderId) {

		boolean productOrderIdFound = false;
		int oid = -1;
		try {
			oid = Integer.parseInt(orderId);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return productOrderIdFound;
		}

		Session session = sessionFactory.openSession();
		session.beginTransaction();
		@SuppressWarnings("rawtypes")
		Query query = session.createQuery("from ProductStockEntity where orderId = :oId");
		query.setParameter("oId", oid);
		if (query.getResultList().size() == 1) {
			productOrderIdFound = true;
			session.close();
			return productOrderIdFound;
		} else {
			logger.error(Constants.PRODUCT_ID_DOES_NOT_EXIST_IN_STOCK_EXCEPTION);
			session.close();
			return productOrderIdFound;
		}
	}

	@Override
	public List<ProductOrder> displayProductOrderDetails() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProductOrder> displayPendingProductOrderDetails() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProductOrder> displayCancelledProductOrderDetails() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProductOrder> displayReceivedProductOrderDetails() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProductOrder> displayDispatchedProductOrderDetails() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProductOrder> displayProductOrderbetweenDetails(Date dt1, Date dt2) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProductOrder> displayOrdersFromDistributor(String distId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
